package io.wedonttrack.utils;

import org.json.JSONObject;

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
                jsonObject = new JSONObject(inputStreamReader);
                inputStreamReader.close();
            } catch (Exception e) {
                throw new IllegalArgumentException("Error in reading file: " + filePath);
            } finally {
                return jsonObject.toString();
            }
        }
    }
}