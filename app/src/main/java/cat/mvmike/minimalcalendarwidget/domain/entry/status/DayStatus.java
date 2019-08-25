// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.domain.entry.status;

import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public final class DayStatus {

    private static final String DAY_OF_MONTH_DF_PATTERN = "00";

    private final boolean inMonth;

    private final boolean today;

    private final DayOfWeek dayOfWeek;

    private final int dayOfMonth;

    private final int month;

    public DayStatus(final LocalDate localDate, final int todayYear, final int thisMonth, final int dayInYear) {

        boolean inYear = localDate.get(YEAR) == todayYear;
        inMonth = localDate.get(MONTH_OF_YEAR) == thisMonth;
        today = inYear && inMonth && localDate.get(DAY_OF_YEAR) == dayInYear;

        dayOfWeek = localDate.getDayOfWeek();

        dayOfMonth = localDate.get(DAY_OF_MONTH);
        month = localDate.get(MONTH_OF_YEAR);
    }

    public boolean isInMonth() {
        return inMonth;
    }

    public boolean isToday() {
        return today;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public String getDayOfMonthString() {
        return new DecimalFormat(DAY_OF_MONTH_DF_PATTERN).format(dayOfMonth);
    }

    public boolean isSingleDigitDay() {
        return dayOfMonth < 10;
    }

    public boolean isInDay(final Instant startInstant, final Instant endInstant, final boolean allDayInstance) {

        // take out 5 milliseconds to avoid erratic behaviour with full day events (or those that end at 00:00)
        return toLocalDate(startInstant, allDayInstance).get(MONTH_OF_YEAR) <= month
            && toLocalDate(startInstant, allDayInstance).get(DAY_OF_MONTH) <= dayOfMonth
            && toLocalDate(endInstant.minusMillis(5), allDayInstance).get(MONTH_OF_YEAR) >= month
            && toLocalDate(endInstant.minusMillis(5), allDayInstance).get(DAY_OF_MONTH) >= dayOfMonth;
    }

    // calendarProvider uses different set of timezones depending if event is allDay
    private static LocalDateTime toLocalDate(final Instant instant, final boolean allDayInstance) {

        if (allDayInstance) {
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        }

        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }
}
