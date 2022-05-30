package com.ihsanmkls.storyapp.helper

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object DateFormat {
    fun String.dateFormatting(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = format.parse(this) as Date

        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }
}