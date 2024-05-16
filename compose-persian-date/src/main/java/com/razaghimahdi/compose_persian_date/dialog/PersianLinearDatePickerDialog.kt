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


package com.razaghimahdi.compose_persian_date.dialog

import androidx.annotation.FontRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.razaghimahdi.compose_persian_date.linear_date_picker.LinearPersianDatePicker
import com.razaghimahdi.compose_persian_date.core.components.NoPaddingAlertDialog
import com.razaghimahdi.compose_persian_date.core.controller.PersianDatePickerController

@Composable
fun PersianLinearDatePickerDialog(
    controller: PersianDatePickerController,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onDateChanged: ((year: Int, month: Int, day: Int) -> Unit)? = null,
    // submitTitle: String = "تایید",
    dismissTitle: String = "بستن",
    textButtonStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    unSelectedTextStyle: TextStyle = LocalTextStyle.current,
    selectedTextStyle: TextStyle = LocalTextStyle.current,
    @FontRes font: Int? = null,
    properties: DialogProperties = DialogProperties()
) {


    NoPaddingAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        shape = shape,
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        properties = properties,
        title = { },
        text = {
            LinearPersianDatePicker(
                modifier = modifier.padding(8.dp),
                controller = controller,
                contentColor = contentColor,
                selectedTextStyle = selectedTextStyle,
                unSelectedTextStyle = unSelectedTextStyle,
                onDateChanged = onDateChanged,
                font = font,
            )
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { controller.resetDate(onDateChanged) }) {
                    Text(text = "امروز", style = textButtonStyle, color = contentColor)
                }
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = dismissTitle, style = textButtonStyle, color = contentColor)
                    }
                    /* TextButton(onClick = {
                         onDismissRequest()
                     }) {
                         Text(text = submitTitle)
                     }*/
                }

            }
        },
    )


}