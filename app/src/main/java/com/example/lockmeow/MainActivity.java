package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button;
    TextView textView;
    FirebaseUser user;
    Boolean isReady = false;
    @SuppressLint("MissingInflatedId")
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
        //Jeyms: Inicializamos braviables necesarias para volver al login, verificar si ya existe el usuario
        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_detail);
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }
        button.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void dissmisSplashScreen() {
        // Jeyms: Retrasar la actualizacion del estado isReady
        new Handler().postDelayed(() -> {
            // Jeyms: Establecer isReady en true despues de 1000 milisegundos (1 segundo)
            isReady = true;
        }, 1000);
    }
}