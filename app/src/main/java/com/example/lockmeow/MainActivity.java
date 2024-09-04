package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    private DatabaseReference database;
    String uid, gato;
    Boolean isReady = false;
    ConfiFragment confiFragment = new ConfiFragment();
    ImageView gatoImageView;
    AnimationDrawable gatoAnimation;
    ImageButton Statsview;


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
        gatoImageView = findViewById(R.id.gatoImageView);
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }else {
            database = FirebaseDatabase.getInstance().getReference();
            //si ya existe el usuario lo que haremos es mirar si ya ese usuario selecciono su gato, si no, no se mostrara nada en pantalla, si sí, pondra que gato se seleccionó
            uid = user.getUid();
            DatabaseReference userRef = database.child("users").child(uid).child("Gato");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        //el nodo existe
                        gatoImageView.setVisibility(View.VISIBLE);
                        gato = dataSnapshot.getValue().toString();
                        switch (gato){
                            case "1":
                                gatoImageView.setImageResource(R.drawable.animaciongatonegro);
                                break;
                            case "2":
                                gatoImageView.setImageResource(R.drawable.animaciongatogris);
                                break;
                            case "3":
                                gatoImageView.setImageResource(R.drawable.animaciongatomono);
                                break;
                            case "4":
                                gatoImageView.setImageResource(R.drawable.animaciongatoeuropeo);
                                break;
                            case "5":
                                gatoImageView.setImageResource(R.drawable.animaciongatosiames);
                                break;
                            case "6":
                                gatoImageView.setImageResource(R.drawable.animaciongatobn);
                                break;

                        }
                        gatoAnimation = (AnimationDrawable) gatoImageView.getDrawable();
                        gatoAnimation.start();




                    } else {
                        Toast.makeText(MainActivity.this, "No ha seleccionado un gato", Toast.LENGTH_SHORT).show();
                        gatoImageView.setVisibility(View.INVISIBLE);
                    }

                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();

                }


            });
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
        Statsview = findViewById(R.id.stats);
        Statsview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Stats.class);
                startActivity(intent);
            }
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