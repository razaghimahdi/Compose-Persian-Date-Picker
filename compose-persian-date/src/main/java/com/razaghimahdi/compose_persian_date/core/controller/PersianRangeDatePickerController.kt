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

package com.razaghimahdi.compose_persian_date.core.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import saman.zamani.persiandate.PersianDate
import java.util.Calendar
import java.util.Locale


@Composable
fun rememberPersianRangeDatePickerController(): PersianRangeDatePickerController {
    return remember { PersianRangeDatePickerController() }
}


class PersianRangeDatePickerController {


    private var _initialPagee: MutableState<Int> = mutableStateOf(1)
    internal val initialPagee get() = _initialPagee.value


    private var _selectedDatesRange: MutableState<ArrayList<PersianDate>> = mutableStateOf(arrayListOf())
    internal val selectedDatesRange get() = _selectedDatesRange.value


    private var _dates: MutableState<ArrayList<PersianDate>> = mutableStateOf(arrayListOf())
    internal val dates get() = _dates.value


    private var _currentSelectedPersianDate: MutableState<PersianDate> = mutableStateOf(PersianDate().startOfDay())
    internal val currentSelectedPersianDate get() = _currentSelectedPersianDate.value


    private var _minSelectedDate: MutableState<PersianDate?> = mutableStateOf(null)
    internal val minSelectedDate get() = _minSelectedDate.value


    private var _maxSelectedDate: MutableState<PersianDate?> = mutableStateOf(null)
    internal val maxSelectedDate get() = _maxSelectedDate.value


    private var _yearRange: MutableState<Int> = mutableIntStateOf(10)
    internal val yearRange get() = _yearRange.value

    private var _minYear: MutableState<Int> = mutableIntStateOf(PersianDate().shYear - yearRange)
    internal val minYear get() = _minYear.value

    private var _maxYear: MutableState<Int> = mutableIntStateOf(PersianDate().shYear + yearRange)
    internal val maxYear get() = _maxYear.value


    init {
        initDate()
    }

    internal fun getInitialPage(): Int {
        val firstDate =
            dates.find {
                it.shYear == currentSelectedPersianDate.shYear && it.shMonth == currentSelectedPersianDate.shMonth
            }
        val value = dates.indexOf(firstDate) + 1
        return value
    }

    internal fun updateCurrentSelectedPersianDate(date: PersianDate) {
        _currentSelectedPersianDate.value = date
    }

    internal fun getFirstNameDayOfWeek(): Int {
        val tmpDate = currentSelectedPersianDate
        tmpDate.setShDay(1)

        return tmpDate.dayOfWeek()
    }

    internal fun getLastNameDayOfWeek(): Int {
        val tmpDate = currentSelectedPersianDate
        tmpDate.setShDay(tmpDate.monthLength)

        return (6 - tmpDate.dayOfWeek())
    }


    internal fun nextMonth() {
        if (currentSelectedPersianDate.shMonth == 12) {
            currentSelectedPersianDate.setShYear(currentSelectedPersianDate.shYear + 1)
            currentSelectedPersianDate.setShMonth(1)
        } else {
            currentSelectedPersianDate.setShMonth(currentSelectedPersianDate.shMonth + 1)
        }
        currentSelectedPersianDate.setShDay(1)
    }

    internal fun prevMonth() {
        if (currentSelectedPersianDate.shMonth == 1) {
            currentSelectedPersianDate.setShYear(currentSelectedPersianDate.shYear - 1)
            currentSelectedPersianDate.setShMonth(12)
        } else {
            currentSelectedPersianDate.setShMonth(currentSelectedPersianDate.shMonth - 1)
        }
        currentSelectedPersianDate.setShDay(1)
    }

