package com.example.lockmeow;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {
    Boolean isReady = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        View content =  findViewById(android.R.id.content);
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (isReady){
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                }
                dissmisSplashScreen();
                return false;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    private void dissmisSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isReady = true;
            }
        }, 1000);
    }
}