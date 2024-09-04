package com.example.lockmeow;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Stats extends AppCompatActivity {
    private UsageStatsManager usageStatsManager;
    private TextView textViewTotalUsage, ronConCola, StatsTime;
    private BarChart barChart;


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
        long endTime = System.currentTimeMillis();
        long startTime = endTime - 1000 * 60 * 60 * 24;

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            // Ordenar por tiempo de uso descendente
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
        if (Totaltime > 7) {
            StatsTime.setText("Has usado tu teléfono por más de 7 horas, no tienes remedio intenta bloquear algunas aplicaciones");
        } else if (Totaltime > 5) {
            StatsTime.setText("Has usado tu teléfono por más de cinco horas, eso me enoja!");
        }
    }


}
