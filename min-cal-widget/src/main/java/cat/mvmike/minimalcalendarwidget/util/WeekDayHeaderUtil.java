// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.Context;
import android.widget.RemoteViews;

import cat.mvmike.minimalcalendarwidget.R;

public abstract class WeekDayHeaderUtil {

    public static void setCellHeaderWeekDays(final RemoteViews headerRowRv, final int firstDayOfWeek, final Context context) {

        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();

        ThemesUtil.Theme theme = ConfigurationUtil.getTheme(context);

        int current;
        for (int i = 0; i < Calendar.DAY_OF_WEEK; i++) {

            current = (firstDayOfWeek + i) % Calendar.DAY_OF_WEEK == 0 ? firstDayOfWeek + i : (firstDayOfWeek + i) % Calendar.DAY_OF_WEEK;

            if (current == Calendar.SATURDAY)
                headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[current], theme.getCellHeaderSaturday()));
            else if (current == Calendar.SUNDAY)
                headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[current], theme.getCellHeaderSunday()));
            else
                headerRowRv.addView(R.id.row_container, setSpecificWeekDay(context, weekdays[current], theme.getCellHeader()));
        }
    }

    private static RemoteViews setSpecificWeekDay(final Context context, final String text, final int layoutId) {
        RemoteViews dayRv = new RemoteViews(context.getPackageName(), layoutId);
        dayRv.setTextViewText(android.R.id.text1, text);
        return dayRv;
    }
}