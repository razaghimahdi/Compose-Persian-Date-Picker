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
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.razaghimahdi.compose_persian_date.core.controller.PersianRangeDatePickerController
import com.razaghimahdi.compose_persian_date.core.controller.rememberPersianRangeDatePickerController
import com.razaghimahdi.compose_persian_date.util.Constants.TEXT_CALENDAR_PADDING
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun RangeDatePicker(
    controller: PersianRangeDatePickerController,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = contentColorFor(containerColor),
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {

    if (controller.dates.isEmpty()) return


    val pagerState = rememberPagerState(
        pageCount = { controller.dates.size },
        initialPage = controller.initialPagee
        // initialPage = 1
    )

    val coroutine = rememberCoroutineScope()


    val recomposeToggleState = remember { mutableStateOf(false) }
    LaunchedEffect(recomposeToggleState.value) {}


    LaunchedEffect(key1 = pagerState.currentPage) {
        controller.currentSelectedPersianDate.setShYear(controller.dates[pagerState.currentPage - 1].shYear)
        controller.currentSelectedPersianDate.setShMonth(controller.dates[pagerState.currentPage - 1].shMonth)
        controller.currentSelectedPersianDate.setShDay(controller.dates[pagerState.currentPage - 1].shDay)
    }


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

                MonthTitleBox(prevOnExecute = {
                    recomposeToggleState.value = !recomposeToggleState.value
                    coroutine.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                }, nextOnExecute = {
                    recomposeToggleState.value = !recomposeToggleState.value
                    coroutine.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }, controller = controller, contentColor = contentColor, containerColor = containerColor)

                WeekTitleBox(textStyle, containerColor = containerColor, contentColor = contentColor)


                HorizontalPager(
                    state = pagerState,
                ) {
                    CalendarBox(controller = controller, containerColor = containerColor, contentColor = contentColor)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CalendarBox(controller: PersianRangeDatePickerController, containerColor: Color, contentColor: Color) {
    val firstDayOfMonth = controller.getFirstNameDayOfWeek()
    val lastDayOfMonth = controller.getLastNameDayOfWeek()
    /* val firstDayOfMonth = remember {
         derivedStateOf { controller.getFirstNameDayOfWeek() }
     }
     val lastDayOfMonth = remember {
         derivedStateOf { controller.getFirstNameDayOfWeek() }
     }*/

    /* FlowRow(modifier = Modifier.fillMaxWidth(), maxItemsInEachRow = 7) {

         for (i in 1..firstDayOfMonth) {
             EmptyDay(containerColor = containerColor, contentColor = contentColor)
         }

         for (i in 1..controller.currentSelectedPersianDate.monthLength) {
             SingleDayBox(
                 title = (i).toString(),
                 modifier = Modifier
                     .fillMaxWidth()
                     .weight(1f),
                 controller = controller,
                 containerColor = containerColor,
                 contentColor = contentColor,
             )

         }

         for (i in 1..lastDayOfMonth) {
             EmptyDay(containerColor = containerColor, contentColor = contentColor)
         }


     }*/


    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxSize()
    ) {
        val list = derivedStateOf { (1..firstDayOfMonth).map { -1 } + (1..controller.currentSelectedPersianDate.monthLength) + (1..lastDayOfMonth).map { -1 } }
        items(list.value) { day ->
            if (day == -1) {
               EmptyDay(containerColor = containerColor, contentColor = contentColor)
            } else {
                SingleDayBox2(
                    title = day.toString(),
                    modifier = Modifier.size(40.dp),
                    controller = controller,
                    containerColor = containerColor,
                    contentColor = contentColor,
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun FlowRowScope.EmptyDay(containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .background(Color.Unspecified)
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EmptyDay(containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.Unspecified)
    )
}

@Preview
@Composable
private fun RangeDatePickerPreview() {
    RangeDatePicker(rememberPersianRangeDatePickerController())
}

@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
internal fun SingleDayBox2(title: String, modifier: Modifier, contentColor: Color, containerColor: Color, controller: PersianRangeDatePickerController) {

    val currentSelectedPersianDate = controller.currentSelectedPersianDate
    val minSelectedDate = controller.minSelectedDate
    val maxSelectedDate = controller.maxSelectedDate
    val selectedDatesRange = controller.selectedDatesRange


    val color = remember(currentSelectedPersianDate.toString(), title) { Animatable(containerColor) }
    val tmpDate = remember(currentSelectedPersianDate.toString(), title) { mutableStateOf(PersianDate(currentSelectedPersianDate.toDate())) }
    SideEffect {
        tmpDate.value.setShDay(title.toInt()).startOfDay()
    }


    LaunchedEffect(minSelectedDate, currentSelectedPersianDate.toString(), selectedDatesRange.size) {

        val singleSelectedRangDate = selectedDatesRange.find { it.toString() == tmpDate.value.startOfDay()?.toString() }

        if (
            (
                    minSelectedDate?.toString() == tmpDate.value.toString() &&
                            maxSelectedDate == null &&
                            selectedDatesRange.isEmpty())

            ||

            (singleSelectedRangDate != null)
        ) {
            color.animateTo(contentColor, animationSpec = tween(350))
        } else {
            color.animateTo(containerColor, animationSpec = tween(350))
        }
    }


    var topStartCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }
    var bottomStartCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }
    var topEndCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }
    var bottomEndCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }

    LaunchedEffect(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        topStartCornerRadius = if (
            (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
            (tmpDate.value.toString() == selectedDatesRange.lastOrNull()?.toString())
        ) {
            50.dp
        } else {
            0.dp
        }
    }


    LaunchedEffect(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        bottomStartCornerRadius = if (
            (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
            (tmpDate.value.toString() == selectedDatesRange.lastOrNull()?.toString())
        ) {
            50.dp
        } else {
            0.dp
        }
    }


    LaunchedEffect(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        topEndCornerRadius = if (
            (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
            (tmpDate.value.toString() == selectedDatesRange.firstOrNull()?.toString())
        ) {
            50.dp
        } else {
            0.dp
        }
    }



    LaunchedEffect(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        bottomEndCornerRadius = if (
            (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
            (tmpDate.value.toString() == selectedDatesRange.firstOrNull()?.toString())
        ) {
            50.dp
        } else {
            0.dp
        }
    }


    val topStartCorner = animateDpAsState(targetValue = topEndCornerRadius, label = "", animationSpec = tween(350))
    val bottomStartCorner = animateDpAsState(targetValue = bottomEndCornerRadius, label = "", animationSpec = tween(350))
    val topEndCorner = animateDpAsState(targetValue = topStartCornerRadius, label = "", animationSpec = tween(350))
    val bottomEndCorner = animateDpAsState(targetValue = bottomStartCornerRadius, label = "", animationSpec = tween(350))




    SingleDayBoxStateless(
        modifier = modifier,
        color = color.value,
        topStartCorner = topStartCorner.value,
        bottomStartCorner = bottomStartCorner.value,
        topEndCorner = topEndCorner.value,
        bottomEndCorner = bottomEndCorner.value,
        controller = controller,
        title = title,
        contentColor = contentColor,
        containerColor = containerColor,
    )
}

@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
internal fun SingleDayBox(title: String, modifier: Modifier, contentColor: Color, containerColor: Color, controller: PersianRangeDatePickerController) {

    val currentSelectedPersianDate = controller.currentSelectedPersianDate
    val minSelectedDate = controller.minSelectedDate
    val maxSelectedDate = controller.maxSelectedDate
    val selectedDatesRange = controller.selectedDatesRange


    var color by remember(currentSelectedPersianDate.toString(), title) { mutableStateOf(containerColor) }
    val tmpDate = remember(currentSelectedPersianDate.toString(), title) { mutableStateOf(PersianDate(currentSelectedPersianDate.toDate())) }
    SideEffect {
        tmpDate.value.setShDay(title.toInt()).startOfDay()
    }


    val singleSelectedRangDate = selectedDatesRange.find { it.toString() == tmpDate.value.startOfDay()?.toString() }

    color = if (
        (
                minSelectedDate?.toString() == tmpDate.value.toString() &&
                        maxSelectedDate == null &&
                        selectedDatesRange.isEmpty())

        ||

        (singleSelectedRangDate != null)
    ) {
        contentColor
    } else {
        containerColor
    }


    var topStartCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }
    var bottomStartCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }
    var topEndCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }
    var bottomEndCornerRadius by remember(selectedDatesRange.size, minSelectedDate, tmpDate.toString()) {
        mutableStateOf(50.dp)
    }

    topStartCornerRadius = if (
        (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
        (tmpDate.value.toString() == selectedDatesRange.lastOrNull()?.toString())
    ) {
        50.dp
    } else {
        0.dp
    }


    bottomStartCornerRadius = if (
        (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
        (tmpDate.value.toString() == selectedDatesRange.lastOrNull()?.toString())
    ) {
        50.dp
    } else {
        0.dp
    }


    topEndCornerRadius = if (
        (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
        (tmpDate.value.toString() == selectedDatesRange.firstOrNull()?.toString())
    ) {
        50.dp
    } else {
        0.dp
    }



    bottomEndCornerRadius = if (
        (minSelectedDate?.toString() == tmpDate.value.toString() && maxSelectedDate == null && selectedDatesRange.isEmpty()) ||
        (tmpDate.value.toString() == selectedDatesRange.firstOrNull()?.toString())
    ) {
        50.dp
    } else {
        0.dp
    }

    /*
        val topStartCorner = animateDpAsState(targetValue = topEndCornerRadius, label = "", animationSpec = tween(350))
        val bottomStartCorner = animateDpAsState(targetValue = bottomEndCornerRadius, label = "", animationSpec = tween(350))
        val topEndCorner = animateDpAsState(targetValue = topStartCornerRadius, label = "", animationSpec = tween(350))
        val bottomEndCorner = animateDpAsState(targetValue = bottomStartCornerRadius, label = "", animationSpec = tween(350))
    */



    SingleDayBoxStateless(
        modifier = modifier,
        color = color,
        topStartCorner = bottomEndCornerRadius,
        bottomStartCorner = bottomEndCornerRadius,
        topEndCorner = topStartCornerRadius,
        bottomEndCorner = bottomStartCornerRadius,
        controller = controller,
        title = title,
        contentColor = contentColor,
        containerColor = containerColor,
    )
}

@Composable
internal fun SingleDayBoxStateless(
    modifier: Modifier,
    color: Color,
    contentColor: Color,
    containerColor: Color,
    topStartCorner: Dp,
    bottomStartCorner: Dp,
    topEndCorner: Dp,
    bottomEndCorner: Dp,
    controller: PersianRangeDatePickerController,
    title: String,
) {

    Box(contentAlignment = Alignment.Center, modifier = modifier
        .then(
            Modifier
                .background(
                    color,
                    RoundedCornerShape(topStart = topStartCorner, bottomStart = bottomStartCorner, topEnd = topEndCorner, bottomEnd = bottomEndCorner)
                )
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                ) {
                    controller.addToRangeList(title.toInt())
                }
        )) {
        Column(
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall, color = if (contentColor == color) containerColor else contentColor)
        }
    }
}

@Composable
internal fun WeekTitleBox(textStyle: TextStyle, containerColor: Color, contentColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
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

@Composable
private fun MonthTitleBox(prevOnExecute: () -> Unit, nextOnExecute: () -> Unit, contentColor: Color, controller: PersianRangeDatePickerController, containerColor: Color) {
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
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = containerColor)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = String.format(Locale.getDefault(), "%s %d", controller.currentSelectedPersianDate.monthName, controller.currentSelectedPersianDate.shYear),
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
                //  controller.prevMonth()
                prevOnExecute()
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = containerColor)
            }
        }
    }
}
