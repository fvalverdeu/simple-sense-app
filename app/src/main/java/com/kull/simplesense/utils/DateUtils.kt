package com.kull.simplesense.utils

import android.util.Log
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    fun getDateTimeInUtc(date: Date): String {
        val dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        var dateFormatUtc: SimpleDateFormat = SimpleDateFormat(dateFormat)
        dateFormatUtc.timeZone = TimeZone.getTimeZone("UTC-4")
        return dateFormatUtc.format(date)
    }

    fun getCurrentUTCDateTimeInISOFormat(): Date? {
        var currentDateTimeInUtc = getDateTimeInUtc(Date())
        Log.e("formatdate", "${currentDateTimeInUtc}")
        var parser: org.joda.time.format.DateTimeFormatter = ISODateTimeFormat.dateTime()
        Log.e("parser", "${parser}")
        var parsedDateTime: DateTime = parser.parseDateTime(currentDateTimeInUtc)
        return parsedDateTime.toDate()
    }
}