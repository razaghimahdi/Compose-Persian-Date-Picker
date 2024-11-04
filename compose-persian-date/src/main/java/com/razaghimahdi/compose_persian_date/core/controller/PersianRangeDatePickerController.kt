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

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.razaghimahdi.compose_persian_date.core.model.PDate
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

    private var _dateListCollection: MutableState<List<PersianDate>> = mutableStateOf(listOf())
    internal val dateListCollection get() = _dateListCollection.value


    private var _selectedDatesRange: MutableState<List<PDate>> = mutableStateOf(listOf())
    internal val selectedDatesRange get() = _selectedDatesRange.value


    private var _date: MutableState<PersianDate> = mutableStateOf(PersianDate())
    internal val date get() = _date.value


    private var _showDateList: MutableState<List<PDate>> = mutableStateOf(listOf())
    internal val showDateList get() = _showDateList.value

    private var _currentSelectedPersianDate: MutableState<PersianDate> =
        mutableStateOf(PersianDate().startOfDay())
    internal val currentSelectedPersianDate get() = _currentSelectedPersianDate.value


    private var _minSelectedDate: MutableState<PDate?> = mutableStateOf(null)
    internal val minSelectedDate get() = _minSelectedDate.value


    private var _selectedYear: MutableState<Int> = mutableIntStateOf(getPersianYear())
    internal val selectedYear get() = _selectedYear.value

    private var _selectedMonth: MutableState<Int> = mutableIntStateOf(getPersianMonth())
    internal val selectedMonth get() = _selectedMonth.value

    private var _selectedDay: MutableState<Int> = mutableIntStateOf(getPersianDay())
    internal val selectedDay get() = _selectedDay.value

    private var _maxSelectedDate: MutableState<PDate?> = mutableStateOf(null)
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
            10 -> String.format(
                "December %s - January %s ",
                currentSelectedPersianDate.grgYear,
                currentSelectedPersianDate.grgYear + 1
            )

            11 -> String.format(locale, "January - February %d", currentSelectedPersianDate.grgYear)
            12 -> String.format(locale, "February - March %d", currentSelectedPersianDate.grgYear)
            else -> String.format(
                locale,
                "%s %d",
                currentSelectedPersianDate.shMonth,
                currentSelectedPersianDate.shYear
            )
        }
    }

    internal fun addToRangeList(date: PDate) {

        Log.i("AppDebug", "addToRangeList date:  "+date)

        val tmpDate = date.persianDate

        if (minSelectedDate == null && maxSelectedDate == null) {
            _minSelectedDate.value = PDate(value = tmpDate.startOfDay().shDay, persianDate = tmpDate.startOfDay(),isSelected = false)
        } else if (minSelectedDate != null && maxSelectedDate == null) {
            if (tmpDate < minSelectedDate?.persianDate!!) {
                _maxSelectedDate.value = minSelectedDate
                _minSelectedDate.value = PDate(value = tmpDate.startOfDay().shDay, persianDate = tmpDate.startOfDay(),isSelected = false)
            } else {
                _maxSelectedDate.value = PDate(value = tmpDate.startOfDay().shDay, persianDate = tmpDate.startOfDay(),isSelected = false)
            }
            initRangDates()
        } else if (minSelectedDate != null && maxSelectedDate != null) {
            _minSelectedDate.value = null
            _maxSelectedDate.value = null
            _selectedDatesRange.value = emptyList()
            addToRangeList(date)
        }

    }

    private fun initRangDates() {
        val startDate = minSelectedDate?.persianDate?.toDate()
            ?: throw IllegalArgumentException("start date is null")
        val endDate = maxSelectedDate?.persianDate?.toDate()
            ?: throw IllegalArgumentException("end date is null")


        val datesInRange = arrayListOf<PDate>()
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        val endCalendar = Calendar.getInstance()
        endCalendar.time = endDate

        while (!calendar.after(endCalendar)) {
            val result = calendar.time
            datesInRange.add(
                PDate(
                    persianDate = PersianDate(result),
                    isSelected = false,
                    value = PersianDate(result).shDay
                )
            )
            calendar.add(Calendar.DATE, 1)
        }

            // _selectedDatesRange.value.addAll(datesInRange)
        _selectedDatesRange.value = datesInRange

    }


    fun updateYearRange(value: Int) {
        _yearRange.value = value
        initDate()
    }


    fun getPersianYear(): Int = date.shYear

    fun getPersianMonth(): Int = date.shMonth

    fun getPersianDay(): Int = date.shDay

    fun updateMinYear(value: Int) {
        _minYear.value = value
    }

    fun updateMaxYear(value: Int) {
        _maxYear.value = value
    }

    private fun updateSelectedYear(value: Int) {
        _selectedYear.value = value
    }

    private fun updateSelectedMonth(value: Int) {
        _selectedMonth.value = value
    }

    private fun updateSelectedDay(value: Int) {
        _selectedDay.value = value
    }

    internal fun initDate() {

        if (minYear > selectedYear) {
            updateMinYear(selectedYear - yearRange)
        }

        if (maxYear < selectedYear) {
            updateMaxYear(selectedYear + yearRange)
        }

        if (selectedYear > maxYear) {
            updateSelectedYear(maxYear)
        }
        if (selectedYear < minYear) {
            updateSelectedYear(minYear)
        }

        if (selectedMonth in 7..11 && selectedDay == 31) {
            updateSelectedDay(30)
        } else {
            val isLeapYear = date.isLeap(selectedYear)
            if (isLeapYear && selectedDay == 31) {
                updateSelectedDay(30)
            } else if (selectedDay > 29) {
                updateSelectedDay(29)
            }
        }

        configureDateCollectionList()

        val firstDate =
            dateListCollection.find {
                it.shYear == PersianDate().shYear && it.shMonth == PersianDate().shMonth
            }
        _initialPagee.value = dateListCollection.indexOf(firstDate)

        configureDateCalendar()
    }

    internal fun updateCurrentDate(page: Int) {
        val date = dateListCollection[page]
        _currentSelectedPersianDate.value = date
        configureDateCalendar()

    }

    private fun configureDateCalendar() {

        val list =
            (1..getFirstNameDayOfWeek()).map { -1 } + (1..currentSelectedPersianDate.monthLength) + (1..getLastNameDayOfWeek()).map { -1 }
        val newList = arrayListOf<PDate>()
        list.forEach {
            val date = PersianDate(currentSelectedPersianDate.toDate())
            if (it > 0) date.setShDay(it).startOfDay()
            newList.add(PDate(value = it, persianDate = date, isSelected = false))
        }
        _showDateList.value = newList


    }

    private fun configureDateCollectionList() {

        val minDate = PersianDate()
        minDate.setShYear(minYear)
        minDate.setShMonth(1)
        minDate.setShDay(1)
        minDate.startOfDay()
        val maxDate = PersianDate()
        maxDate.setShYear(maxYear)
        maxDate.setShMonth(12)
        maxDate.setShDay(1)
        maxDate.startOfDay()

        val datesByYearAndMonth = mutableListOf<Pair<Int, Int>>()

        val calendar = Calendar.getInstance()
        calendar.time = minDate.toDate()!!


        while (calendar.time.before(maxDate.toDate()!!) || calendar.time == maxDate.toDate()!!) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-based in Calendar, so add 1
            datesByYearAndMonth.add(year to month)

            // Move to the next month
            calendar.add(Calendar.MONTH, 1)
        }

        val list = mutableListOf<PersianDate>()
        datesByYearAndMonth.forEach { date ->

            val cal = Calendar.getInstance()
            cal.set(date.first, date.second, 1)

            val pDate = PersianDate(cal.time)
            list.add(pDate)
        }


        _dateListCollection.value = list


        val currentDate =
            dateListCollection.find { it.shYear == PersianDate().shYear && it.shMonth == PersianDate().shMonth }

        _currentSelectedPersianDate.value =
            currentDate ?: throw IllegalArgumentException("current date not found!")

    }


}