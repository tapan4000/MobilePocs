package com.example.tapanj.mapsdemo.common.converters;

import android.arch.persistence.room.TypeConverter;

import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/*public class OffsetDateTimeConverter {
    private static DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(String value){
        return formatter.parse(value, OffsetDateTime::from);
    }

    @TypeConverter
    public static String fromOffsetDateTime(OffsetDateTime dateTime){
        return dateTime.format(formatter);
    }
}*/

public class OffsetDateTimeConverter {
    @TypeConverter
    public static OffsetDateTime toOffsetDateTime(int value){
        return OffsetDateTime.now();
    }

    @TypeConverter
    public static int fromOffsetDateTime(OffsetDateTime dateTime){
        return 12;
    }
}

