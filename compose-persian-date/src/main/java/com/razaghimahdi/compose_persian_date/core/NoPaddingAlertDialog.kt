package com.razaghimahdi.compose_persian_date.core

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@Composable
internal fun NoPaddingAlertDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    buttons: @Composable () -> Unit,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    properties: DialogProperties = DialogProperties()
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Dialog(
            onDismissRequest = onDismissRequest,
            properties = properties
        ) {
            Surface(
                modifier = modifier,
                shape = shape,
                color = backgroundColor,
                contentColor = contentColor
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    title?.let {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                            val textStyle = MaterialTheme.typography.subtitle1
                            ProvideTextStyle(textStyle, it)
                        }
                    }
                    text?.let {
                        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.high) {
                            val textStyle = MaterialTheme.typography.subtitle1
                            ProvideTextStyle(textStyle, it)
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp)
                    ) {
                        buttons()
                    }
                }
            }
        }
    }
}

