package com.ecommerce.shopmitt.base.extentions

import java.util.*

fun Calendar.isSameDay(second: Calendar): Boolean {

    return this[Calendar.YEAR] == second[Calendar.YEAR] && this[Calendar.DAY_OF_YEAR] == second[Calendar.DAY_OF_YEAR]
}

fun Calendar.compareDatesOnly(other: Calendar): Int {

    return when {
        isSameDay(other) -> 0
        before(other) -> -1
        else -> 1
    }
}