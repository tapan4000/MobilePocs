package com.example.tapanj.mapsdemo.integration.Retrofit;

import com.example.tapanj.mapsdemo.common.Utility.Utility;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

public class GsonDateDeserializer implements JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Date parsedDate = Utility.getDateFromString(json.getAsString());
        if(null == parsedDate){
            throw new JsonParseException("Unparsable Date: " + json.getAsString() + "Supported formats include ISO time with and without milliseconds.");
        }

        return parsedDate;
    }
}
