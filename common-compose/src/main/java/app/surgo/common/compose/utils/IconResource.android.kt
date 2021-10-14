package app.surgo.common.compose.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext

@Composable
fun iconResource(packageName: String): ImageBitmap {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val drawable = packageManager.getApplicationIcon(packageName)

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
        drawable is AdaptiveIconDrawable -> {
            /*
            val layer = LayerDrawable(arrayOf(drawable.background, drawable.foreground))
            val width = layer.intrinsicWidth
            val height = layer.intrinsicHeight
            */
            val width = drawable.intrinsicWidth
            val height = drawable.intrinsicHeight
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            bitmap.asImageBitmap()
        }
        else -> {
            (drawable as BitmapDrawable).bitmap.asImageBitmap()
        }
    }
}
