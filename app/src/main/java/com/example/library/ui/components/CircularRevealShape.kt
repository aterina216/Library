package com.example.library.ui.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.hypot

class CircularRevealShape(
    private val progress: Float,
    private val offset: Offset? = null
): Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val radius = calculateRadius(size, offset) * progress
        val centerX = offset?.x ?: (size.width / 2f)
        val centerY = offset?.y ?: (size.height / 2f)

        return Outline.Generic(
            path = Path().apply {
                addOval(
                    Rect(
                        left = centerX - radius,
                        top = centerY - radius,
                        right = centerX + radius,
                        bottom = centerY + radius
                    )
                )
            }
        )
    }

    private fun calculateRadius(size: Size, offset: Offset?): Float {
        if(offset == null) {
            return hypot(size.width/2f, size.height/2f)
        }

        val topLeft = hypot(offset.x, offset.y)
        val topRight = hypot(size.width - offset.x, offset.y)
        val bottomLeft = hypot(offset.x, size.height - offset.y)
        val bottomRight = hypot(size.width - offset.x, size.height - offset.y)
        return maxOf(topLeft, topRight, bottomLeft, bottomRight)
    }
}

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.circularReveal(
    isRevealed: Boolean,
    offset: Offset? = null,
    animationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 400)
): Modifier = composed {

    val progress by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0f,
        animationSpec = animationSpec,
        label = "CircularRevealProgress"
    )
    this.clip(CircularRevealShape(progress = progress, offset = offset))
}

fun Modifier.captureTouchPoint(
    capturedOffset: MutableState<Offset?>
): Modifier = pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            val touchPoint = event.changes.first().position
            if (event.changes.first().pressed) {
                capturedOffset.value = touchPoint
            }
        }
    }
}