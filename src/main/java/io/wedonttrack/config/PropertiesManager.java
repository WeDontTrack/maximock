package io.wedonttrack.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class PropertiesManager {
    private static final Logger LOGGER = LogManager.getLogger(PropertiesManager.class);

    private static final Map<String, String> propertiesMap = new ConcurrentHashMap<>();

    public PropertiesManager(){
        LOGGER.info("Username: {}", System.getProperty("user.name"));
        propertiesMap.put("userName", System.getProperty("user.name"));
    }

    public static Map<String, String> getPropertiesMap() {
        return propertiesMap;
    }

    public static void loadProperties(Properties properties){
        LOGGER.info("Loading 'properties'");
        properties.forEach((key, value) ->{
            LOGGER.info("Loading property: [{}] : [{}]", key, value);
            propertiesMap.put((String) key, (String) value);
        });
    }

    public static void setProperty(String key, String value) {
        LOGGER.info("Setting property: [{}] : [{}]", key, value);
        propertiesMap.put(key, value);
    }

    public static boolean isPropertyExist(String propertyName) {
        LOGGER.debug("isPropertyExist: {}", propertyName);
        return propertiesMap.containsKey(propertyName);
    }

    public static String getProperty(String propertyName) {
        LOGGER.debug("getProperty: {}", propertyName);
        isPropertyExist(propertyName);
        return (String)propertiesMap.get(propertyName);
    }

    public static String getProperty(String propertyName, String defaultValue) {
        LOGGER.debug("getProperty (with default value): {}", propertyName);
        if (isPropertyExist(propertyName)) {
            String propertyValue = (String)propertiesMap.get(propertyName);
            LOGGER.debug("Returning value: {}", propertyValue);
            return propertyValue;
        } else {
            LOGGER.debug("Returning default value: {}", defaultValue);
            return defaultValue;
        }
    }
}
