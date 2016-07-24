// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information
package cat.mvmike.minimalcalendarwidget;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.text.style.RelativeSizeSpan;
import android.widget.RemoteViews;

import cat.mvmike.minimalcalendarwidget.activity.CalendarActivity;
import cat.mvmike.minimalcalendarwidget.resolver.CalendarResolver;
import cat.mvmike.minimalcalendarwidget.resolver.dto.InstanceDTO;
import cat.mvmike.minimalcalendarwidget.util.DayUtil;
import cat.mvmike.minimalcalendarwidget.util.PermissionsUtil;
import cat.mvmike.minimalcalendarwidget.util.WeekDayHeaderUtil;

public class MonthWidget extends AppWidgetProvider {

    private static final int INTENT_CODE_CALENDAR = 1;

    private static final String WIDGET_PRESS = "action.WIDGET_PRESS";

    private static final String MONTH_FORMAT = "MMMM";

    private static final String YEAR_FORMAT = "yyyy";

    private static final String HEADER_DATE_FORMAT = MONTH_FORMAT + " " + YEAR_FORMAT;

    private static final float HEADER_RELATIVE_YEAR_SIZE = 0.8f;

    private static final int FIRST_DAY_OF_WEEK = Calendar.MONDAY;

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        super.onUpdate(context, appWidgetManager, appWidgetIds);

        PermissionsUtil.checkPermissions(context);

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        drawWidgets(context, appWidgetManager, appWidgetIds, rv);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        super.onReceive(context, intent);

        String action = intent.getAction();
        if (WIDGET_PRESS.equals(action))
            CalendarActivity.startCalendarApplication(context);

        forceRedraw(context);
    }

    private void drawWidgets(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds,
        final RemoteViews rv) {

        for (int appWidgetId : appWidgetIds)
            drawWidget(context, appWidgetManager, appWidgetId, rv);
    }

    private void drawWidget(final Context context, final AppWidgetManager appWidgetManager, final int appWidgetId, final RemoteViews rv) {

        Calendar cal = Calendar.getInstance();

        Calendar[] safeDateSpan = CalendarResolver.getSafeDateSpan(cal);
        Set<InstanceDTO> instanceSet = PermissionsUtil.checkPermStatus(context)
            ? CalendarResolver.readAllInstances(context.getContentResolver(), safeDateSpan[0], safeDateSpan[1])
            : new HashSet<InstanceDTO>();

        // SET MONTH AND YEAR
        String monthAndYear = String.valueOf(DateFormat.format(HEADER_DATE_FORMAT, cal));
        SpannableString ss = new SpannableString(monthAndYear);
        ss.setSpan(new RelativeSizeSpan(HEADER_RELATIVE_YEAR_SIZE), monthAndYear.length() - YEAR_FORMAT.length(), monthAndYear.length(), 0);
        rv.setTextViewText(R.id.month_year_label, ss);

        rv.removeAllViews(R.id.calendar_widget);

        // SET DAYS OF WEEK (HEADERS)
        RemoteViews headerRowRv = new RemoteViews(context.getPackageName(), R.layout.row_header);
        WeekDayHeaderUtil.setCellHeaderWeekDays(headerRowRv, FIRST_DAY_OF_WEEK, context);
        rv.addView(R.id.calendar_widget, headerRowRv);

        // SET INDIVIDUAL DAYS
        DayUtil.setDays(context, cal, FIRST_DAY_OF_WEEK, ss, rv, instanceSet);

        // LISTENER FOR WIDGET PRESS
        rv.setOnClickPendingIntent(R.id.calendar_widget, PendingIntent.getBroadcast(context, INTENT_CODE_CALENDAR,
            new Intent(context, MonthWidget.class).setAction(WIDGET_PRESS), PendingIntent.FLAG_UPDATE_CURRENT));

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    private void forceRedraw(final Context context) {

        if (!PermissionsUtil.checkPermStatus(context))
            return;

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.widget);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName name = new ComponentName(context, this.getClass());
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(name);

        drawWidgets(context, appWidgetManager, appWidgetIds, rv);
    }
}