package app.surgo.common.compose.utils

import androidx.annotation.FloatRange

fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Float {
    return startValue + fraction * (endValue - startValue)
}

fun lerp(
    startValue: Float,
    endValue: Float,
    @FloatRange(from = 0.0, to = 1.0) startFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) endFraction: Float,
    @FloatRange(from = 0.0, to = 1.0) fraction: Float
): Float {
    if (fraction < startFraction) return startValue
    if (fraction > endFraction) return endValue

    return lerp(
        startValue,
        endValue,
        (fraction - startFraction) / (endFraction - startFraction)
    )
}
