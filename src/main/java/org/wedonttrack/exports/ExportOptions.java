package org.wedonttrack.exports;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExportOptions {

    private static final Logger LOGGER = LogManager.getLogger(ExportOptions.class);

    //Export all wiremock collections as postman JSON file

    //env values - <name>.postman_environment.json
    //curls - <name>.postman_collection.json
    //global values - <name>.postman_globals.json

    private static final String POSTMAN_EXPORT_VERSION = "v2.1.0";
    private static final String SCHEMA_URL = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";


    public void exportCollection(String exportAs, String collection){
        switch (exportAs){
            case "JSON":
                LOGGER.info("Exporting mocks as json collection");
                exportCollectionAsJson(collection);
            case "SOMETHING":
                LOGGER.info("IDK");
            default:
                LOGGER.info("No input provided, taking no further actions.");
                return;
        }
    }


    public void exportCollectionAsJson(String collection){

    }

}
