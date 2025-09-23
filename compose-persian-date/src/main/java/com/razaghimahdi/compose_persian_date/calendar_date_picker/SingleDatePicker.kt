/*
 * Copyright (C) 2024 razaghimahdi (Mahdi Razzaghi Ghaleh)
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


package com.razaghimahdi.compose_persian_date.calendar_date_picker

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.razaghimahdi.compose_persian_date.core.components.EmptyDay
import com.razaghimahdi.compose_persian_date.core.components.InfiniteHorizontalPager
import com.razaghimahdi.compose_persian_date.core.components.WeekTitleBox
import com.razaghimahdi.compose_persian_date.core.controller.PersianSingleDatePickerController
import com.razaghimahdi.compose_persian_date.core.controller.rememberPersianSingleDatePickerController
import com.razaghimahdi.compose_persian_date.core.model.PDate
import com.razaghimahdi.compose_persian_date.util.Constants.TEXT_CALENDAR_PADDING
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun SingleDatePicker(
    controller: PersianSingleDatePickerController,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {

    LaunchedEffect(Unit) {
        controller.initDate()
    }

    val max = Short.MAX_VALUE.toInt()
    val half = max / 2

    if (controller.dateListCollection.isEmpty()) return

    val pagerPositionIndex =
        controller.initialPagee + half - half % controller.dateListCollection.size
    val pagerState = rememberPagerState(pageCount = { max }, initialPage = pagerPositionIndex)

    val coroutine = rememberCoroutineScope()

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {

        Box(
            modifier = modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                MonthTitleBox(
                    prevOnExecute = {
                         coroutine.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)

                        }
                    },
                    nextOnExecute = {
                         coroutine.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    controller = controller,
                    contentColor = contentColor,
                    containerColor = containerColor
                )

                WeekTitleBox(
                    textStyle,
                    containerColor = containerColor,
                    contentColor = contentColor
                )
                InfiniteHorizontalPager(
                    modifier = Modifier.fillMaxWidth(),
                    pagerState = pagerState,
                    pagerPositionIndex = pagerPositionIndex,
                    pageCount = controller.dateListCollection.size,
                    onPageChanged = { page ->
                        controller.updateCurrentDate(page)
                    }
                ) {

                    CalendarBox(
                        controller = controller,
                        containerColor = containerColor,
                        contentColor = contentColor
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CalendarBox(
    controller: PersianSingleDatePickerController,
    containerColor: Color,
    contentColor: Color
) {
    val list =  controller.showDateList

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {

        items(items = list) { date ->
            if (date.value == -1) {
                EmptyDay(containerColor = containerColor, contentColor = contentColor)
            } else {
                DateBox(
                    dateP = date,
                    modifier = Modifier.size(40.dp),
                    controller = controller,
                    containerColor = containerColor,
                    contentColor = contentColor,
                )
            }
        }
    }
}


@Preview
@Composable
private fun DatePickerPreview() {
    SingleDatePicker(rememberPersianSingleDatePickerController())
}


@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
private fun DateBox(
    dateP: PDate,
    modifier: Modifier,
    contentColor: Color,
    containerColor: Color,
    controller: PersianSingleDatePickerController
) {

    val date by rememberUpdatedState(dateP)
    val isSelected by rememberUpdatedState(date.persianDate.startOfDay().time == controller.selectedDate?.persianDate?.startOfDay()?.time)

    val color by animateColorAsState(
        if (isSelected) {
            contentColor
        } else {
            containerColor
        }, label = "", animationSpec = tween(350)
    )


    Box(contentAlignment = Alignment.Center, modifier = modifier
        .background(color, RoundedCornerShape(50.dp))
        .clip(CircleShape)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = true),
        ) {
             controller.updateSelectedDate(date = date)
        }
    ) {
        Column(
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = date.value.toString(),
                style = MaterialTheme.typography.bodySmall,
                color = if (contentColor == color) containerColor else contentColor
            )
        }
    }
}


@Composable
private fun MonthTitleBox(
    prevOnExecute: () -> Unit,
    nextOnExecute: () -> Unit,
    contentColor: Color,
    controller: PersianSingleDatePickerController,
    containerColor: Color
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(contentColor, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            IconButton(onClick = {
                //    controller.nextMonth()
                nextOnExecute()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = containerColor
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%s %d",
                        controller.currentSelectedPersianDate.monthName,
                        controller.currentSelectedPersianDate.shYear
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = containerColor
                )
                Text(
                    text = controller.getGeorgianMonth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = containerColor
                )
            }

            IconButton(onClick = {
                 prevOnExecute()
            }) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = containerColor
                )
            }
        }
    }
}
