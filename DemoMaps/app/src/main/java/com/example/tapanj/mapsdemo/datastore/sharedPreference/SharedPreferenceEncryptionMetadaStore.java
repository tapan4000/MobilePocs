package com.example.tapanj.mapsdemo.datastore.sharedPreference;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SharedPreferenceEncryptionMetadaStore {
    private static List<String> keysWithEncryptionEnabled;
    static {
        keysWithEncryptionEnabled = new ArrayList<>();
        keysWithEncryptionEnabled.add(SharedPreferenceConstants.UserAuthInfoKeyName);
    }
    
    public static boolean IsKeyEncryptionEnabled(String key){
        for (String keyWithEncryptionEnabled: keysWithEncryptionEnabled) {
            if(keyWithEncryptionEnabled.equals(key)){
                return true;
            }
        }

        return false;
    }
}
