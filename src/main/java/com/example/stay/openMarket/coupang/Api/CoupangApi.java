package com.example.stay.openMarket.coupang.Api;

import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.coupang.hmac.HmacGenerater;
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
    /**
     * POST
     * @param strPostBody
     * @param strPath
     */
    public JSONObject coupangPostApi(String strPostBody, String strPath){
        String method = "POST";
        JSONObject resultJson = null;
        try {
            //build uri
            URIBuilder uriBuilder = new URIBuilder()
                    .setPath(strPath)
//                    .addParameter("searchStartDateTime", "20130501000000")
//                    .addParameter("searchEndDateTime", "20130530000000")
                    .addParameter("offset", "0")
                    .addParameter("limit", "100");

            /********************************************************/
            //authorize, demonstrate how to generate hmac signature here
            String authorization = HmacGenerater.generate(method, uriBuilder.build().toString(), Constants.SECRET_KEY, Constants.ACCESS_KEY);
            //print out the hmac key
            System.out.println("authorization : " + authorization);
            /********************************************************/
            uriBuilder.setScheme(Constants.SCHEMA).setHost(Constants.HOST).setPort(Constants.PORT);
            HttpPost post = new HttpPost ( uriBuilder.build ().toString () );
            /********************************************************/
            // set header, demonstarte how to use hmac signature here
            post.addHeader ( "Authorization", authorization);
            post.addHeader ( "Content-Type", "application/json");
            post.addHeader ( "Request-Vendor-Id", Constants.vendorId);
            /********************************************************/

            // set post body here
            StringEntity entity = new StringEntity (strPostBody);
            System.out.println(strPostBody);
            post.setEntity (entity);

            // execute
//            resultJson =  httpExecute(post);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    /**
     * PUT
     * @param strPutBody
     * @param strPath
     */
    public JSONObject coupangPutApi(String strPutBody, String strPath){
        //params
        String method = "PUT";
        JSONObject resultJson = null;
        try {
            //build uri
            URIBuilder uriBuilder = new URIBuilder ( )
                    .setPath ( strPath );
            /********************************************************/
            //authorize, demonstrate how to generate hmac signature here
            String authorization = HmacGenerater.generate ( method, uriBuilder.build ().toString (), Constants.SECRET_KEY, Constants.ACCESS_KEY );
            //print out the hmac key
            System.out.println ( authorization );
            /********************************************************/
            uriBuilder.setScheme ( Constants.SCHEMA ).setHost ( Constants.HOST ).setPort ( Constants.PORT );
            HttpPut put = new HttpPut ( uriBuilder.build ().toString () );
            /********************************************************/
            // set header, demonstarte how to use hmac signature here
            put.addHeader ( "Authorization", authorization );
            put.addHeader ( "Content-Type", "application/json" );
            put.addHeader ( "Request-Vendor-Id", Constants.vendorId );
            /********************************************************/
            // set put request body here

            StringEntity entity = new StringEntity (strPutBody);
            put.setEntity ( entity );
            //execute
//            httpExecute(put);
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
        return resultJson;
    }

    /**
     * GET
     * @param strPath
     */
    public JSONObject coupangGetApi(String strPath) {
        //params
        String method = "GET";
        JSONObject resultJson = null;
        try {
            //build uri
            URIBuilder uriBuilder = new URIBuilder()
                    .setPath(strPath)
//                    .addParameter("searchStartDateTime", "20130501000000")
//                    .addParameter("searchEndDateTime", "20130530000000")
                    .addParameter("offset", "0")
                    .addParameter("limit", "100");

            /********************************************************/
            //authorize, demonstrate how to generate hmac signature here
            String authorization = HmacGenerater.generate(method, uriBuilder.build().toString(), Constants.SECRET_KEY, Constants.ACCESS_KEY);
            //print out the hmac key
            System.out.println("authorization : " + authorization);
            /********************************************************/
            uriBuilder.setScheme(Constants.SCHEMA).setHost(Constants.HOST).setPort(Constants.PORT);
            HttpGet get = new HttpGet(uriBuilder.build().toString());
            /********************************************************/
            // set header, demonstarte how to use hmac signature here
            get.addHeader("Authorization", authorization);
            get.addHeader("content-type", "application/json");
            get.addHeader("Request-Vendor-Id", Constants.vendorId);
            /********************************************************/

            // execute
            httpExecute(get);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    /**
     * Patch
     * @param strPatchBody
     * @param strPath
     */
    public void coupangPatchApi(String strPatchBody, String strPath) {
        //params
        String method = "PATCH";

        try {
            //build uri
            URIBuilder uriBuilder = new URIBuilder ( )
                    .setPath ( strPath );
            /********************************************************/
            //authorize, demonstrate how to generate hmac signature here
            String authorization = HmacGenerater.generate ( method, uriBuilder.build ().toString (), Constants.SECRET_KEY, Constants.ACCESS_KEY );
            //print out the hmac key
            System.out.println ( authorization );
            /********************************************************/
            uriBuilder.setScheme ( Constants.SCHEMA ).setHost ( Constants.HOST ).setPort ( Constants.PORT );
            HttpPatch patch = new HttpPatch ( uriBuilder.build ().toString () );
            /********************************************************/
            // set header, demonstarte how to use hmac signature here
            patch.addHeader ( "Authorization", authorization );
            patch.addHeader ("Content-Type","application/json");
            patch.addHeader ( "Request-Vendor-Id", Constants.vendorId );
            /********************************************************/

            StringEntity entity = new StringEntity ( strPatchBody,"UTF-8" );
            patch.setEntity ( entity );
            //execute
            httpExecute(patch);
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    /**
     * Delete
     * @param strPath
     */
    public void coupangDeleteApi(String strPath) {
        //params
        String method = "DELETE";

        try {
            //build uri
            URIBuilder uriBuilder = new URIBuilder ( )
                    .setPath ( strPath );
            /********************************************************/
            //authorize, demonstrate how to generate hmac signature here
            String authorization = HmacGenerater.generate(method, uriBuilder.build().toString(), Constants.SECRET_KEY, Constants.ACCESS_KEY);
            //print out the hmac key
            System.out.println ( authorization );
            /********************************************************/
            uriBuilder.setScheme(Constants.SCHEMA).setHost(Constants.HOST).setPort(Constants.PORT);
            HttpDelete delete = new HttpDelete ( uriBuilder.build ().toString () );
            /********************************************************/
            // set header, demonstarte how to use hmac signature here
            delete.addHeader ( "Authorization", authorization );
            /********************************************************/
            delete.addHeader ( "content-type", "application/json" );
            delete.addHeader ( "Request-Vendor-Id", Constants.vendorId );
            // execute
            httpExecute(delete);
        } catch ( Exception e ) {
            e.printStackTrace ( );
        }
    }

    public JSONObject httpExecute(HttpUriRequest request) {

        CloseableHttpClient client = null;
        Object objData = null;
        JSONObject resultJson = null;
        try {
            //create client
            client = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                //execute get request
                response = client.execute(request);
                //print result
                System.out.println ("status code:" + response.getStatusLine().getStatusCode());
                System.out.println ("status message:" + response.getStatusLine().getReasonPhrase());
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);
//                System.out.println ("result:" + EntityUtils.toString(entity));
                System.out.println ("result:" + result);

//                if(response.getStatusLine().getStatusCode() == 200){

                    // 결과값 json으로 받기 Object -> Json // JsonArray로 받아야되나..?
                    JSONParser jsonParser = new JSONParser();
                    objData = jsonParser.parse(result);
                    resultJson = (JSONObject) objData;

                    System.out.println(resultJson.get("code"));
//                }

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

        return resultJson;
    }

}