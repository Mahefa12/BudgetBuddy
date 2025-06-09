package com.example.budgetbuddy_prog7313.utils

import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.ChronoUnit

object DateUtils {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

    fun getCurrentMonthRange(): Pair<ZonedDateTime, ZonedDateTime> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val firstOfMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS)
        val lastOfMonth = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).truncatedTo(ChronoUnit.DAYS)
        return Pair(firstOfMonth, lastOfMonth)
    }

    fun getLastMonthRange(): Pair<ZonedDateTime, ZonedDateTime> {
        val now = ZonedDateTime.now(ZoneId.systemDefault())
        val firstOfLastMonth = now.minusMonths(1).withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS)
        val lastOfLastMonth = now.withDayOfMonth(1).minusDays(1).truncatedTo(ChronoUnit.DAYS)
        return Pair(firstOfLastMonth, lastOfLastMonth)
    }

    fun formatDate(date: ZonedDateTime): String = date.format(formatter)
    
    fun formatMonth(date: ZonedDateTime): String = date.format(monthFormatter)

    fun parseDate(dateStr: String): ZonedDateTime = 
        LocalDate.parse(dateStr, formatter).atStartOfDay(ZoneId.systemDefault())
} 