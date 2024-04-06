/*
 * Copyright (C) 2023 razaghimahdi (Mahdi Razzaghi Ghaleh)
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


package com.razaghimahdi.compose_persian_date.util

import android.content.res.Resources.NotFoundException
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.widget.EditText
import android.widget.NumberPicker
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.lang.reflect.Field
import java.util.Calendar
import java.util.Date


internal object Tools {
    private val persianNumbers = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')



    fun Date.isDateToday(): Boolean {
        val calendar = Calendar.getInstance()
        val today = Calendar.getInstance()

        calendar.time = this
        today.set(Calendar.HOUR_OF_DAY, 0)
        today.set(Calendar.MINUTE, 0)
        today.set(Calendar.SECOND, 0)
        today.set(Calendar.MILLISECOND, 0)

        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
    }


    internal fun String.toPersianNumber(): String {
        if (this.isEmpty()) return ""
        val out = StringBuilder()
        val length = this.length
        for (i in 0 until length) {
            when (val c = this[i]) {
                in '0'..'9' -> {
                    val number = c.toString().toInt()
                    out.append(persianNumbers[number])
                }
                '٫' -> {
                    out.append('،')
                }
                else -> {
                    out.append(c)
                }
            }
        }
        return out.toString()
    }

    internal fun String.toEnglishNumber(): String {
        if (this.isEmpty()) return ""
        val out = StringBuilder()
        val length = this.length
        for (i in 0 until length) {
            val c = this[i]
            var charPos: Int
            if (c.hasCharachter().also { charPos = it } != -1) {
                out.append(englishNumbers[charPos])
            } else if (c == '،') {
                out.append('٫')
            } else {
                out.append(c)
            }
        }
        return out.toString()
    }

    private fun Char.hasCharachter(): Int {
        for (i in persianNumbers.indices) {
            if (this == persianNumbers[i]) {
                return i
            }
        }
        return -1
    }

    internal fun Int.hexToString() = String.format("#%06X", 0xFFFFFF and this)

    internal fun getStringColor(color: Color): String {
        return color.toArgb().hexToString()
    }

    internal fun changeDividerColor(picker: NumberPicker, color: Int) {
        val pickerFields = NumberPicker::class.java.declaredFields
        for (pf in pickerFields) {
            if (pf.name == "mSelectionDivider") {
                pf.isAccessible = true
                try {
                    val colorDrawable = ColorDrawable(color)
                    pf[picker] = colorDrawable
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: NotFoundException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
                break
            }
        }
    }

}


internal fun NumberPicker.changeTextColor(color: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        val count: Int = childCount
        for (i in 0 until count) {
            val child: View = getChildAt(i)
            if (child is EditText) {
                try {
                    child.setTextColor(color)
                    invalidate()
                    val selectorWheelPaintField: Field = this.javaClass.getDeclaredField("mSelectorWheelPaint")
                    var accessible: Boolean = selectorWheelPaintField.isAccessible
                    selectorWheelPaintField.isAccessible = true
                    (selectorWheelPaintField.get(this) as Paint).color = color
                    selectorWheelPaintField.isAccessible = accessible
                    invalidate()
                    val selectionDividerField: Field = this.javaClass.getDeclaredField("mSelectionDivider")
                    accessible = selectionDividerField.isAccessible()
                    selectionDividerField.isAccessible = true
                    selectionDividerField.set(this, null)
                    selectionDividerField.isAccessible = accessible
                    invalidate()
                } catch (ignore: Exception) { }
            }
        }
    } else {
        textColor = color
    }
}