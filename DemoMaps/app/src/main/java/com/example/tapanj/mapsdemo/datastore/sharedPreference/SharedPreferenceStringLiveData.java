package com.example.tapanj.mapsdemo.datastore.sharedPreference;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class SharedPreferenceStringLiveData extends SharedPreferenceLiveData<String> {

    public SharedPreferenceStringLiveData(SharedPreferences sharedPrefs, String key, String defaultValue) {
        super(sharedPrefs, key, defaultValue);
    }

    @Override
    String getValueFromPreferences(String key, String defaultValue) {
        return sharedPrefs.getString(key, defaultValue);
    }
}
