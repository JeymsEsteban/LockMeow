package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.ArrayList;

public class alarmActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<appModel> appModelList = new ArrayList<>();
    appAdapter adapter;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_alarm);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.alarm), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recyclerView = findViewById(R.id.recycleView);
        adapter = new appAdapter(appModelList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        loadingDialog.setContentView(R.layout.loading_apps);
        loadingDialog.setCancelable(false);

        TextView title = loadingDialog.findViewById(R.id.dialogTitle);
        TextView message = loadingDialog.findViewById(R.id.dialogMessage);
        title.setText("Buscando Apps");
        message.setText("Cargando");
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingDialog.show();
        new LoadAppsTask().execute();
    }

    public void volverBoton(View view) {
        Intent volver = new Intent(alarmActivity.this, MainActivity.class);
        startActivity(volver);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAppsInstaladas() {
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);

        appModelList.clear();

        for (PackageInfo packageInfo : packageInfos) {
            String name = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfo.applicationInfo.loadIcon(getPackageManager());
            String packName = packageInfo.packageName;

            appModelList.add(new appModel(name, icon, 0, packName));
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadAppsTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getAppsInstaladas();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            loadingDialog.dismiss();
        }
    }
}
