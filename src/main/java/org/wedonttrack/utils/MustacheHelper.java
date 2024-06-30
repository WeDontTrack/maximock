package org.wedonttrack.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringWriter;
import java.util.Map;

public class MustacheHelper {
    static MustacheFactory mf = new DefaultMustacheFactory();

    public static String generateFileFromTemplateUsingMap(String filePath, Map<String, String> data) {
        Mustache mustache = mf.compile(filePath);

        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, data);

        return stringWriter.toString();
    }

    public static String generateStringFromTemplateUsingObject(String filePath, Object object) {
        Mustache mustache = mf.compile(filePath);
        StringWriter stringWriter = new StringWriter();
        mustache.execute(stringWriter, object);
        return stringWriter.toString();
    }
}
