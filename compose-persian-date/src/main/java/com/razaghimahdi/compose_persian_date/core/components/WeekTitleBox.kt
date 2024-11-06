package com.razaghimahdi.compose_persian_date.core.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.razaghimahdi.compose_persian_date.util.Constants.TEXT_CALENDAR_PADDING


@Composable
internal fun WeekTitleBox(textStyle: TextStyle, containerColor: Color, contentColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "ش", style = textStyle, textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
        Text(
            text = "ی", textAlign = TextAlign.Center, style = textStyle,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
        Text(
            text = "د", textAlign = TextAlign.Center, style = textStyle,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
        Text(
            text = "س", textAlign = TextAlign.Center, style = textStyle,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
        Text(
            text = "چ", textAlign = TextAlign.Center, style = textStyle,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
        Text(
            text = "پ", textAlign = TextAlign.Center, style = textStyle,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
        Text(
            text = "ج", textAlign = TextAlign.Center, style = textStyle,
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING)
                .weight(1f),
            color = contentColor,
        )
    }
}
