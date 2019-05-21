package com.example.tapanj.mapsdemo.common.converters;

import android.arch.persistence.room.TypeConverter;
import org.threeten.bp.Instant;

public class InstantConverter {
    @TypeConverter
    public static Instant toInstant(String value){
        return Instant.parse(value);
    }

    @TypeConverter
    public static String fromInstant(Instant instant){
        return instant.toString();
    }
}
