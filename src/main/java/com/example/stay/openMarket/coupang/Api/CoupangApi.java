package com.example.stay.openMarket.coupang.Api;

import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.coupang.hmac.HmacGenerater;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
//import jdk.jpackage.internal.Log;
import org.apache.catalina.util.URLEncoder;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class CoupangApi {

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * POST
     * @param strRequest
     * @param strPath
     */
    public JSONObject coupangPostApi(String strRequest, String strPath){
        JSONObject returnJson = null;

        URIBuilder uriBuilder = new URIBuilder()
                .setPath(Constants.cpUrl + strPath)
//                    .addParameter("searchStartDateTime", "20130501000000")
//                    .addParameter("searchEndDateTime", "20130530000000")
                .addParameter("offset", "0")
                .addParameter("limit", "100");

        LogWriter logWriter = new LogWriter("POST", uriBuilder.getPath(), System.currentTimeMillis());

        try {
            String authorization = HmacGenerater.generate("POST", uriBuilder.build().toString(), Constants.cpSecretKey, Constants.cpAccessKey);

            uriBuilder.setScheme("https").setHost(Constants.cpHost).setPort(Constants.cpPort);

            HttpPost post = new HttpPost ( uriBuilder.build ().toString () );
            post.addHeader ( "Authorization", authorization);
            post.addHeader ( "Content-Type", "application/json; charset=UTF-8");
            post.addHeader ( "Request-Vendor-Id", Constants.cpVendorId);

            if(strRequest != null){
                StringEntity entity = new StringEntity (strRequest);

                logWriter.addRequest(strRequest);

                post.setEntity (entity);
            }

            returnJson =  httpExecute(post);

            if(returnJson != null){
                logWriter.add(gson.toJson(returnJson));
            }

            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return returnJson;
    }

    /**
     * PUT
     * @param strRequest
     * @param strPath
     */
    public JSONObject coupangPutApi(String strRequest, String strPath){
        String method = "PUT";
        JSONObject returnJson = null;

        URIBuilder uriBuilder = new URIBuilder ( )
                .setPath (Constants.cpUrl + strPath);

        LogWriter logWriter = new LogWriter("PUT", uriBuilder.getPath(), System.currentTimeMillis());
        try {
            String authorization = HmacGenerater.generate ( method, uriBuilder.build ().toString (), Constants.cpSecretKey, Constants.cpAccessKey );

            uriBuilder.setScheme ( "https" ).setHost(Constants.cpHost).setPort ( Constants.cpPort );

            HttpPut put = new HttpPut ( uriBuilder.build ().toString () );
            put.addHeader ( "Authorization", authorization );
            put.addHeader ( "Content-Type", "application/json; charset=UTF-8" );
            put.addHeader ( "Request-Vendor-Id", Constants.cpVendorId );

            StringEntity entity = new StringEntity (strRequest);

            logWriter.addRequest(strRequest);

            put.setEntity (entity);

            returnJson = httpExecute(put);

            if(returnJson != null){
                logWriter.add(gson.toJson(returnJson));
            }

            logWriter.log(0);
        } catch ( Exception e ) {
            e.printStackTrace ( );
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return returnJson;
    }

    /**
     * GET
     * @param strPath
     */
    public JSONObject coupangGetApi(String strPath) {
        URIBuilder uriBuilder = new URIBuilder()
                .setPath(Constants.cpUrl + strPath)
//                    .addParameter("searchStartDateTime", "20130501000000")
//                    .addParameter("searchEndDateTime", "20130530000000")
                .addParameter("offset", "0")
                .addParameter("limit", "100");

        LogWriter logWriter = new LogWriter("GET", uriBuilder.getPath(), System.currentTimeMillis());

        JSONObject returnJson = null;
        try {
            String authorization = HmacGenerater.generate("GET", uriBuilder.build().toString(), Constants.cpSecretKey, Constants.cpAccessKey);

            uriBuilder.setScheme("https").setHost(Constants.cpHost).setPort(Constants.cpPort);

            HttpGet get = new HttpGet(uriBuilder.build().toString());
            get.addHeader("Authorization", authorization);
            get.addHeader("Content-type", "application/json; charset=UTF-8");
            get.addHeader("Accept-Charset", "UTF-8");
            get.addHeader("Request-Vendor-Id", Constants.cpVendorId);

            returnJson = httpExecute(get);

            if(returnJson != null){
                logWriter.add(gson.toJson(returnJson));
            }

            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return returnJson;
    }

    /**
     * Patch
     * @param strRequest
     * @param strPath
     */
    public JSONObject coupangPatchApi(String strRequest, String strPath) {
        URIBuilder uriBuilder = new URIBuilder()
                .setPath (Constants.cpUrl + strPath);

        LogWriter logWriter = new LogWriter("PATCH", uriBuilder.getPath(), System.currentTimeMillis());

        JSONObject returnJson = null;
        try {
            String authorization = HmacGenerater.generate ("PATCH", uriBuilder.build().toString(), Constants.cpSecretKey, Constants.cpAccessKey );

            uriBuilder.setScheme ("https").setHost(Constants.cpHost).setPort ( Constants.cpPort );

            HttpPatch patch = new HttpPatch (uriBuilder.build().toString());
            patch.addHeader ("Authorization", authorization);
            patch.addHeader ("Content-Type","application/json; charset=UTF-8");
            patch.addHeader ("Request-Vendor-Id", Constants.cpVendorId);

            logWriter.addRequest(strRequest);

            StringEntity entity = new StringEntity (strRequest,"UTF-8");
            patch.setEntity (entity);

            returnJson = httpExecute(patch);

            if(returnJson != null){
                logWriter.add(gson.toJson(returnJson));
            }

            logWriter.log(0);
        } catch (Exception e) {
            e.printStackTrace ();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return returnJson;
    }

    /**
     * Delete
     * @param strPath
     */
    public JSONObject coupangDeleteApi(String strPath) {
        URIBuilder uriBuilder = new URIBuilder ( )
                .setPath (Constants.cpUrl + strPath);

        LogWriter logWriter = new LogWriter("DELETE", uriBuilder.getPath(), System.currentTimeMillis());

        JSONObject returnJson = null;

        try {
            String authorization = HmacGenerater.generate("DELETE", uriBuilder.build().toString(), Constants.cpSecretKey, Constants.cpAccessKey);

            uriBuilder.setScheme("https").setHost(Constants.cpHost).setPort(Constants.cpPort);

            HttpDelete delete = new HttpDelete ( uriBuilder.build ().toString () );
            delete.addHeader ( "Authorization", authorization );
            delete.addHeader ( "content-type", "application/json; charset=UTF-8" );
            delete.addHeader ( "Request-Vendor-Id", Constants.cpVendorId );

            returnJson =  httpExecute(delete);

            if(returnJson != null){
                logWriter.add(gson.toJson(returnJson));
            }

            logWriter.log(0);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return returnJson;
    }

    public JSONObject httpExecute(HttpUriRequest request) {
        CloseableHttpClient client = null;
        Object objData = null;
        JSONObject returnJson = null;
        try {
            client = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                response = client.execute(request);

                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                JSONParser jsonParser = new JSONParser();
                objData = jsonParser.parse(result);
                returnJson = (JSONObject) objData;

            } catch ( Exception e ) {
                e.printStackTrace ( );
            } finally {
                if (response != null) {
                    try {
                        response.close ( );
                    } catch ( IOException e ) {
                        e.printStackTrace ( );
                    }
                }
            }

        } catch ( Exception e ) {
            e.printStackTrace ( );
        } finally {
            if (client != null) {
                try {
                    client.close ( );
                } catch ( IOException e ) {
                    e.printStackTrace ( );
                }
            }
        }

        return returnJson;
    }

}