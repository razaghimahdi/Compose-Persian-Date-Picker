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

package com.razaghimahdi.compose_persian_date.linear_date_picker

import androidx.annotation.FontRes
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.razaghimahdi.compose_persian_date.core.components.ComposeCustomNumberPicker
import com.razaghimahdi.compose_persian_date.core.components.PersianDatePickerController
import com.razaghimahdi.compose_persian_date.util.Constants.persianMonthNames



@Composable
internal fun LinearPersianDatePicker(
    controller: PersianDatePickerController,
    modifier: Modifier = Modifier,
    contentColor: Color,
    unSelectedTextStyle: TextStyle,
    selectedTextStyle: TextStyle,
    @FontRes font: Int?,
    onDateChanged: ((year: Int, month: Int, day: Int) -> Unit)? = null,
) {


    controller.initDate()

    Row(
        modifier = modifier, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {

        ComposeCustomNumberPicker(
            modifier = Modifier.weight(1F),
            onValueChangedListener = { picker, oldVal, newVal ->
                controller.updateFromCustomNumberPicker(newDay = newVal)
                if (onDateChanged != null) {
                    onDateChanged(
                        controller.selectedYear, controller.selectedMonth, controller.selectedDay
                    )
                }

            },
            minValue = 1,
            maxValue = controller.maxDay,
            dividerColor = contentColor,
            selectedTextColor = contentColor,
            unSelectedTextColor = contentColor,
            selectedTextStyle = selectedTextStyle,
            unSelectedTextStyle = unSelectedTextStyle,
            font = font,
            selectedValue = controller.selectedDay,
        )

        Spacer(modifier = Modifier.size(16.dp))



        ComposeCustomNumberPicker(
            modifier = Modifier.weight(1F),
            onValueChangedListener = { picker, oldVal, newVal ->
                controller.updateFromCustomNumberPicker(newMonth = newVal)
                if (onDateChanged != null) {
                    onDateChanged(
                        controller.selectedYear, controller.selectedMonth, controller.selectedDay
                    )
                }
            },
            minValue = 1,
            maxValue = if (controller.maxMonth > 0) controller.maxMonth else 12,
            selectedValue = controller.selectedMonth,
            dividerColor = contentColor,
            selectedTextColor = contentColor,
            unSelectedTextColor = contentColor,
            selectedTextStyle = selectedTextStyle,
            unSelectedTextStyle = unSelectedTextStyle,
            font = font,
            displayedValues = if (controller.displayMonthNames) persianMonthNames else null,
        )

        Spacer(modifier = Modifier.size(16.dp))


        ComposeCustomNumberPicker(
            modifier = Modifier
                .weight(1F)
                .wrapContentHeight(),
            onValueChangedListener = { picker, oldVal, newVal ->
                controller.updateFromCustomNumberPicker(newYear = newVal)
                if (onDateChanged != null) {
                    onDateChanged(
                        controller.selectedYear, controller.selectedMonth, controller.selectedDay
                    )
                }
            },
            minValue = controller.minYear,
            maxValue = controller.maxYear,
            dividerColor = contentColor,
            selectedTextColor = contentColor,
            unSelectedTextColor = contentColor,
            selectedTextStyle = selectedTextStyle,
            unSelectedTextStyle = unSelectedTextStyle,
            font = font,
            selectedValue = controller.selectedYear,
        )

    }

}
