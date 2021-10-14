package app.surgo.common.compose.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

enum class BottomSheetLayoutValue {
    Hidden,
    Collapsed,
    Expanded
}

@ExperimentalMaterialApi
@Stable
class BottomSheetLayoutState(
    initialValue: BottomSheetLayoutValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BottomSheetLayoutValue) -> Boolean = { true }
) : SwipeableState<BottomSheetLayoutValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    val isExpanded: Boolean
        get() = currentValue == BottomSheetLayoutValue.Expanded

    val isCollapsed: Boolean
        get() = currentValue == BottomSheetLayoutValue.Collapsed

    suspend fun expand() = animateTo(BottomSheetLayoutValue.Expanded)

    suspend fun collapse() = animateTo(BottomSheetLayoutValue.Collapsed)

    companion object {
        @Suppress("FunctionName")
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmStateChange: (BottomSheetLayoutValue) -> Boolean
        ): Saver<BottomSheetLayoutState, *> = Saver(
            save = { it.currentValue },
            restore = {
                BottomSheetLayoutState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    confirmStateChange = confirmStateChange
                )
            }
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun rememberBottomSheetLayoutState(
    initialValue: BottomSheetLayoutValue = BottomSheetLayoutValue.Collapsed,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BottomSheetLayoutValue) -> Boolean = { true }
): BottomSheetLayoutState {
    return rememberSaveable(
        animationSpec,
        saver = BottomSheetLayoutState.Saver(
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    ) {
        BottomSheetLayoutState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange
        )
    }
}

@ExperimentalMaterialApi
@Composable
fun BottomSheetLayout(
    modifier: Modifier = Modifier,
    bottomBar: (@Composable () -> Unit)? = null,
    sheetState: BottomSheetLayoutState = rememberBottomSheetLayoutState(),
    sheetShape: Shape = MaterialTheme.shapes.large,
    sheetElevation: Dp = BottomSheetLayoutDefaults.SheetElevation,
    sheetBackgroundColor: Color = MaterialTheme.colors.surface,
    sheetContentColor: Color = contentColorFor(sheetBackgroundColor),
    sheetPeekHeight: Dp = BottomSheetLayoutDefaults.SheetPeekHeight,
    sheetContent: @Composable ((Float) -> Unit)? = null,
    bodyContent: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier) {
        val fullHeight = constraints.maxHeight.toFloat()
        val peekHeightPx = with(LocalDensity.current) { sheetPeekHeight.toPx() }
        val dragRange = fullHeight - peekHeightPx

        var bottomSheetHeight by remember { mutableStateOf(fullHeight) }

        val swipeable = Modifier
            .swipeable(
                state = sheetState,
                anchors = mapOf(
                    fullHeight to BottomSheetLayoutValue.Hidden,
                    fullHeight - peekHeightPx to BottomSheetLayoutValue.Collapsed,
                    fullHeight - bottomSheetHeight to BottomSheetLayoutValue.Expanded
                ),
                orientation = Orientation.Vertical,
                enabled = true,
                resistance = null
            )
            .semantics {
                if (peekHeightPx != bottomSheetHeight) {
                    if (sheetState.isCollapsed) {
                        expand {
                            scope.launch { sheetState.expand() }
                            true
                        }
                    } else {
                        collapse {
                            scope.launch { sheetState.collapse() }
                            true
                        }
                    }
                }
            }

        // FIXME: the uninitialized state is not `Float.NaN`.
        val initializedFlag = rememberSaveable { mutableStateOf(false) }
        val openFraction = if (sheetState.offset.value.isNaN() || !initializedFlag.value) {
            initializedFlag.value = true

            0f
        } else {
            (dragRange - sheetState.offset.value) / dragRange
        }.coerceIn(0f, 1f)

        SubcomposeLayout { constraints ->
            val layoutWidth = constraints.maxWidth
            val layoutHeight = constraints.maxHeight

            val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

            layout(layoutWidth, layoutHeight) {
                var bottomBarHeight = 0
                val bottomBarPlaceables = bottomBar?.let { content ->
                    val placeables = subcompose(
                        slotId = BottomSheetLayoutContent.BottomBar,
                        content = content
                    ).fastMap { it.measure(looseConstraints) }

                    bottomBarHeight = placeables.fastMaxBy { it.height }?.height ?: 0

                    placeables
                }

                val sheetContentPlaceables = subcompose(BottomSheetLayoutContent.SheetContent) {
                    Surface(
                        modifier = swipeable.requiredHeightIn(min = sheetPeekHeight)
                            .onGloballyPositioned {
                                bottomSheetHeight = it.size.height.toFloat()
                            },
                        shape = sheetShape,
                        elevation = sheetElevation,
                        color = sheetBackgroundColor,
                        contentColor = sheetContentColor,
                        content = { sheetContent?.invoke(openFraction) }
                    )
                }.fastMap { it.measure(looseConstraints) }

                val bodyContentPlaceables = subcompose(BottomSheetLayoutContent.MainContent) {
                    bodyContent(PaddingValues(bottom = sheetPeekHeight))
                }.fastMap { it.measure(looseConstraints) }

                bodyContentPlaceables.fastForEach {
                    it.place(0, 0)
                }
                sheetContentPlaceables.fastForEach {
                    val bottomSheetOffsetY = sheetState.offset.value.roundToInt()

                    it.place(0, bottomSheetOffsetY)
                }
                bottomBarPlaceables?.fastForEach {
                    val bottomBarOffsetY = (bottomBarHeight * (1 - openFraction)).roundToInt()

                    it.place(0, layoutHeight - bottomBarOffsetY)
                }
            }
        }
    }
}

/**
 * Contains useful defaults for [BottomSheetLayout].
 */
object BottomSheetLayoutDefaults {
    /**
     * The default elevation used by [BottomSheetLayout].
     */
    val SheetElevation = 8.dp

    /**
     * The default peek height used by [BottomSheetLayout].
     */
    val SheetPeekHeight = 56.dp
}

private enum class BottomSheetLayoutContent {
    MainContent,
    SheetContent,
    BottomBar
}
