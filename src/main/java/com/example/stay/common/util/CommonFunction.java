package com.example.stay.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CommonFunction<T> {

    public String makeReturn(String statusCode, String message){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);

            strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public String makeReturn(String statusCode, String message, T result){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);
            jsonObject.put("result", result);

            strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public JsonNode callJsonApi(String strAccomm, String strType, JSONObject requestJson, String strUrl, String method){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.createObjectNode();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // API 호출 정보
        if(strUrl.equals("")){
            if(strAccomm.equals("hanwha")){
                strUrl = Constants.hanwhaUrl;
            }else if(strAccomm.equals("YPB")){
                if(strType.equals("stock")){
                    strUrl = Constants.ypUrl+"getRsrvMm";
                }else if(strType.equals("booking")){
                    strUrl = Constants.ypUrl+"joinSalesPkgRsrvInfo";
                }else if(strType.equals("bookingInfo")){
                    strUrl = Constants.ypUrl+"getSalesRsrvList";
                }else if(strType.equals("bookingCancel")){
                    strUrl = Constants.ypUrl+"joinSalesPkgRsrvCncl";
                }else if(strType.equals("bookingList")){
                    strUrl = Constants.ypUrl+"getArrvRsrvList";
                }
            }
        }

        LogWriter logWriter = new LogWriter(method, strUrl, System.currentTimeMillis());
        try{
            URL url = new URL(strUrl);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            if(strAccomm.equals("YPB")){
                conn.setRequestProperty("X-Yobiss-AuthToken", Constants.ypTokenKey);
            }

            if(!requestJson.isEmpty()){
                conn.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                writer.write(requestJson.toJSONString());
                writer.close();

                logWriter.addRequest(gson.toJson(requestJson));
            }

            String strResult = "";
            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                strResult = sb.toString();
            }else{
                strResult = conn.getResponseMessage();
            }

            conn.disconnect();

            // JSON 파싱
            jsonNode = objectMapper.readTree(strResult);

            logWriter.add(gson.toJson(jsonNode));
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return jsonNode;
    }


    /*
    지번 주소 => 우편번호
    주소로 신우편번호(5자리) 가져오기
    !상세주소로만 조회할것! 상세주소가 아니면 관련된 모든 우편번호중 첫번째 항목을 SET하니 오출력 가능성 있음
     */
    public String getZipcodeByParcelAddress(String parcelAddress){
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        String requestUrl = "http://api.vworld.kr/req/search?service=search&request=search&version=2.0&crs=EPSG:900913&size=10&page=1&type=address&category=parcel&format=json&errorformat=json";
        requestUrl += "&query=" + parcelAddress;
        requestUrl += "&key=" + Constants.vWorldApiSecretKey;
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(requestUrl)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                responseJson = ( JSONObject ) responseJson.get("response");
                responseJson = ( JSONObject ) responseJson.get("result");
                List<Map<String, Object>> itemList = ( List<Map<String, Object> > ) responseJson.get("items");
                JSONObject address = ( JSONObject ) itemList.get(0).get("address");
                System.out.println(address.get("zipcode").toString());
                return address.get("zipcode").toString();


            } else {
                return makeReturn(String.valueOf(response.code()), response.message());
            }
        } catch (Exception e){
            e.printStackTrace();
            return makeReturn(e.toString(), e.getMessage());
        }

    }
}
