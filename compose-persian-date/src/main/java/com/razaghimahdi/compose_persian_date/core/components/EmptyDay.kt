package com.razaghimahdi.compose_persian_date.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EmptyDay(containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.Unspecified)
    )
}