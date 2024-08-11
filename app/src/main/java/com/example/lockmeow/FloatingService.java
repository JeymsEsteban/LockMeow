package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;

import java.util.List;


//API level 34
public class FloatingService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private WindowManager.LayoutParams params;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FloatingService", "Service started");
        String packageName = intent.getStringExtra("packageName");
        if (packageName != null && isAppBlocked(packageName)) {
            Log.d("FloatingService", "App is blocked: " + packageName);
            mostrarOverlay();
        } else {
            Log.d("FloatingService", "App is not blocked or packageName is null.");
        }
        return START_STICKY;
    }

    private boolean isAppBlocked(String packageName) {
        List<String> appsBloqueadas = SharedPreferencies.getInstance(this).getListString();
        return appsBloqueadas.contains(packageName);
    }

    private void mostrarOverlay() {
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.activity_block, null);

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null) mWindowManager.removeView(mFloatingView);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
