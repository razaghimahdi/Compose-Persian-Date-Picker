package com.razaghimahdi.composepersiandatepicker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.razaghimahdi.compose_persian_date.bottom_sheet.linear.DatePickerLinearModalBottomSheet
import com.razaghimahdi.compose_persian_date.bottom_sheet.single.DatePickerSingleModalBottomSheet
import com.razaghimahdi.compose_persian_date.core.controller.rememberDialogDatePicker
import com.razaghimahdi.compose_persian_date.core.controller.rememberPersianSingleDatePickerController
import com.razaghimahdi.compose_persian_date.dialog.PersianLinearDatePickerDialog
import com.razaghimahdi.compose_persian_date.dialog.SingleDatePickerDialog
import com.razaghimahdi.composepersiandatepicker.ui.theme.ComposePersianDatePickerTheme
import kotlinx.coroutines.launch
import java.util.Date


class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePersianDatePickerTheme {
                val coroutine = rememberCoroutineScope()
                val rememberPersianDialogDatePicker = rememberDialogDatePicker()
                val rememberPersianDialogSingleDatePickerController = rememberPersianSingleDatePickerController()
                val rememberPersianBottomSheetSingleDatePickerController = rememberPersianSingleDatePickerController()
                val rememberPersianBottomSheetDatePickerController = rememberDialogDatePicker()
                val showLinearDialog = remember { mutableStateOf(false) }
                val bottomLinearSheetState = rememberModalBottomSheetState()
                val showSingleDialog = remember { mutableStateOf(false) }
                val bottomSingleSheetState = rememberModalBottomSheetState()


                LaunchedEffect(key1 = Unit) {

                    rememberPersianDialogSingleDatePickerController.updateDate(date = Date())
                    rememberPersianDialogSingleDatePickerController.updateDate(timestamp = Date().time)
                    rememberPersianDialogSingleDatePickerController.updateDate(
                        persianYear = 1403,
                        persianMonth = 7,
                        persianDay = 20
                    )

                    rememberPersianDialogDatePicker.updateDate(date = Date())
                    rememberPersianDialogDatePicker.updateDate(timestamp = Date().time)
                    rememberPersianDialogDatePicker.updateDate(
                        persianYear = 1403,
                        persianMonth = 7,
                        persianDay = 20
                    )

                    rememberPersianBottomSheetDatePickerController.updateDate(date = Date())
                    rememberPersianBottomSheetDatePickerController.updateDate(timestamp = Date().time)
                    rememberPersianBottomSheetDatePickerController.updateDate(
                        persianYear = 1403,
                        persianMonth = 7,
                        persianDay = 20
                    )
                    rememberPersianBottomSheetSingleDatePickerController.updateDate(date = Date())
                    rememberPersianBottomSheetSingleDatePickerController.updateDate(timestamp = Date().time)
                    rememberPersianBottomSheetSingleDatePickerController.updateDate(
                        persianYear = 1403,
                        persianMonth = 7,
                        persianDay = 20
                    )
                }


                rememberPersianDialogDatePicker.updateMaxYear(1420)
                rememberPersianDialogDatePicker.updateMinYear(1395)
                rememberPersianDialogDatePicker.updateYearRange(10)
                rememberPersianDialogDatePicker.updateDisplayMonthNames(false)


                rememberPersianBottomSheetDatePickerController.updateMaxYear(1420)
                rememberPersianBottomSheetDatePickerController.updateMinYear(1395)

                if (showLinearDialog.value) {
                    PersianLinearDatePickerDialog(
                        rememberPersianDialogDatePicker,
                        Modifier.fillMaxWidth(),
                        onDismissRequest = { showLinearDialog.value = false },
                        onDateChanged = { year, month, day ->
                            // do something...
                            Log.i(
                                "TAG",
                                "onCreate getPersianFullDate: " + rememberPersianDialogDatePicker.getPersianFullDate()
                            )
                        })
                }
                if (showSingleDialog.value) {
                    SingleDatePickerDialog(
                        rememberPersianDialogSingleDatePickerController,
                        Modifier.fillMaxWidth(),
                        onDismissRequest = { showSingleDialog.value = false },
                        onDateChanged = { year, month, day ->
                            // do something...
                            Log.i(
                                "TAG",
                                "onCreate getPersianFullDate: " + rememberPersianDialogSingleDatePickerController.getPersianFullDate()
                            )
                        })
                }

                if (bottomLinearSheetState.isVisible) {
                    DatePickerLinearModalBottomSheet(
                        modifier = Modifier
                            .fillMaxSize(),
                        sheetState = bottomLinearSheetState,
                        controller = rememberPersianBottomSheetDatePickerController,
                        onDismissRequest = {
                            coroutine.launch {
                                bottomLinearSheetState.hide()
                            }
                        }
                    )
                }
                if (bottomSingleSheetState.isVisible) {
                    DatePickerSingleModalBottomSheet(
                        modifier = Modifier
                            .fillMaxSize(),
                        sheetState = bottomSingleSheetState,
                        controller = rememberPersianBottomSheetSingleDatePickerController,
                        onDismissRequest = {
                            coroutine.launch {
                                bottomSingleSheetState.hide()
                            }
                        }
                    )
                }


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Button(onClick = { showLinearDialog.value = true }) {
                            Text(text = "نمایش دیالوگ دیت پیکر")
                        }
                        Text(text = rememberPersianDialogDatePicker.getPersianFullDate())

                        Spacer(modifier = Modifier.size(8.dp))


                        Button(onClick = { showSingleDialog.value = true }) {
                            Text(text = "نمایش دیالوگ سینگل دیت پیکر")
                        }
                        Text(text = rememberPersianDialogSingleDatePickerController.getPersianFullDate())

                        Spacer(modifier = Modifier.size(8.dp))

                        Button(onClick = { coroutine.launch { bottomLinearSheetState.show() } }) {
                            Text(text = "نمایش باتم شت دیت پیکر")
                        }
                        Text(text = rememberPersianBottomSheetDatePickerController.getPersianFullDate())

                        Spacer(modifier = Modifier.size(8.dp))

                        Button(onClick = { coroutine.launch { bottomSingleSheetState.show() } }) {
                            Text(text = "نمایش باتم شت سینگل دیت پیکر")
                        }
                        Text(text = rememberPersianBottomSheetSingleDatePickerController.getPersianFullDate())
                    }

                }
            }
        }
    }
}