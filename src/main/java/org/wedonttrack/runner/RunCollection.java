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
    private final OkHttpClient client = new OkHttpClient().newBuilder().build();
    private final CSVFileUtils csvFileUtils = new csvFileUtils();

    private String envPropertiesFile;
    private String collectionPackage;
    private String mappingIdCSVFile;



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
        List<String> mustacheFiles = fileFinder.findMustacheFiles(collectionPackage); 
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
        //read - mappings from csv file and attach the stub id to the payload, if not present then create one and save it to csv file


        JSONObject jsonObject = new JSONObject(body);
        DocumentContext context = JsonPath.parse(jsonObject);
        String urlPattern = context.read("$.request.urlPathPattern");
        String method = context.read("$.request.method");
        String urlMappingId = csvFileUtils.getMappingIdOfUrlPathPattern(this.mappingIdCSVFile, urlPathPattern);
        if(mappingId == null){
            mappingId = new CommonUtils().generateRandomUUID();
            csvFileUtils.setMappingIdOfUrlPathPattern(this.mappingIdCSVFile, urlPathPattern, mappingId);
        }

        boolean newWiremock = this.getStubMappingById(mappingId);
        if(newWireMock){
            LOGGER.info("Mapping wiremock for: {} {} with id: {}", method, urlPattern, mappingId);
            return this.wireMockAction(mappingId, jsonObject, "NEW");
        }else{
            LOGGER.info("Editing wiremock for: {} {} with id: {}", method, urlPattern, mappingId);
            return this.wireMockAction(mappingId, jsonObject, "EDIT");
        }

        // LOGGER.info("Mapping wiremock for: {} {} with id: {}", method, urlPattern, id);
        // LOGGER.info("Mapping wiremock for: {} {}", method, urlPattern);

        // RequestBody requestBody = RequestBody.create(Constants.JSON_MEDIA_TYPE, jsonObject.toString());
        // String newMappingUrl = PropertiesManager.getProperty(Constants.ENV_URL) + Constants.NEW_MAPPING_BASEPATH;

        // requestBuilder
        //         .url(newMappingUrl)
        //         .method("POST", requestBody)
        //         .addHeader("Content-Type", "application/json");

        // headers.forEach(requestBuilder::addHeader);

        // Request request = requestBuilder.build();
        // Response response = client.newCall(request).execute();

        // return response;
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

    public boolean getStubMappingById(String mappingId) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();
        String getMappingUrl = PropertiesManager.getProperty(Constants.ENV_URL) + Constants.GET_MAPPINGS_BASEPATH + "/" + mappingId;
        requestBuilder
                .url(getMappingUrl)
                .method("GET", null);
        Response response = client.newCall(requestBuilder.build()).execute();
        return response.code() == 200;
        //if id not present then returns 500 status code
        //if id present then 200 status code
    }

    public Response wireMockAction(String mappingId, JSONObject body, String wiremockAction) throws IOException{
        Request.Builder requestBuilder = new Request.Builder();
        RequestBody requestBody = RequestBody.create(Constants.JSON_MEDIA_TYPE, body.toString());
        String editMappingUrl = PropertiesManager.getProperty(Constants.ENV_URL) + Constants.GET_MAPPINGS_BASEPATH + "/" + mappingId;
        body.put("id", mappingId);

        String method = "POST";
        switch (wiremockAction){
            case "EDIT":
                method = "PUT";
                break;
            case "NEW":
                method = "POST";
                break;
            case "DELETE":
                method = "DELETE";
                requestBody = null;
                break;
            default:
                method = "POST";
        }

        requestBuilder
                .url(editMappingUrl)
                .method(method, requestBody)
                .addHeader("Content-Type", "application/json");

        return client.newCall(requestBuilder.build()).execute();
    }

}
