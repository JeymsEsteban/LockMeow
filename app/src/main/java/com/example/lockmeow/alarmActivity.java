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
import android.view.View;
import android.view.Window;
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
import android.widget.ImageView;
import android.widget.PopupMenu;

public class alarmActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<appModel> appModelList = new ArrayList<>();
    private appAdapter adapter;
    private Dialog loadingDialog;
    private final Context context = this;
    private boolean appsLoaded = false;

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

        ImageView permisosMenu = findViewById(R.id.permisosMenu);
        permisosMenu.setOnClickListener(v -> showPopupMenu(v));


        if (!AccesoPermitido() || !Settings.canDrawOverlays(this)) {
            Toast.makeText(alarmActivity.this, "Activa los permisos :)", Toast.LENGTH_LONG).show();
        }

    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(alarmActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.permission_menu, popupMenu.getMenu());


        popupMenu.setOnMenuItemClickListener(item -> {

            int itemId = item.getItemId();
            if (itemId == R.id.permission_usage) {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" + getPackageName())));
                return true;
            } else if (itemId == R.id.permission_overlay) {
                startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())));
                return true;
            }
            return false;
        });

        popupMenu.show();
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
        if(!appsLoaded){
            loadingDialog.show();
            new LoadAppsTask().execute();
            appsLoaded = true;
        }

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