package com.example.lockmeow;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencies {

    private static final String SHARED_APP_PREFERENCE_NAME = "SharedRef";
    private SharedPreferences pref;

    public SharedPreferencies(Context context) {
        this.pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferencies getInstance(Context context) {
        return new SharedPreferencies(context);
    }

    public void putString(String key, String value) {
        pref.edit().putString(key, value).apply();
    }

    public void putInteger(String key, int value) {
        pref.edit().putInt(key, value).apply();
    }

    public String getString(String key) {
        return pref.getString(key, "");
    }

    public int getInteger(String key) {
        return pref.getInt(key, 0);
    }

    public void putListString(List<String> list) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("listSize", list.size());
        for (int i = 0; i < list.size(); i++) {
            editor.putString("app_" + i, list.get(i));
        }
        editor.apply();
    }

    public void agregarAppDesbloqueada(String packageName, Context context) {
        List<String> appsBloqueadas = getListString();
        if (appsBloqueadas.contains(packageName)) {
            appsBloqueadas.remove(packageName);
            putListString(appsBloqueadas);
        }
    }

    public List<String> getListString() {
        int size = getInteger("listSize");
        List<String> temp = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            temp.add(getString("app_" + i));
        }
        return temp;
    }

    public void agregarAppBloqueada(String packageName, Context context) {
        List<String> appsBloqueadas = getListString();
        if (!appsBloqueadas.contains(packageName)) {
            appsBloqueadas.add(packageName);
            putListString(appsBloqueadas);
        }
    }
}