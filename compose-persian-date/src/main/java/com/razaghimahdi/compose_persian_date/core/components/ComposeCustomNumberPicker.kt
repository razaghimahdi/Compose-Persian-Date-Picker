package com.razaghimahdi.compose_persian_date.core.components

import androidx.annotation.FontRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat


@Composable
internal fun ComposeCustomNumberPicker(
    modifier: Modifier,
    onValueChangedListener: NumberPicker.OnValueChangeListener,
    minValue: Int,
    maxValue: Int,
    selectedValue: Int,
    dividerColor: Color,
    selectedTextColor: Color,
    unSelectedTextColor: Color,
    unSelectedTextStyle: TextStyle,
    selectedTextStyle: TextStyle,
    @FontRes font: Int? = null,
    displayedValues: Array<String>? = null,
) {


    AndroidView(modifier = modifier, factory = { context ->

        val numberPicker = NumberPicker(
            context
        )
        numberPicker.minValue = minValue
        numberPicker.maxValue = maxValue
        numberPicker.value = selectedValue
        numberPicker.dividerColor = dividerColor.toArgb()
        numberPicker.textColor = unSelectedTextColor.toArgb()
        numberPicker.selectedTextColor = selectedTextColor.toArgb()
        numberPicker.textSize = unSelectedTextStyle.fontSize.value * 2
        numberPicker.selectedTextSize = selectedTextStyle.fontSize.value * 2
        numberPicker.isFadingEdgeEnabled = false;
        if (font != null) {
            numberPicker.setSelectedTypeface(ResourcesCompat.getFont(context, font))
            numberPicker.typeface = ResourcesCompat.getFont(context, font)
        };
        numberPicker.setOnValueChangedListener(onValueChangedListener)
        if (displayedValues != null) {
            numberPicker.setDisplayedValues(displayedValues)
        }

        numberPicker
    }, update = { numberPicker ->
        numberPicker.minValue = minValue
        numberPicker.maxValue = maxValue
        numberPicker.smoothScrollToPosition(selectedValue)

    })
}
