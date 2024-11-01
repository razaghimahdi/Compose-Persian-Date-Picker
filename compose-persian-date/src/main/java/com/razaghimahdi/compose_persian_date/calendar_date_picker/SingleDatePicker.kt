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
import androidx.compose.runtime.ReusableContent
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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


    val pagerState = rememberPagerState(
        pageCount = { controller.dateList.size },
        initialPage = controller.initialPagee
        // initialPage = 1
    )

    val coroutine = rememberCoroutineScope()


  //  val recomposeToggleState = remember { mutableStateOf(false) }
   // LaunchedEffect(recomposeToggleState.value) {}


    LaunchedEffect(key1 = pagerState.currentPage) {
        controller.currentSelectedPersianDate.setShYear(controller.dateList[pagerState.currentPage - 1].persianDate.shYear)
        controller.currentSelectedPersianDate.setShMonth(controller.dateList[pagerState.currentPage - 1].persianDate.shMonth)
        controller.currentSelectedPersianDate.setShDay(controller.dateList[pagerState.currentPage - 1].persianDate.shDay)
        controller.configurePageList()
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

                MonthTitleBox(
                    prevOnExecute = {
                      //  recomposeToggleState.value = !recomposeToggleState.value
                        coroutine.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    },
                    nextOnExecute = {
                      //  recomposeToggleState.value = !recomposeToggleState.value
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

                HorizontalPager(
                    state = pagerState,
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
    val firstDayOfMonth = controller.getFirstNameDayOfWeek()
    val lastDayOfMonth = controller.getLastNameDayOfWeek()

    //val list = (1..firstDayOfMonth).map { -1 } + (1..controller.currentSelectedPersianDate.monthLength) + (1..lastDayOfMonth).map { -1 }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxSize()
    ) {
        items(items = controller.showDateList,
                key = {it.hashCode()}) { date ->
            if (date.value == -1) {
                EmptyDay(containerColor = containerColor, contentColor = contentColor)
            } else {
                /*  SingleDayBox(
                      title = day.toString(),
                      modifier = Modifier.size(40.dp),
                      controller = controller,
                      containerColor = containerColor,
                      contentColor = contentColor,
                  )*/
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyDay(containerColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(Color.Unspecified)
    )
}

@Preview
@Composable
private fun DatePickerPreview() {
    SingleDatePicker(rememberPersianSingleDatePickerController())
}

@SuppressLint("ProduceStateDoesNotAssignValue")
@Composable
private fun SingleDayBox(
    title: String,
    modifier: Modifier,
    contentColor: Color,
    containerColor: Color,
    controller: PersianSingleDatePickerController
) {

    val currentSelectedPersianDate =
        remember { derivedStateOf { controller.currentSelectedPersianDate } }

    val tmpDate = remember(currentSelectedPersianDate.value.toString(), title) {
        val date = PersianDate(currentSelectedPersianDate.value.toDate())
        date.setShDay(title.toInt()).startOfDay()
        mutableStateOf(date)
    }

    // val isSelected = remember { derivedStateOf { tmpDate.value.startOfDay().time == controller.selectedDate?.startOfDay()?.time } }
    val isSelected = remember { derivedStateOf { false } }


    /* val color = remember(currentSelectedPersianDate.value.toString(), title) { Animatable(containerColor) }
     LaunchedEffect(isSelected) {
         if (isSelected) {
             color.animateTo(contentColor, animationSpec = tween(350))
         } else {
             color.animateTo(containerColor, animationSpec = tween(350))
         }
     }*/

    val color by animateColorAsState(
        if (isSelected.value) {
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
            controller.updateSelectedDate(day = title.toInt())
        }
    ) {
        Column(
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = if (contentColor == color) containerColor else contentColor
            )
        }
    }
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

    //  val currentSelectedPersianDate = remember { derivedStateOf { controller.currentSelectedPersianDate } }

    /* val tmpDate = remember(currentSelectedPersianDate.value.toString(), title) {
         val date = PersianDate(currentSelectedPersianDate.value.toDate())
         date.setShDay(title.toInt()).startOfDay()
         mutableStateOf(date)
     }*/

    val date by rememberUpdatedState(dateP)
    val isSelected by rememberUpdatedState(date.persianDate.startOfDay().time == controller.selectedDate?.persianDate?.startOfDay()?.time)

    // val isSelected =  date.persianDate.startOfDay().time == controller.selectedDate?.persianDate?.startOfDay()?.time

    // Log.i("AppDebug", "DateBox isSelected: " + isSelected)


    /* val color = remember(currentSelectedPersianDate.value.toString(), title) { Animatable(containerColor) }
     LaunchedEffect(isSelected) {
         if (isSelected) {
             color.animateTo(contentColor, animationSpec = tween(350))
         } else {
             color.animateTo(containerColor, animationSpec = tween(350))
         }
     }*/

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
                // controller.updateSelectedDate(day = date.value)
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
private fun SingleDayBoxStateless(
    modifier: Modifier,
    color: Color,
    contentColor: Color,
    containerColor: Color,
    topStartCorner: Dp,
    bottomStartCorner: Dp,
    topEndCorner: Dp,
    bottomEndCorner: Dp,
    controller: PersianSingleDatePickerController,
    title: String,
) {

    Box(contentAlignment = Alignment.Center, modifier = modifier
        .then(
            Modifier
                .background(
                    color,
                    RoundedCornerShape(
                        topStart = topStartCorner,
                        bottomStart = bottomStartCorner,
                        topEnd = topEndCorner,
                        bottomEnd = bottomEndCorner
                    )
                )
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = true),
                ) {
                    //    controller.addToRangeList(title.toInt())
                }
        )) {
        Column(
            modifier = Modifier
                .padding(TEXT_CALENDAR_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = if (contentColor == color) containerColor else contentColor
            )
        }
    }
}

@Composable
private fun WeekTitleBox(textStyle: TextStyle, containerColor: Color, contentColor: Color) {
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
                //  controller.prevMonth()
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
