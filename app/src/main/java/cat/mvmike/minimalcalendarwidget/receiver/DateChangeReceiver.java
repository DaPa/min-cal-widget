// Copyright (c) 2016, Miquel Martí <miquelmarti111@gmail.com>
// See LICENSE for licensing information

package cat.mvmike.minimalcalendarwidget.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.time.LocalDateTime;

import cat.mvmike.minimalcalendarwidget.MonthWidget;

import static java.time.temporal.ChronoField.DAY_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

public class DateChangeReceiver extends BroadcastReceiver {

    private LocalDateTime lastChecked = LocalDateTime.now();

    @Override
    public void onReceive(final Context context, final Intent intent) {

        LocalDateTime now = LocalDateTime.now();

        if (now.get(YEAR) != lastChecked.get(YEAR)
            || now.get(DAY_OF_YEAR) != lastChecked.get(DAY_OF_YEAR)) {
            MonthWidget.forceRedraw(context);
        }

        lastChecked = now;
    }
}
