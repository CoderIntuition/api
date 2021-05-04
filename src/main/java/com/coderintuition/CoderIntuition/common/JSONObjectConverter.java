package com.coderintuition.CoderIntuition.common;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.json.JSONException;
import org.json.JSONObject;

@Converter
public class JSONObjectConverter implements AttributeConverter<JSONObject, String> {
    @Override
    public String convertToDatabaseColumn(JSONObject jsonData) {
        if (jsonData == null) {
            return "";
        }
        return jsonData.toString();
    }

    @Override
    public JSONObject convertToEntityAttribute(String jsonDataAsJson) {
        if (jsonDataAsJson == null || jsonDataAsJson.isEmpty()) {
            return null;
        }

        JSONObject jsonData;
        try {
            jsonData = new JSONObject(jsonDataAsJson);
        } catch (JSONException ex) {
            jsonData = null;
        }
        return jsonData;
    }
}
