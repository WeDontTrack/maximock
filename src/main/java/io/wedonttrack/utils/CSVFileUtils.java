package io.wedonttrack.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

        public String getMappingId() { return this.mappingId; }
        public String getUrlPathPattern() { return this.urlPathPattern; }
        public String getRequestMethod() { return this.requestMethod; }

        public void setMappingId(String mappingId) { this.mappingId = mappingId; }
        public void setUrlPathPattern(String urlPathPattern) { this.urlPathPattern = urlPathPattern; }
        public void setRequestMethod(String requestMethod) { this.requestMethod = requestMethod; }

        @Override
        public String toString(){
            return this.mappingId + "," + this.urlPathPattern + "," + this.requestMethod;
        }
    }

    public void writeToCSV(List<CSVRow> rows, String filePath){
        LOGGER.info("Writing data into csv file: {}", filePath);
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

    public List<String[]> readCSVFile(String filePath){
        List<String[]> content = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while( (line = br.readLine()) != null) {
                content.add(line.split(","));
            }
        } catch (IOException err){
            LOGGER.info("Error: {}", err.getMessage());
            err.printStackTrace();
        }
        return content;
    }

    public List<String[]> readCSVData(String filePath, boolean createIfNotFound){
        List<String[]> rows = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while((line = br.readLine()) != null){
                rows.add(line.split(","));
            }
        } catch (FileNotFoundException err){
            if(createIfNotFound){
                new CSVFileUtils().writeToCSV(null, filePath);
                rows = readCSVData(filePath, false);
            }
        }
        catch (IOException err){
            LOGGER.error("Error reading file: {}", err.getMessage());
        }
        return rows;
    }

    public String getMappingIdOfUrlPathPattern(String filePath, String urlPathPattern){
        List<String[]> contents = this.readCSVFile(filePath);
        for(String[] row: contents){
            if(row.length >= 2 && row[1].equals(urlPathPattern)){
                return row[0];
            }
        }
        return null;
    }

    public void setMappingIdOfUrlPathPattern(String mappingIdCSVFile, String urlPattern, String mappingId) {
        List<String[]> contents = this.readCSVData(mappingIdCSVFile, false);
        boolean found = false;
        for(String[] row: contents){
            if(row.length >= 2 && row[1].equals(urlPattern)){
                row[0] = mappingId;
                found = true;
                break;
            }
        }
        if (!found) {
            String[] newRow = {mappingId, urlPattern, ""};
            contents.add(newRow);
        }
        List<CSVRow> rows = new ArrayList<>();
        for(String[] content : contents){
            rows.add(new CSVRow(content[0], content[1], content[2]));
        }
        this.writeToCSV(rows, mappingIdCSVFile);
    }
}