    internal fun getGeorgianMonth(): String {
        val locale = Locale.getDefault()
        val _month: Int = currentSelectedPersianDate.shMonth
        return when (_month) {
            1 -> String.format(locale, "March - April %d", currentSelectedPersianDate.grgYear)
            2 -> String.format(locale, "April - May %d", currentSelectedPersianDate.grgYear)
            3 -> String.format(locale, "May - June %d", currentSelectedPersianDate.grgYear)
            4 -> String.format(locale, "June - July %d", currentSelectedPersianDate.grgYear)
            5 -> String.format(locale, "July - August %d", currentSelectedPersianDate.grgYear)
            6 -> String.format(locale, "August - September %d", currentSelectedPersianDate.grgYear)
            7 -> String.format(locale, "September - October %d", currentSelectedPersianDate.grgYear)
            8 -> String.format(locale, "October - November %d", currentSelectedPersianDate.grgYear)
            9 -> String.format(locale, "November - December %d", currentSelectedPersianDate.grgYear)
            10 -> String.format("December %s - January %s ", currentSelectedPersianDate.grgYear, currentSelectedPersianDate.grgYear + 1)
            11 -> String.format(locale, "January - February %d", currentSelectedPersianDate.grgYear)
            12 -> String.format(locale, "February - March %d", currentSelectedPersianDate.grgYear)
            else -> String.format(locale, "%s %d", currentSelectedPersianDate.shMonth, currentSelectedPersianDate.shYear)
        }
    }

    internal fun addToRangeList(day: Int) {
        val tmpDate = PersianDate(currentSelectedPersianDate.toDate())
        tmpDate.setShDay(day)?.startOfDay()

        if (minSelectedDate == null && maxSelectedDate == null) {
            _minSelectedDate.value = tmpDate.startOfDay()
        } else if (minSelectedDate != null && maxSelectedDate == null) {
            if (tmpDate < minSelectedDate!!) {
                _maxSelectedDate.value = minSelectedDate
                _minSelectedDate.value = tmpDate.startOfDay()
            } else {
                _maxSelectedDate.value = tmpDate.startOfDay()
            }
            initRangDates()
        } else if (minSelectedDate != null && maxSelectedDate != null) {
            _minSelectedDate.value = null
            _maxSelectedDate.value = null
            _selectedDatesRange.value.clear()
            addToRangeList(day)
        }

    }

    private fun initRangDates() {
        val startDate = minSelectedDate?.toDate()
        val endDate = maxSelectedDate?.toDate()
        if (startDate == null || endDate == null) {
            throw IllegalArgumentException("start date or end date is null")
        }
        val datesInRange = mutableListOf<PersianDate>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        while (!calendar.after(endCalendar)) {
            val result = calendar.time
            datesInRange.add(PersianDate(result))
            calendar.add(Calendar.DATE, 1)
        }

        _selectedDatesRange.value.addAll(datesInRange)
    }


    fun updateYearRange(value: Int) {
        _yearRange.value = value
        initDate()
    }


    fun updateMinYear(value: Int) {
        _minYear.value = value
    }

    fun updateMaxYear(value: Int) {
        _maxYear.value = value
    }

    internal fun initDate() {

        if (minYear > currentSelectedPersianDate.shYear) {
            updateMinYear(currentSelectedPersianDate.shYear - yearRange)
        }

        if (maxYear < currentSelectedPersianDate.shYear) {
            updateMaxYear(currentSelectedPersianDate.shYear + yearRange)
        }

        val startDate = PersianDate().setShYear(minYear).toDate()
        val endDate = PersianDate().setShYear(maxYear).toDate()
        if (startDate == null || endDate == null) {
            throw IllegalArgumentException("start date or end date is null")
        }
        val datesInRange = mutableListOf<PersianDate>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        while (!calendar.after(endCalendar)) {
            val result = calendar.time
            datesInRange.add(PersianDate(result).startOfDay())
            calendar.add(Calendar.MONTH, 1)
        }

        _dates.value.addAll(datesInRange)

        val firstDate =
            dates.find {
                it.shYear == PersianDate().shYear && it.shMonth == PersianDate().shMonth
            }
        _initialPagee.value = dates.indexOf(firstDate) + 1
    }

}