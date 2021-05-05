package com.coderintuition.CoderIntuition.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.json.JSONException;
import org.json.JSONArray;

@Converter
public class JSONArrayConverter implements AttributeConverter<JSONArray, String> {
    @Override
    public String convertToDatabaseColumn(JSONArray jsonArray) {
        if (jsonArray == null) {
            return "[]";
        }
        return jsonArray.toString();
    }

    @Override
    public JSONArray convertToEntityAttribute(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new JSONArray();
        }

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(jsonString);
        } catch (JSONException ex) {
            jsonArray = new JSONArray();
        }
        return jsonArray;
    }
}
