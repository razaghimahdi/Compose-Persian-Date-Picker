package com.razaghimahdi.composepersiandatepicker

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.razaghimahdi.compose_persian_date.PersianDataPickerDialog
import com.razaghimahdi.compose_persian_date.core.rememberPersianDataPicker
import com.razaghimahdi.composepersiandatepicker.ui.theme.ComposePersianDatePickerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposePersianDatePickerTheme {
                val rememberPersianDataPicker = rememberPersianDataPicker()
                val showDialog = remember { mutableStateOf(false) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (showDialog.value) {
                        PersianDataPickerDialog(
                            rememberPersianDataPicker,
                            Modifier.fillMaxWidth(),
                            onDismissRequest = { showDialog.value = false },
                            onDateChanged = { year, month, day ->
                               // do something...
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


                        Text(text = rememberPersianDataPicker.getPersianLongDate())
                    }

                }
            }
        }
    }
}
