/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.google.accompanist.swiperefresh

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A class to encapsulate details of different indicator sizes.
 *
 * @param size The overall size of the indicator.
 * @param arcRadius The radius of the arc.
 * @param strokeWidth The width of the arc stroke.
 * @param arrowWidth The width of the arrow.
 * @param arrowHeight The height of the arrow.
 */
@Immutable
private data class SwipeRefreshIndicatorSizes(
    val size: Dp,
    val arcRadius: Dp,
    val strokeWidth: Dp,
    val arrowWidth: Dp,
    val arrowHeight: Dp,
)

/**
 * The default/normal size values for [SwipeRefreshIndicator].
 */
private val DefaultSizes = SwipeRefreshIndicatorSizes(
    size = 40.dp,
    arcRadius = 7.5.dp,
    strokeWidth = 2.5.dp,
    arrowWidth = 10.dp,
    arrowHeight = 5.dp,
)

/**
 * The 'large' size values for [SwipeRefreshIndicator].
 */
private val LargeSizes = SwipeRefreshIndicatorSizes(
    size = 56.dp,
    arcRadius = 11.dp,
    strokeWidth = 3.dp,
    arrowWidth = 12.dp,
    arrowHeight = 6.dp,
)

/**
 * A version of [SwipeRefreshIndicator] which reads the appropriate values for the `isRefreshing`,
 * `offset` and `triggerOffset` from the provided [state].
 *
 * @param state The [SwipeRefreshState] passed into the [SwipeRefresh] `indicator` block.
 * @param modifier The modifier to apply to this layout.
 * @param scale Whether the indicator should scale up/down as it is scrolled in. Defaults to false.
 * @param arrowEnabled Whether an arrow should be drawn on the indicator. Defaults to true.
 * @param backgroundColor The color of the indicator background surface.
 * @param contentColor The color for the indicator's contents.
 * @param shape The shape of the indicator background surface. Defaults to [CircleShape].
 * @param largeIndication Whether the indicator should be 'large' or not. Defaults to false.
 * @param elevation The size of the shadow below the indicator.
 */
@Composable
inline fun SwipeRefreshIndicator(
    state: SwipeRefreshState,
    modifier: Modifier = Modifier,
    scale: Boolean = false,
    arrowEnabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    largeIndication: Boolean = false,
    elevation: Dp = 4.dp,
) {
    SwipeRefreshIndicator(
        isRefreshing = state.isRefreshing,
        offset = state.indicatorOffset,
        refreshOffset = state.indicatorRefreshOffset,
        modifier = modifier,
        scale = scale,
        arrowEnabled = arrowEnabled,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        shape = shape,
        largeIndication = largeIndication,
        elevation = elevation,
    )
}

/**
 * Indicator composable which is typically used in conjunction with [SwipeRefresh].
 *
 * @param isRefreshing Whether the indicator should display an indeterminate progress indicator.
 * @param offset The current scroll offset, in pixels.
 * @param refreshOffset The scroll offset which would trigger a refresh, in pixels.
 * @param modifier The modifier to apply to this layout.
 * @param scale Whether the indicator should scale up/down as it is scrolled in. Defaults to false.
 * @param arrowEnabled Whether an arrow should be drawn on the indicator. Defaults to true.
 * @param backgroundColor The color of the indicator background surface.
 * @param contentColor The color for the indicator's contents.
 * @param shape The shape of the indicator background surface. Defaults to [CircleShape].
 * @param largeIndication Whether the indicator should be 'large' or not. Defaults to false.
 * @param elevation The size of the shadow below the indicator.
 */
@Composable
fun SwipeRefreshIndicator(
    isRefreshing: Boolean,
    offset: Float,
    refreshOffset: Float,
    modifier: Modifier = Modifier,
    scale: Boolean = false,
    arrowEnabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    largeIndication: Boolean = false,
    elevation: Dp = 4.dp,
) {
    val adjustedElevation = when (offset) {
        0f -> 0.dp
        else -> elevation
    }
    val animatedAlpha by animateFloatAsState(
        targetValue = if (offset >= refreshOffset) MaxAlpha else MinAlpha,
        animationSpec = tween()
    )

    val sizes = if (largeIndication) LargeSizes else DefaultSizes

    Surface(
        modifier = modifier
            .size(size = sizes.size)
            .graphicsLayer {
                val scaleFraction = if (scale) {
                    // We use LinearOutSlowInEasing to speed up the scale in
                    LinearOutSlowInEasing
                        .transform(offset / refreshOffset)
                        .coerceIn(0f, 1f)
                } else 1f

                scaleX = scaleFraction
                scaleY = scaleFraction
            },
        shape = shape,
        color = backgroundColor,
        elevation = adjustedElevation
    ) {
        val painter = remember { CircularProgressPainter() }
        painter.arcRadius = sizes.arcRadius
        painter.strokeWidth = sizes.strokeWidth
        painter.arrowWidth = sizes.arrowWidth
        painter.arrowHeight = sizes.arrowHeight
        painter.arrowEnabled = arrowEnabled && !isRefreshing
        painter.color = contentColor
        painter.alpha = animatedAlpha

        val slingshot = calculateSlingshot(
            offsetY = offset,
            maxOffsetY = refreshOffset,
            height = with(LocalDensity.current) { sizes.size.roundToPx() }
        )
        painter.startTrim = slingshot.startTrim
        painter.endTrim = slingshot.endTrim
        painter.rotation = slingshot.rotation
        painter.arrowScale = slingshot.arrowScale

        // This shows either an Image with CircularProgressPainter or a CircularProgressIndicator,
        // depending on refresh state
        Crossfade(
            targetState = isRefreshing,
            animationSpec = tween(durationMillis = CrossfadeDurationMs)
        ) { refreshing ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (refreshing) {
                    val circleSize = (sizes.arcRadius + sizes.strokeWidth) * 2
                    CircularProgressIndicator(
                        color = contentColor,
                        strokeWidth = sizes.strokeWidth,
                        modifier = Modifier.size(circleSize),
                    )
                } else {
                    Image(
                        painter = painter,
                        contentDescription = "Refreshing"
                    )
                }
            }
        }
    }
}

private const val MaxAlpha = 1f
private const val MinAlpha = 0.3f
private const val CrossfadeDurationMs = 100

@Preview
@Composable
fun PreviewSwipeRefreshIndicator() {
    MaterialTheme {
        SwipeRefreshIndicator(
            isRefreshing = false,
            offset = 0f,
            refreshOffset = 10f,
        )
    }
}
