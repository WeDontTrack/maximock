package org.wedonttrack.postman;

import org.wedonttrack.utils.MustacheHelper;
import org.wedonttrack.config.PropertiesManager;

/**
 * The FormatJsonBody class is responsible for formatting JSON bodies.
 * It uses the MustacheHelper class to generate a string from a template using a properties map.
 */
public class FormatJsonBody {

    /**
     * Formats a JSON body using a mustache file.
     * @param mustacheFile the name of the mustache file
     * @return the formatted response body
     */
    public static String format(String mustacheFile){
        return MustacheHelper.generateStringFromTemplateUsingObject(mustacheFile, PropertiesManager.getPropertiesMap());
    }
}
