package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class alarmActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<appModel> appModelList = new ArrayList<>();
    private appAdapter adapter;
    private Dialog loadingDialog;
    private final Context context = this;
    private final List<String> mappedApps = List.of("com.google.android.youtube");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setupLoadingDialog();

        Button permisosBtn = findViewById(R.id.Permission);
        permisosBtn.setOnClickListener(v -> startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)));

        if (!AccesoPermitido()) {
            Toast.makeText(alarmActivity.this, "Activa los permisos :)", Toast.LENGTH_LONG).show();
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        }
    }

    private void setupLoadingDialog() {
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
        ApplockService.start(this);
        loadingDialog.show();
        new LoadAppsTask().execute();
    }

    private void getAppsInstaladas() {
        List<String> blockedApps = SharedPreferencies.getInstance(context).getListString();
        List<ResolveInfo> packageInfos = getPackageManager().queryIntentActivities(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0);
        Set<String> addedPackages = new HashSet<>();

        for (ResolveInfo resolveInfo : packageInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            String appName = resolveInfo.loadLabel(getPackageManager()).toString();
            Drawable appIcon = resolveInfo.loadIcon(getPackageManager());
            boolean isBlocked = blockedApps.contains(packageName);

            appModelList.add(new appModel(appName, appIcon, isBlocked ? 1 : 0, packageName));
            addedPackages.add(packageName);
        }

        for (String mappedPackageName : mappedApps) {
            if (!addedPackages.contains(mappedPackageName)) {
                String mappedAppName = getAppNameFromPackageName(mappedPackageName);
                Drawable mappedAppIcon = getAppIconFromPackageName(mappedPackageName);
                appModelList.add(new appModel(mappedAppName, mappedAppIcon, 0, mappedPackageName));
            }
        }
    }

    private String getAppNameFromPackageName(String packageName) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, 0);
            return getPackageManager().getApplicationLabel(appInfo).toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return packageName;
        }
    }

    private Drawable getAppIconFromPackageName(String packageName) {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(packageName, 0);
            return getPackageManager().getApplicationIcon(appInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return getDrawable(R.drawable.cathome);
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

    private boolean AccesoPermitido() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}