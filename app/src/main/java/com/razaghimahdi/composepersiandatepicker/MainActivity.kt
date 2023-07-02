package com.razaghimahdi.composepersiandatepicker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.razaghimahdi.compose_persian_date.PersianDatePickerDialog
import com.razaghimahdi.compose_persian_date.core.rememberPersianDatePicker
import com.razaghimahdi.composepersiandatepicker.ui.theme.ComposePersianDatePickerTheme
import java.time.LocalDate
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePersianDatePickerTheme {
                val rememberPersianDatePicker = rememberPersianDatePicker()
                val showDialog = remember { mutableStateOf(false) }

                 rememberPersianDatePicker.updateDate(date = Date())
                 rememberPersianDatePicker.updateDate(timestamp = Date().time)
                rememberPersianDatePicker.updateDate(
                    persianYear = 1403,
                    persianMonth = 7,
                    persianDay = 20
                )


                rememberPersianDatePicker.updateMaxYear(1420)
                rememberPersianDatePicker.updateMinYear(1395)
                rememberPersianDatePicker.updateYearRange(10)
                rememberPersianDatePicker.updateDisplayMonthNames(false)


                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (showDialog.value) {
                        PersianDatePickerDialog(
                            rememberPersianDatePicker,
                            Modifier.fillMaxWidth(),
                            onDismissRequest = { showDialog.value = false },
                            onDateChanged = { year, month, day ->
                                // do something...
                                Log.i("TAG", "onCreate getPersianFullDate: "+rememberPersianDatePicker.getPersianFullDate())
                            })
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Button(onClick = { showDialog.value = true }) {
                            Text(text = "نمایش")
                        }


                        Text(text = rememberPersianDatePicker.getPersianFullDate())
                    }

                }
            }
        }
    }
}
