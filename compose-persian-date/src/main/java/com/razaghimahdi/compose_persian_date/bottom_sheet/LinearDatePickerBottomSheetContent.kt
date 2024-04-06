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



package com.razaghimahdi.compose_persian_date.bottom_sheet

import androidx.annotation.FontRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.razaghimahdi.compose_persian_date.linear_date_picker.LinearPersianDatePicker
import com.razaghimahdi.compose_persian_date.core.components.AppButton
import com.razaghimahdi.compose_persian_date.core.components.PersianDatePickerController
import com.razaghimahdi.compose_persian_date.core.components.TextButton
import com.razaghimahdi.compose_persian_date.util.Tools.isDateToday


@Composable
internal fun LinearDatePickerBottomSheetContent(
    controller: PersianDatePickerController,
    modifier: Modifier = Modifier,
    onDateChanged: ((year: Int, month: Int, day: Int) -> Unit)? = null,
    submitTitle: String = "تایید",
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    textButtonStyle: TextStyle = LocalTextStyle.current,
    unSelectedTextStyle: TextStyle = LocalTextStyle.current,
    selectedTextStyle: TextStyle = LocalTextStyle.current,
    @FontRes font: Int? = null,
    onDismissRequest: () -> Unit,
) {

    val recomposeToggleState = remember { mutableStateOf(false) }
    LaunchedEffect(recomposeToggleState.value) {}


    var tmpController by remember(key1 = controller) {
        mutableStateOf(PersianDatePickerController())
    }
    LaunchedEffect(controller) {
        tmpController.updateDate(controller.date.toDate())
    }

    val isToday = tmpController.getGregorianDate()?.isDateToday() ?: false




    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Surface(
            modifier = modifier, color = backgroundColor, contentColor = contentColor
        ) {

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight(), horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {


                    LinearPersianDatePicker(
                        controller = tmpController,
                        contentColor = contentColor,
                        selectedTextStyle = selectedTextStyle,
                        unSelectedTextStyle = unSelectedTextStyle,
                        font = font,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        onDateChanged = { year,month,day->
                            if (onDateChanged != null) {
                                onDateChanged(year,month,day)
                            }
                            recomposeToggleState.value = !recomposeToggleState.value
                        },
                    )


                    Spacer(modifier = Modifier.size(16.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        AppButton(label = submitTitle, modifier = Modifier.fillMaxWidth(.8f), enable = true, textButtonStyle = textButtonStyle, onClick = {
                            controller.updateDate(tmpController.date.toDate())
                            recomposeToggleState.value = !recomposeToggleState.value

                            onDismissRequest()
                        })


                        TextButton(label = "امروز", modifier = Modifier, enabled = !isToday, textButtonStyle = textButtonStyle, contentColor = contentColor) {
                            tmpController.resetDate()
                            recomposeToggleState.value = !recomposeToggleState.value
                        }
                    }

                }

            }
        }
    }
}
