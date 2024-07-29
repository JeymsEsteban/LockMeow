package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;

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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        //Jeyms: Inicializamos braviables necesarias para volver al login, verificar si ya existe el usuario, Y LAS VARIABLES QUE SE USARAN PARA LOS BOTONES DEL HOME

        //Login var
        auth = FirebaseAuth.getInstance();

        textView = findViewById(R.id.user_detail);
        user = auth.getCurrentUser();

        //Home var


        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setText(user.getEmail());
        }

        //metodo para ajustar a la pantalla la actividad principal
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Inicializar variable para tomar referencia de la barra de navegacion
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavegationViews);
        bottomNavigationView.setSelectedItemId(R.id.btn_home);
        //metodo que permite captar cuando un item de la barra es tocado
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //con ayuda del identificador se hace un if dependiendo de lo que toque el usuario cambia de pantalla
                if (id == R.id.btn_home){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.logoutbtn){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                }
                if (id == R.id.SettingsBtn){
                    startActivity(new Intent(getApplicationContext(), CalendarActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                if (id == R.id.alarmsButtonBtn){
                    startActivity(new Intent(getApplicationContext(), alarmActivity.class));
                    overridePendingTransition(0, 0);
                    return true;
                }
                return false;

            }
        });
        //Funcionalidad de cada boton



    }
    private void dissmisSplashScreen() {
        // Jeyms: Retrasar la actualizacion del estado isReady
        new Handler().postDelayed(() -> {
            // Jeyms: Establecer isReady en true despues de 1000 milisegundos (1 segundo)
            isReady = true;
        }, 1000);

    }
    public void confiBoton(View view) {

    }
}