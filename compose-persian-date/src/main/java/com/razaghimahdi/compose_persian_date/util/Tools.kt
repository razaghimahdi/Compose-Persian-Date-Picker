/*
 * Copyright (C) 2022 razaghimahdi (Mahdi Razzaghi Ghaleh)
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
import android.graphics.drawable.ColorDrawable
import android.widget.NumberPicker
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


internal object Tools {
    private val persianNumbers = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
    private val englishNumbers = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

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