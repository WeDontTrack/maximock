package org.wedonttrack.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wedonttrack.utils.Constants;

import java.util.Properties;
import java.util.Random;

public class WiremockData {
    private static final Logger LOGGER = LogManager.getLogger(WiremockData.class);
    long randomNum = new Random().nextLong((Constants.MAX_NUM - Constants.MIN_NUM) + 1) + Constants.MIN_NUM;

    public Properties properties = new Properties();

    public WiremockData() {
        LOGGER.info("Creating global parameters");
        properties.put("randomNum", randomNum);

        LOGGER.info("Loading properties into PropertiesManager");

        this.loadProperties();
    }

    public void loadProperties(){
        PropertiesManager.loadProperties(this.properties);
    }

    public void addParameter(String key, String value){
        LOGGER.info("Loading parameter: [{}] : [{}]", key, value);
        this.properties.put(key, value);
    }

    public void addParameters(Properties properties){
        this.properties.putAll(properties);
    }

}