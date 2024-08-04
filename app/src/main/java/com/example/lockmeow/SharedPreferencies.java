package com.example.lockmeow;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class SharedPreferencies {

    private static final String SHARED_APP_PREFERENCE_NAME = "SharedRef";
    Context ctx;
    private SharedPreferences pref;
    private SharedPreferences.Editor shareEditor;

    public SharedPreferencies (Context context) {
        this.pref = context.getSharedPreferences(SHARED_APP_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static  SharedPreferencies getInstance(Context context) {
        return new SharedPreferencies(context);
    }
    public void putString (String key, String value){
        pref.edit().putString(key, value).apply();
    }

    public void putInteger (String key, int value){
        pref.edit().putInt(key, value).apply();
    }

    public String getString (String key){
        return pref.getString (key,"");
    }

    public int getInteger (String key){
        return pref.getInt (key,0);
    }
    public void putListString (List<String> list){
        for(int i  = 0; i < list.size(); i++){
            putString("app_"+i, list.get(i));
        }
        putInteger("listSize", list.size());
    }

    public List<String> getListString() {
        int size = getInteger("listSize");
        List<String> temp = new ArrayList<>();
        for(int i = 0 ; i < size; i++){
            temp.add(getString("app_"+i));
        }
        return temp;
    }
}
