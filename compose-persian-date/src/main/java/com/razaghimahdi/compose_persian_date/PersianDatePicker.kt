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

import android.graphics.Typeface
import android.widget.NumberPicker
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.razaghimahdi.compose_persian_date.GlobalStyle.textColor
import com.razaghimahdi.compose_persian_date.GlobalStyle.textSize
import com.razaghimahdi.compose_persian_date.core.CustomNumberPicker
import com.razaghimahdi.compose_persian_date.core.PersianDatePickerController
import com.razaghimahdi.compose_persian_date.util.Constants.persianMonthNames
import com.razaghimahdi.compose_persian_date.util.Tools.getStringColor
import com.razaghimahdi.compose_persian_date.util.Tools.toPersianNumber
import com.razaghimahdi.compose_persian_date.util.changeTextColor


@Composable
internal fun PersianDatePicker(
    controller: PersianDatePickerController,
    modifier: Modifier = Modifier,
    onDateChanged: ((year: Int, month: Int, day: Int) -> Unit)? = null,
    contentColor: Color
) {


    controller.initDate()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {


        ComposeCustomNumberPicker(
            modifier = Modifier.wrapContentWidth(),
            formatter = { i -> i.toString().toPersianNumber() },
            onValueChangedListener = { picker, oldVal, newVal ->
                controller.updateFromCustomNumberPicker(newDay = newVal)
                if (onDateChanged != null) {
                    onDateChanged(
                        controller.selectedYear,
                        controller.selectedMonth,
                        controller.selectedDay
                    )
                }

            },
            minValue = 1,
            maxValue = controller.maxDay,
            selectedValue = controller.selectedDay,
            contentColor = contentColor
        )

        Spacer(
            modifier = Modifier
                .height(4.dp)
                .width(4.dp)
        )

        ComposeCustomNumberPicker(
            modifier = Modifier.wrapContentWidth(),
            formatter = { i -> i.toString().toPersianNumber() },
            onValueChangedListener = { picker, oldVal, newVal ->
                controller.updateFromCustomNumberPicker(newMonth = newVal)
                if (onDateChanged != null) {
                    onDateChanged(
                        controller.selectedYear,
                        controller.selectedMonth,
                        controller.selectedDay
                    )
                }
            },
            minValue = 1,
            maxValue = if (controller.maxMonth > 0) controller.maxMonth else 12,
            selectedValue = controller.selectedMonth,
            displayedValues = if (controller.displayMonthNames) persianMonthNames else null,
            contentColor = contentColor
        )

        Spacer(
            modifier = Modifier
                .height(4.dp)
                .width(4.dp)
        )

        ComposeCustomNumberPicker(
            modifier = Modifier.wrapContentWidth(),
            formatter = { i -> i.toString().toPersianNumber() },
            onValueChangedListener = { picker, oldVal, newVal ->
                controller.updateFromCustomNumberPicker(newYear = newVal)
                if (onDateChanged != null) {
                    onDateChanged(
                        controller.selectedYear,
                        controller.selectedMonth,
                        controller.selectedDay
                    )
                }
            },
            minValue = controller.minYear,
            maxValue = controller.maxYear,
            selectedValue = controller.selectedYear,
            contentColor = contentColor
        )

    }

}


@Composable
fun ComposeCustomNumberPicker(
    modifier: Modifier,
    formatter: NumberPicker.Formatter,
    onValueChangedListener: NumberPicker.OnValueChangeListener,
    minValue: Int,
    maxValue: Int,
    selectedValue: Int,
    // dividerColor: Color = Color.Red,
    displayedValues: Array<String>? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    contentColor: Color
) {


    textSize = textStyle.fontSize.value
    textColor = android.graphics.Color.parseColor(getStringColor(textStyle.color))


    /* NumberPicker(
         value = selectedValue,
         range = minValue..maxValue,
         onValueChange = {
             pickerValue = it
         }
     )*/

    AndroidView(
        modifier = modifier,
        factory = { context ->

            val numberPicker = CustomNumberPicker(context = context)
            /* changeDividerColor(
                 numberPicker,
                 android.graphics.Color.parseColor(getStringColor(dividerColor))
             )*/
            numberPicker.setFormatter(formatter)
            numberPicker.minValue = minValue
            numberPicker.maxValue = maxValue
            numberPicker.value = selectedValue
            numberPicker.changeTextColor( contentColor.toArgb())
            numberPicker.setOnValueChangedListener(onValueChangedListener)
            if (displayedValues != null) numberPicker.displayedValues = displayedValues


            numberPicker
        },
        update = { numberPicker ->
            numberPicker.minValue = minValue
            numberPicker.maxValue = maxValue
            numberPicker.value = selectedValue

        }
    )
}

internal object GlobalStyle {

    internal var typeface: Typeface? = null
    internal var textSize: Float? = null
    internal var textColor: Int? = null

}

