package com.razaghimahdi.compose_persian_date.core.model

import saman.zamani.persiandate.PersianDate

data class PDate(
    val value: Int,
    val persianDate: PersianDate,
    val isSelected: Boolean = false
)
