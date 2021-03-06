package com.adveisor.g2.monopoly.engine.service.model;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

public class Json {

    static Gson gson = new Gson();

    public static String toJson(Object object){
        return gson.toJson(object);
    }

    public static <T> T toString(String s, Class<T> clas){
        T output = null;
        try {
            output = gson.fromJson(s, clas);
        } catch(JsonSyntaxException | JsonIOException e){
            e.printStackTrace();
        }
        return output;
    }
}
