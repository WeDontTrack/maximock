package org.wedonttrack.postman;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.wedonttrack.config.PropertiesManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;


/**
 * The EnvDetails class is responsible for managing environment details.
 * It loads properties from a specified environment properties file.
 */
public class EnvDetails {
    private static final Logger LOGGER = LogManager.getLogger(EnvDetails.class);

    private String envPropertiesFile;
    Properties envProperties = new Properties();

    /**
     * Constructs an EnvDetails object.
     * @param envPropertiesFile the name of the environment properties file
     */
    public EnvDetails(String envPropertiesFile) {
//        this.envPropertiesFile = "src/main/resources/environment/" + envPropertiesFile;
        this.envPropertiesFile = envPropertiesFile;
        LOGGER.info("Loading properties from: {}", this.envPropertiesFile);
        this.loadData();
    }

    public Properties getEnvProperties() { return this.envProperties; }

    public void setEnvPropertiesFile(String envPropertiesFile) {this.envPropertiesFile = envPropertiesFile;}

    /**
     * Loads data from the environment properties file into the Properties object.
     */
    public void loadData(){
        LOGGER.info("Loading Data into properties");
        try{
            this.envProperties.load(ClassLoader.getSystemResourceAsStream(this.envPropertiesFile));
            PropertiesManager.loadProperties(this.envProperties);
        } catch (FileNotFoundException fileError){
            fileError.printStackTrace();
        } catch (IOException err){
            err.printStackTrace();
        }
    }

}

