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
        // Jeyms: Instalar y mostrar el splash screen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        View content =  findViewById(android.R.id.content);
        // Jeyms: Agregar un listener para detectar cuando la vista esté lista para ser dibujada
        content.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                //Jeyms: Verificar si el contenido esta listo
                if (isReady){
                    content.getViewTreeObserver().removeOnPreDrawListener(this);
                    // Jeyms: Si está listo, eliminar el listener
                }
                dissmisSplashScreen();
                return false;
            }
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    private void dissmisSplashScreen() {
        // Jeyms: Retrasar la actualizacion del estado isReady
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Jeyms: Establecer isReady en true despues de 1000 milisegundos (1 segundo)
                isReady = true;
            }
        }, 1000);
    }
}