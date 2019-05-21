package com.example.tapanj.mapsdemo.datastore.sharedPreference;

import android.arch.lifecycle.LiveData;
import android.content.SharedPreferences;

abstract class SharedPreferenceLiveData<T> extends LiveData<T> {
    SharedPreferences sharedPrefs;
    String key;
    T defaultValue;

    public SharedPreferenceLiveData(SharedPreferences sharedPrefs, String key, T defaultValue){
        this.sharedPrefs = sharedPrefs;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String changedKey) {
            if(SharedPreferenceLiveData.this.key.equals(changedKey)){
                setValue(getValueFromPreferences(changedKey, defaultValue));
            }
        }
    };

    abstract T getValueFromPreferences(String key, T defaultValue);

    @Override
    protected void onActive() {
        super.onActive();
        setValue(getValueFromPreferences(key, defaultValue));
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    @Override
    protected void onInactive() {
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        super.onInactive();
    }
}
