package com.example.lockmeow;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Stats extends AppCompatActivity {
    private UsageStatsManager usageStatsManager;
    private TextView textViewTotalUsage, ronConCola, StatsTime;
    private BarChart barChart;
    private DatabaseReference database;
    String uid, gato;
    ImageView Catface;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        textViewTotalUsage = findViewById(R.id.textViewTotalUsage);
        ronConCola = findViewById(R.id.textViewMostUsedApps);
        barChart = findViewById(R.id.chart);


        // Instanciamos UsageStatsManager
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        // Verificamos si el permiso está otorgado, si no, lo mandamos a que lo dé
        if (!hasUsageStatsPermission()) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        } else {
            showMostUsedApps();
        }
    }

    private boolean hasUsageStatsPermission() {
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000 * 60 * 60 * 24;
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        return !(usageStatsList == null || usageStatsList.isEmpty());
    }

    private void showMostUsedApps() {
        Calendar calendar = Calendar.getInstance();

        // Establecer la hora a las 00:00 del día actual
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startTime = calendar.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            // Ordenar por tiempo de uso de las mas usadas a la menos usada
            Collections.sort(usageStatsList, new Comparator<UsageStats>() {
                @Override
                public int compare(UsageStats o1, UsageStats o2) {
                    return Long.compare(o2.getTotalTimeInForeground(), o1.getTotalTimeInForeground());
                }
            });

            List<BarEntry> entries = new ArrayList<>();
            StringBuilder mostUsedApps = new StringBuilder();

            double totalTime = 0;

            // Mostrar las 5 aplicaciones más usadas y crrear valores que tomara el grafico
            for (int i = 0; i < Math.min(5, usageStatsList.size()); i++) {
                UsageStats usageStats = usageStatsList.get(i);
                mostUsedApps.append(i + 1).append(". ").append(usageStats.getPackageName()).append("\n");
                // Convertir el tiempo de milisegundos a horas con un decimal
                double hours = usageStats.getTotalTimeInForeground() / (1000.0 * 60 * 60);
                entries.add(new BarEntry(i, (float) hours));
                totalTime += hours;
            }
            ronConCola.setText(mostUsedApps.toString());
            BarDataSet dataSet = new BarDataSet(entries, "Aplicaciones más usadas");
            BarData barData = new BarData(dataSet);
            barChart.setData(barData);
            barChart.getDescription().setEnabled(false);

            // Configuración del eje X
            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setDrawGridLines(false);
            xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"1", "2", "3", "4", "5"}));
            xAxis.setGranularity(1f);

            // Configuración del eje Y
            YAxis yAxisLeft = barChart.getAxisLeft();
            yAxisLeft.setAxisMinimum(0f);
            yAxisLeft.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(Locale.getDefault(), "%.1f horas", value);
                }
            });

            // Deshabilitar el eje derecho
            barChart.getAxisRight().setEnabled(false);
            // Actualizar gráfico
            barChart.getLegend().setEnabled(false);
            barChart.invalidate();
            textViewTotalUsage.setText("Tiempo total de uso: " + String.format(Locale.getDefault(), "%.1f", totalTime) + " horas");
            settextgato(totalTime);
        }

    }

    private void settextgato(double Totaltime) {
        StatsTime = findViewById(R.id.StatsComent);
        Catface = findViewById(R.id.Catface);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        uid = user.getUid();
        database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = database.child("users").child(uid).child("Gato");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //el nodo existe
                    Catface.setVisibility(View.VISIBLE);
                    gato = dataSnapshot.getValue().toString();
                    if (Totaltime > 7) {
                        StatsTime.setText("Has usado tu teléfono por más de 7 horas, no tienes remedio intenta bloquear algunas aplicaciones");
                        switch (gato){
                            case "1":
                                Catface.setImageResource(R.drawable.gatonegroenojado);
                                break;
                            case "2":
                                Catface.setImageResource(R.drawable.gatogrisenojado);
                                break;
                            case "3":
                                Catface.setImageResource(R.drawable.gatomonoenojado);
                                break;
                            case "4":
                                Catface.setImageResource(R.drawable.gatoeuropeoenojado);
                                break;
                            case "5":
                                Catface.setImageResource(R.drawable.gatosiamesenojado);
                                break;
                            case "6":
                                Catface.setImageResource(R.drawable.gatobnenojado);
                                break;

                        }
                    } else if (Totaltime > 5) {
                        StatsTime.setText("Has usado tu teléfono por más de cinco horas, eso me entristece!");
                        switch (gato){
                            case "1":
                                Catface.setImageResource(R.drawable.gatonegrotriste);
                                break;
                            case "2":
                                Catface.setImageResource(R.drawable.gatogristriste);
                                break;
                            case "3":
                                Catface.setImageResource(R.drawable.gatomonotriste);
                                break;
                            case "4":
                                Catface.setImageResource(R.drawable.gatoeuropeotriste);
                                break;
                            case "5":
                                Catface.setImageResource(R.drawable.gatosiamestriste);
                                break;
                            case "6":
                                Catface.setImageResource(R.drawable.gatobntriste);
                                break;

                        }
                    }else {
                        StatsTime.setText("Muy bien no has usado mucho tu telefono, eso me hace feliz!");
                        switch (gato){
                            case "1":
                                Catface.setImageResource(R.drawable.gatonegrosonriendo);
                                break;
                            case "2":
                                Catface.setImageResource(R.drawable.gatogrissonriendo);
                                break;
                            case "3":
                                Catface.setImageResource(R.drawable.gatomonosonriendo);
                                break;
                            case "4":
                                Catface.setImageResource(R.drawable.gatoeuropeosonriendo);
                                break;
                            case "5":
                                Catface.setImageResource(R.drawable.gatosiamessonriendo);
                                break;
                            case "6":
                                Catface.setImageResource(R.drawable.gatobnsonriendo);
                                break;

                        }


                    }

                } else {
                    Toast.makeText(Stats.this, "No ha seleccionado un gato", Toast.LENGTH_SHORT).show();
                    Catface.setVisibility(View.INVISIBLE);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Stats.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();

            }


        });

    }


}
