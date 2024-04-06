/*
 * Copyright (C) 2023 razaghimahdi (Mahdi Razzaghi Ghaleh)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.razaghimahdi.compose_persian_date.core.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle


@Composable
internal fun TextButton(
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    contentColor: Color,
    enabled: Boolean = true,
    textButtonStyle: TextStyle,
    onClick: () -> Unit
) {
    androidx.compose.material3.TextButton(
        enabled = enabled,
        modifier = modifier,
        onClick = { onClick() },
    ) {
        Text(
            text = label, color = if (enabled) contentColor else Color(0xFFDDDDDD), style = textButtonStyle
        )
    }
}