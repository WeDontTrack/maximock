package org.wedonttrack.utils;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.URL;

public class JsonHelper {

    public static String getJsonObjectFromFile(String filePath) {
        final InputStreamReader inputStreamReader;
        ClassLoader classLoader = JsonHelper.class.getClassLoader();
        URL resource = classLoader.getResource(filePath);

        if (resource == null) {
            throw new IllegalArgumentException(filePath + " file is not found");
        } else {
            JSONObject jsonObject = null;
            try {
                inputStreamReader = new InputStreamReader(resource.openStream());
                JSONParser jsonParser = new JSONParser();
                Object obj = jsonParser.parse(inputStreamReader);
                jsonObject = (JSONObject) obj;
                inputStreamReader.close();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error in reading file: " + filePath);
            } finally {
                return jsonObject.toString();
            }
        }
    }
}