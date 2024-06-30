package org.wedonttrack.runner;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.wedonttrack.postman.EnvDetails;
import org.wedonttrack.utils.CommonUtils;
import org.wedonttrack.utils.Constants;
import org.wedonttrack.postman.FormatJsonBody;
import org.wedonttrack.utils.FileFinder;
import org.wedonttrack.config.PropertiesManager;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;



public class RunCollection {
    private static final Logger LOGGER = LogManager.getLogger(RunCollection.class);

    private String envPropertiesFile;
    private String collectionPackage;
    private String mappingIdCSVFile;

    private final OkHttpClient client = new OkHttpClient().newBuilder().build();

    public RunCollection(){
        this.envPropertiesFile = "sample.properties";
        this.collectionPackage = "resources/mocks";
        this.mappingIdCSVFile = "sample.csv";
        LOGGER.info("Using default properties file: {} and collection package: {}, reference mappings id file: {}", this.envPropertiesFile, this.collectionPackage, this.mappingIdCSVFile);
    }

    public RunCollection(String envPropertiesFile, String collectionPackage, String mappingIdCSVFile){
        this.envPropertiesFile = envPropertiesFile;
        this.collectionPackage = collectionPackage;
        this.mappingIdCSVFile = mappingIdCSVFile;
        LOGGER.info("Using properties file: {} and collection package: {} and reference mappingId file: {}", this.envPropertiesFile, this.collectionPackage, this.mappingIdCSVFile);
    }

    public String getCollectionPackage() { return this.collectionPackage; }
    public String getEnvPropertiesFile() { return envPropertiesFile; }
    public String getMappingIdCSVFile() { return this.mappingIdCSVFile; }

    public void setCollectionPackage(String collectionPackage) { this.collectionPackage = collectionPackage; }
    public void setEnvPropertiesFile(String envPropertiesFile) { this.envPropertiesFile = envPropertiesFile; }
    public void setMappingIdCSVFile(String mappingIdCSVFile) { this.mappingIdCSVFile = mappingIdCSVFile; }


    public void run(){
        LOGGER.info("Running collections with default collections and env values");
        this.run(this.collectionPackage, this.envPropertiesFile, this.mappingIdCSVFile);
    }

    public void run(String collectionPackage, String envPropertiesFile, String mappingIdCSVFile) {
        //for each collection - find mustache files - for each file - run wiremock mapping + assert response code
        this.setEnvPropertiesFile(envPropertiesFile);
        this.setCollectionPackage(collectionPackage);
        this.setMappingIdCSVFile(mappingIdCSVFile);
        LOGGER.info("Loading envProp file: {}, collectionProp file: {}, mappingId file: {}", this.envPropertiesFile, this.collectionPackage, this.mappingIdCSVFile);
        EnvDetails envDetails = new EnvDetails(this.envPropertiesFile);

        LOGGER.info("Running collection: {}", collectionPackage);
        FileFinder fileFinder = new FileFinder();
        List<String> mustacheFiles = fileFinder.findMustacheFiles(collectionPackage); //fileFinder.findMustacheFiles("src/main/resources/mocks");
        LOGGER.info("Found {} mustache files", mustacheFiles.size());
        LOGGER.info("Files: {}", mustacheFiles.toString());

        mustacheFiles.forEach(file ->{
            try {
                Response response = mapWiremock(file, null);
                if(assertResponse(response)){
                    LOGGER.info("Response code: {}", response.code());
                    assert response.body() != null;
                    LOGGER.info("Response body: {}", response.body().toString());
                } else {
                    LOGGER.error("Response code: {}", response.code());
                    assert response.body() != null;
                    LOGGER.error("Response body: {}", response.body().toString());
                }
            } catch (IOException e) {
                e.fillInStackTrace();
            }

        });

    }

    public Response mapWiremock(String mustacheFile, Map<String, String> headers) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        String body = FormatJsonBody.format(mustacheFile);
        //read -

        String id = new CommonUtils().generateRandomUUID();
        //String to json object
        JSONObject jsonObject = new JSONObject(body);
        jsonObject.put("id", id);
        DocumentContext context = JsonPath.parse(jsonObject);
        String urlPattern = context.read("$.request.urlPathPattern");
        String method = context.read("$.request.method");
        LOGGER.info("Mapping wiremock for: {} {} with id: {}", method, urlPattern, id);
        LOGGER.info("Mapping wiremock for: {} {}", method, urlPattern);

        RequestBody requestBody = RequestBody.create(Constants.JSON_MEDIA_TYPE, jsonObject.toString());
        String newMappingUrl = PropertiesManager.getProperty(Constants.ENV_URL) + Constants.NEW_MAPPING_BASEPATH;

        requestBuilder
                .url(newMappingUrl)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json");

        headers.forEach(requestBuilder::addHeader);

        Request request = requestBuilder.build();
        Response response = client.newCall(request).execute();

        return response;
    }

    public void getAllMappings(){
        Request.Builder requestBuilder = new Request.Builder();
        String queryParams = "?limit=100&offset=0";
        String getMappingsUrl = PropertiesManager.getProperty(Constants.ENV_URL) + Constants.GET_MAPPINGS_BASEPATH + queryParams;
        requestBuilder
                .url(getMappingsUrl)
                .method("GET", null);
    }

    public boolean assertResponse(Response response){
        return response.code() == 201;
        //add custom assertions if required
    }

    //add Proxy option - if necessary

    //add option to update mock based on mock-id


    //add id to mock -

}
