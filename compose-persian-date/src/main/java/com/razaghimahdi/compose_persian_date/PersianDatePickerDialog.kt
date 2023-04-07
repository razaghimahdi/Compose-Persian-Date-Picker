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


package com.razaghimahdi.compose_persian_date

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.razaghimahdi.compose_persian_date.core.NoPaddingAlertDialog
import com.razaghimahdi.compose_persian_date.core.PersianDatePickerController

@Composable
fun PersianDatePickerDialog(
    controller: PersianDatePickerController,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onDateChanged: ((year: Int, month: Int, day: Int) -> Unit)? = null,
    // submitTitle: String = "تایید",
    dismissTitle: String = "بستن",
    textButtonStyle: TextStyle = LocalTextStyle.current,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
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
        text = { PersianDataPicker(controller, modifier.padding(8.dp), onDateChanged) },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { controller.resetDate() }) {
                    Text(text = "امروز", style = textButtonStyle)
                }
                Row(
                    modifier = Modifier.wrapContentWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text(text = dismissTitle, style = textButtonStyle)
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