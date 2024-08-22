package com.example.lockmeow;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class ApplockService extends Service {

    private static final String TAG = "ApplockService";
    private Handler handler;
    private static PendingIntent pendingIntent;
    private Map<String, Boolean> lockedPackages;
    private ActivityManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ActivityManager.RunningTaskInfo getTopTask() {
        return this.manager.getRunningTasks(1).get(0);
    }

    private boolean init() {
        this.manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        handler = new Handler();
        lockedPackages = new HashMap<>();
        SharedPreferencies.getInstance(this).getListString().forEach(packageName -> {
            lockedPackages.put(packageName, true);
            Log.d(TAG, "Aplicación añadida a la lista de bloqueos: " + packageName);
        });
        startAlarm(this);
        return true;
    }

    private static void startAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent repeatedIntent = getRunIntent(context);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 5000, repeatedIntent);
    }

    private static PendingIntent getRunIntent(Context context) {
        if (pendingIntent == null) {
            Intent intent = new Intent(context, ApplockService.class);
            intent.setAction("com.pk.applock.applock_service.start");
            pendingIntent = PendingIntent.getService(context, 1193135, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        }
        return pendingIntent;
    }

    public static void start(Context context) {
        startAlarm(context);
    }

    private void showLocker(String packageName) {
        Log.d(TAG, "Mostrando la superposición de bloqueo para: " + packageName);
        Intent intent = new Intent(this, blockActivity.class);
        intent.putExtra("blockedAppPackage", packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void checkPackageChanged() {
        String currentPackageName = getTopPackageName();
        Log.d(TAG, "Aplicación actual: " + currentPackageName);
        if (!currentPackageName.equals(getPackageName())) {  // Solo verificar si no es la misma app
            onAppOpen(currentPackageName);
        }
    }

    private String getTopPackageName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 10, time);

            if (stats != null && !stats.isEmpty()) {
                SortedMap<Long, UsageStats> sortedStats = new TreeMap<>();
                for (UsageStats usageStats : stats) {
                    sortedStats.put(usageStats.getLastTimeUsed(), usageStats);
                }
                return sortedStats.get(sortedStats.lastKey()).getPackageName();
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(1);
            if (!taskInfo.isEmpty()) {
                return taskInfo.get(0).topActivity.getPackageName();
            }
        }
        return "";
    }

    private void onAppOpen(String packageName) {
        if (lockedPackages.containsKey(packageName)) {
            onLockedAppOpen(packageName);
        }
    }

    private void onLockedAppOpen(String packageName) {
        Log.d(TAG, "Intentando bloquear la aplicación: " + packageName);
        if (Boolean.TRUE.equals(lockedPackages.get(packageName))) {
            Log.d(TAG, "Aplicación bloqueada: " + packageName);
            showLocker(packageName);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "ApplockService se ha iniciado.");
        if (intent == null || "com.pk.applock.applock_service.start".equals(intent.getAction())) {
            if (!init()) {
                init();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkPackageChanged();
                    handler.postDelayed(this, 500); // Verificar medio segundo
                }
            }, 10);
            return START_STICKY;
        }
        return START_NOT_STICKY;
    }
}
