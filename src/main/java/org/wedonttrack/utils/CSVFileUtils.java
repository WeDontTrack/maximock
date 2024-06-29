package org.wedonttrack.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

public class CSVFileUtils {
    private static final Logger LOGGER = LogManager.getLogger(CSVFileUtils.class);

    public static class CSVRow{
        private String mappingId;
        private String urlPathPattern;
        private String requestMethod;

        public CSVRow(String mappingId, String urlPathPattern, String requestMethod){
            this.mappingId = mappingId;
            this.urlPathPattern = urlPathPattern;
            this.requestMethod = requestMethod;
        }

        @Override
        public String toString(){
            return this.mappingId + "," + this.urlPathPattern + "," + this.requestMethod;
        }
    }

    public void writeToCSV(List<CSVRow> rows, String filePath){
        try(PrintWriter writer = new PrintWriter(filePath)){
            writer.println("mappingId,urlPathPattern,requestMethod");
            for(CSVRow row: rows) {
                writer.println(row.toString());
            }
        } catch (FileNotFoundException err){
            LOGGER.error("Error writing to file: {}", err.getMessage());
        } catch (Exception err){
            LOGGER.error("Error: {}", err.getMessage());
            err.printStackTrace();
        }
    }
}
