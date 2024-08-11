package com.example.lockmeow;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    Button permisosBtn;
    final Context con = this;

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


        permisosBtn = (Button) findViewById(R.id.Permission);
        permisosBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
            }

        });
        if (AccesoPermitido()){

        } else {
            Toast.makeText(alarmActivity.this, "Activa los permisos :)",Toast.LENGTH_LONG).show();
        }
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 1234);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AccesoPermitido()) {
            SharedPreferencies.getInstance(this).agregarAppBloqueada("com.ejemplo.appbloqueada", this);

            Intent intent = new Intent(this, FloatingService.class);
            intent.putExtra("packageName", "com.ejemplo.appbloqueada");
            startService(intent);
        }
        else{

        }
        loadingDialog.show();
        new LoadAppsTask().execute();
    }

    public void volverBoton(View view) {
        Intent volver = new Intent(alarmActivity.this, MainActivity.class);
        startActivity(volver);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getAppsInstaladas() {

        List<String> list = SharedPreferencies.getInstance(con).getListString();
        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packageInfos.size();i++){
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packName = packageInfos.get(i).packageName;

            if(!list.isEmpty()) {
                if (list.contains(packName)) {
                    appModelList.add(new appModel(name, icon, 1, packName));
                } else {
                    appModelList.add(new appModel(name, icon, 0, packName));
                }
            }
            else {
                appModelList.add(new appModel(name, icon, 0, packName));
            }
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

    private boolean AccesoPermitido(){
        try{
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(),0);
            AppOpsManager appOpsManager = null;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT){
                appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            }

            int mode = 0;

            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT){
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch(PackageManager.NameNotFoundException e){
            return false;
        }

    }
}
