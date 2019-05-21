package com.example.tapanj.mapsdemo.datastore.sharedPreference;

import android.content.SharedPreferences;
import com.example.tapanj.mapsdemo.datastore.sharedPreference.Interfaces.ISharedPreferenceProvider;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SharedPreferenceProvider implements ISharedPreferenceProvider {
    @Override
    public <TPayload> TPayload FetchSharedPreference(SharedPreferences sharedPreferences, String keyName, TypeToken<TPayload> responseType) {
        String storedPreference = sharedPreferences.getString(keyName, null);
        if(SharedPreferenceEncryptionMetadaStore.IsKeyEncryptionEnabled(keyName)){
            // Perform the decryption of storedPreference.
            storedPreference = storedPreference;
        }

        Gson gson = new Gson();
        // The usage of TypeToken captures the compile time type between <> into a run-time type object, hence the type information is available
        // at runtime.
        TPayload payload = gson.fromJson(storedPreference, responseType.getType());
        return payload;
    }

    @Override
    public <TPayload> void StoreSharedPreference(SharedPreferences sharedPreferences, String keyName, TPayload payload) {
        SharedPreferences.Editor sharedPreferenceEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String serializedObject = gson.toJson(payload);

        if(SharedPreferenceEncryptionMetadaStore.IsKeyEncryptionEnabled(keyName)){
            // Perform the encryptiom of payload.
        }

        sharedPreferenceEditor.putString(keyName, serializedObject);
        sharedPreferenceEditor.commit();
    }
}
