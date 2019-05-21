package com.example.tapanj.mapsdemo.datastore.sharedPreference.Interfaces;

import android.content.SharedPreferences;
import com.google.gson.reflect.TypeToken;

public interface ISharedPreferenceProvider {
    <TPayload> TPayload FetchSharedPreference(SharedPreferences sharedPreferences, String keyName, TypeToken<TPayload> responseType);
    <TPayload> void StoreSharedPreference(SharedPreferences sharedPreferences, String keyName, TPayload payload);
}
