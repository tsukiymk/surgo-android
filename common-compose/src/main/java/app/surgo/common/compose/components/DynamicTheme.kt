package app.surgo.common.compose.components

import android.content.Context
import android.util.LruCache
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import coil.Coil
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.size.Scale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * A class which stores and caches the result of any calculated dominant colors
 * from images.
 *
 * @param context Android context
 * @param defaultColor The default color, which will be used if [calculateDominantColor] fails to
 * calculate a dominant color
 * @param defaultOnColor The default foreground 'on color' for [defaultColor].
 * @param cacheSize The size of the [LruCache] used to store recent results. Pass `0` to
 * disable the cache.
 * @param isColorValid A lambda which allows filtering of the calculated image colors.
 */
@Stable
class DomainColorState(
    private val context: Context,
    private val defaultColor: Color,
    private val defaultOnColor: Color,
    cacheSize: Int = 12,
    private val isColorValid: (Color) -> Boolean = { true }
) {
    var color by mutableStateOf(defaultColor)
        private set
    var onColor by mutableStateOf(defaultOnColor)
        private set

    private val cache = when {
        cacheSize > 0 -> LruCache<String, DominantColors>(cacheSize)
        else -> null
    }

    suspend fun updateColorsFromImageUrl(url: String) {
        val result = calculateDominantColor(url)
        color = result?.color ?: defaultColor
        onColor = result?.onColor ?: defaultOnColor
    }

    private suspend fun calculateDominantColor(url: String): DominantColors? {
        val cached = cache?.get(url)
        if (cached != null) {
            return cached
        }

        return calculateSwatchesInImage(context, url)
            .sortedByDescending { swatch -> swatch.population }
            .firstOrNull { swatch -> isColorValid(Color(swatch.rgb)) }
            ?.let { swatch ->
                DominantColors(
                    color = Color(swatch.rgb),
                    onColor = Color(swatch.bodyTextColor).copy(alpha = 1f)
                )
            }
            ?.also { result -> cache?.put(url, result) }
    }

    fun reset() {
        color = defaultColor
        onColor = defaultOnColor
    }
}

@Composable
fun rememberDominantColorState(
    context: Context = LocalContext.current,
    defaultColor: Color = MaterialTheme.colors.surface,
    defaultOnColor: Color = MaterialTheme.colors.onSurface,
    cacheSize: Int = 12,
    isColorValid: (Color) -> Boolean = { true }
): DomainColorState = remember {
    DomainColorState(context, defaultColor, defaultOnColor, cacheSize, isColorValid)
}

/**
 * A composable which allows dynamic theming of the [androidx.compose.material.Colors.primary]
 * color from an image.
 */
@Composable
fun DynamicThemePrimaryColorsFromImage(
    dominantColorState: DomainColorState = rememberDominantColorState(),
    content: @Composable () -> Unit
) {
    val color = animateColorAsState(
        dominantColorState.color,
        spring(stiffness = Spring.StiffnessLow)
    ).value
    val colors = MaterialTheme.colors.copy(
        primary = color,
        onPrimary = color
    )

    MaterialTheme(colors = colors, content = content)
}

@Immutable
private data class DominantColors(val color: Color, val onColor: Color)

/**
 * Fetches the given [imageUrl] with [Coil], then uses [Palette] to calculate the dominant color.
 */
private suspend fun calculateSwatchesInImage(
    context: Context,
    imageUrl: String
): List<Palette.Swatch> {
    val request = ImageRequest.Builder(context)
        .data(imageUrl)
        .size(128).scale(Scale.FILL)
        .allowHardware(false)
        .build()
    val bitmap = when (val result = Coil.execute(request)) {
        is SuccessResult -> result.drawable.toBitmap()
        else -> null
    }

    return bitmap?.let {
        withContext(Dispatchers.Default) {
            val palette = Palette.Builder(bitmap)
                .resizeBitmapArea(0)
                .maximumColorCount(16)
                .generate()

            palette.swatches
        }
    } ?: emptyList()
}
