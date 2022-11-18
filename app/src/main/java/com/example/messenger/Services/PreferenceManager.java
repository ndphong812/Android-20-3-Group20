package com.example.messenger.Services;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences shp;

    public PreferenceManager(Context context) {
        shp = context.getSharedPreferences("ChatApp", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = shp.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public Boolean getBoolean(String key) {
        return shp.getBoolean(key, false);
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = shp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String getString(String key) {
        return shp.getString(key, null);
    }

    public void clear() {
        SharedPreferences.Editor editor = shp.edit();
        editor.clear();
        editor.apply();
    }
}
