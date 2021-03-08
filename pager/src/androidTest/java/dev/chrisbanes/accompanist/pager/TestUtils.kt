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

package dev.chrisbanes.accompanist.pager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.percentOffset
import androidx.compose.ui.test.performGesture
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.LayoutDirection

fun ComposeContentTestRule.setContent(
    layoutDirection: LayoutDirection? = null,
    composable: @Composable () -> Unit,
) {
    setContent {
        CompositionLocalProvider(
            LocalLayoutDirection provides (layoutDirection ?: LocalLayoutDirection.current),
            content = composable
        )
    }
}

internal fun SemanticsNodeInteraction.swipeAcrossCenter(
    distancePercentageX: Float = 0f,
    distancePercentageY: Float = 0f,
    durationMillis: Long = 200,
): SemanticsNodeInteraction = performGesture {
    swipe(
        start = percentOffset(
            x = 0.5f - distancePercentageX / 2,
            y = 0.5f - distancePercentageY / 2,
        ),
        end = percentOffset(
            x = 0.5f + distancePercentageX / 2,
            y = 0.5f + distancePercentageY / 2,
        ),
        durationMillis = durationMillis
    )
}
