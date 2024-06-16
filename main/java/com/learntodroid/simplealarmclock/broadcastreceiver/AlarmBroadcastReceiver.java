package com.learntodroid.simplealarmclock.broadcastreceiver;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.learntodroid.simplealarmclock.service.AlarmService;
import com.learntodroid.simplealarmclock.service.RescheduleAlarmsService;

import java.util.Calendar;

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    public static final String MONDAY = "MONDAY";
    public static final String TUESDAY = "TUESDAY";
    public static final String WEDNESDAY = "WEDNESDAY";
    public static final String THURSDAY = "THURSDAY";
    public static final String FRIDAY = "FRIDAY";
    public static final String SATURDAY = "SATURDAY";
    public static final String SUNDAY = "SUNDAY";
    public static final String RECURRING = "RECURRING";
    public static final String TITLE = "TITLE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = String.format("Alarm Reboot");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        } else {
            Log.e("TAG", "onReceive: " );
            String toastText = String.format("Alarm Received");
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            if (!intent.getBooleanExtra(RECURRING, false)) {
                startAlarmService(context, intent);
            } else {
                if (alarmIsToday(intent)) {
                    startAlarmService(context, intent);
                }
                scheduleNextRecurringAlarm(context, intent);
            }
        }
    }

    private boolean alarmIsToday(Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        switch(today) {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(SUNDAY, false);
            default:
                return false;
        }
    }

    private void startAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmService.class);
        intentService.putExtra(TITLE, intent.getStringExtra(TITLE));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }

    private void scheduleNextRecurringAlarm(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int today = calendar.get(Calendar.DAY_OF_WEEK);

        // Find next day for the recurring alarm
        for (int i = 1; i <= 7; i++) {
            int nextDay = (today + i - 1) % 7 + 1;
            if (isRecurringOnDay(intent, nextDay)) {
                // Schedule the alarm for nextDay
                calendar.add(Calendar.DAY_OF_WEEK, i);
                long calcutetime = calendar.getTimeInMillis();
                // Code to set the alarm for the nextDay
                setAlarmForNextDay(context, calendar, intent);
                Log.e("TAG", "scheduleNextRecurringAlarm: " + calcutetime);
                break;
            }
        }
    }

    private boolean isRecurringOnDay(Intent intent, int day) {
        switch(day) {
            case Calendar.MONDAY:
                return intent.getBooleanExtra(MONDAY, false);
            case Calendar.TUESDAY:
                return intent.getBooleanExtra(TUESDAY, false);
            case Calendar.WEDNESDAY:
                return intent.getBooleanExtra(WEDNESDAY, false);
            case Calendar.THURSDAY:
                return intent.getBooleanExtra(THURSDAY, false);
            case Calendar.FRIDAY:
                return intent.getBooleanExtra(FRIDAY, false);
            case Calendar.SATURDAY:
                return intent.getBooleanExtra(SATURDAY, false);
            case Calendar.SUNDAY:
                return intent.getBooleanExtra(SUNDAY, false);
            default:
                return false;
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private void setAlarmForNextDay(Context context, Calendar calendar, Intent intent) {
        // Implement the method to set the alarm for the next day using AlarmManager
        // You can create a new Intent and PendingIntent and set it with AlarmManager

        // Example:
        Intent alarmIntent = new Intent(context, AlarmBroadcastReceiver.class);
        alarmIntent.putExtras(intent.getExtras());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
//        alarmManager.setExact(
//                AlarmManager.RTC_WAKEUP,
//                calendar.getTimeInMillis(),
//                pendingIntent
//        );

        String toastText = String.format("Next Recurring Alarm Scheduled");
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
    }
}
