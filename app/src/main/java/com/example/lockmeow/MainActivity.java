package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Boolean isReady = false;
    ConfiFragment confiFragment = new ConfiFragment();
    ImageView gatoA;
    ImageView gatoB;
    ImageView gatoC;
    ImageView gatoD;
    ImageView gatoE;
    ImageView gatoF;

    private SharedViewModel sharedViewModel;
    private SharedPreferences sharedPreferences;
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
        user = auth.getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
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
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, confiFragment).commit();
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
        // Configurar el ViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        gatoA = findViewById(R.id.gatoA);
        gatoB = findViewById(R.id.gatoB);
        gatoC = findViewById(R.id.gatoC);
        gatoD = findViewById(R.id.gatoD);
        gatoE = findViewById(R.id.gatoE);
        gatoF = findViewById(R.id.gatoF);
        sharedPreferences = getSharedPreferences("com.example.lockmeow", MODE_PRIVATE);

        // Restaurar el estado de visibilidad desde SharedPreferences
        restoreGatoVisibility();

        // Observa los cambios en el ViewModel
        sharedViewModel.getButtonClicked().observe(this, buttonId -> {
            hideAllGatos();
            switch (buttonId) {
                case 1:
                    gatoA.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean("gatoAVisible", true).apply();
                    break;
                case 2:
                    gatoB.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean("gatoBVisible", true).apply();
                    break;
                case 3:
                    gatoC.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean("gatoCVisible", true).apply();
                    break;
                case 4:
                    gatoD.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean("gatoDVisible", true).apply();
                    break;
                case 5:
                    gatoE.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean("gatoEVisible", true).apply();
                    break;
                case 6:
                    gatoF.setVisibility(View.VISIBLE);
                    sharedPreferences.edit().putBoolean("gatoFVisible", true).apply();
                    break;

            }
        });
    }

    private void restoreGatoVisibility() {
        if (sharedPreferences.getBoolean("gatoAVisible", false)) {
            gatoA.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("gatoBVisible", false)) {
            gatoB.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("gatoCVisible", false)) {
            gatoC.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("gatoDVisible", false)) {
            gatoD.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("gatoEVisible", false)) {
            gatoE.setVisibility(View.VISIBLE);
        }
        if (sharedPreferences.getBoolean("gatoFVisible", false)) {
            gatoF.setVisibility(View.VISIBLE);
        }

    }

    private void hideAllGatos() {
        gatoA.setVisibility(View.INVISIBLE);
        gatoB.setVisibility(View.INVISIBLE);
        gatoC.setVisibility(View.INVISIBLE);
        gatoD.setVisibility(View.INVISIBLE);
        gatoE.setVisibility(View.INVISIBLE);
        gatoF.setVisibility(View.INVISIBLE);

        // Actualizar SharedPreferences
        sharedPreferences.edit()
                .putBoolean("gatoAVisible", false)
                .putBoolean("gatoBVisible", false)
                .putBoolean("gatoCVisible", false)
                .putBoolean("gatoDVisible", false)
                .putBoolean("gatoEVisible", false)
                .putBoolean("gatoFVisible", false)
                .apply();
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