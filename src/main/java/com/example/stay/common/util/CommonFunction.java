package com.example.stay.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.*;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
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

    public String makeReturn(String returnType, String statusCode, String message){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);

            if(returnType.equals("json")){
                strResult = jsonObject.toJSONString();
            }else if(returnType.equals("jsonp")){
                strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";
            }else if(returnType.equals("화면출력???")){
                strResult = "?????????";
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return strResult;
    }

    public String makeReturn(String returnType, String statusCode, String message, T result){

        String strResult = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", statusCode);
            jsonObject.put("message", message);
            jsonObject.put("result", result);

            if(returnType.equals("json")){
                strResult = jsonObject.toJSONString();
            }else if(returnType.equals("jsonp")){
                strResult = Constants.jsonpCallback + "(" + jsonObject.toJSONString() + ")";
            }else if(returnType.equals("화면출력???")){
                strResult = "?????????";
            }

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
                logWriter.add("responseCode : " + conn.getResponseCode());
                logWriter.add("responseMessage : " + conn.getResponseMessage() + "\n");
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
    SOAP Client API
     */
    public String sendMessage(String url, String method, String message){
        try {
            System.out.println(url);
            Service service = new Service();
            Call call = (Call) service.createCall();

            call.setTargetEndpointAddress(new java.net.URL(url));
            call.setOperationName(method);

            String response = (String) call.invoke(new Object[]{message});
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
    한글 지역명을 코드로
    지역이 같아도 지역명이 다른경우가 있어 매칭 선처리
    ex) 강원, 강원특별자치도, 강원도 => 42
     */
    public String addressToDistrictCode(String address) {
        if ("서울".equals(address) || "서울시".equals(address) || "서울특별시".equals(address)) {
            return "11";
        } else if ( "부산".equals(address) ||  "부산시".equals(address) || "부산광역시".equals(address)) {
            return "26";
        } else if ( "대구".equals(address) ||  "대구시".equals(address) || "대구광역시".equals(address)) {
            return "27";
        } else if ( "인천".equals(address) ||  "인천시".equals(address) || "인천광역시".equals(address)) {
            return "28";
        } else if ( "광주".equals(address) ||  "광주시".equals(address) || "광주광역시".equals(address)) {
            return "29";
        } else if ( "대전".equals(address) ||  "대전시".equals(address) || "대전광역시".equals(address)) {
            return "30";
        } else if ( "울산".equals(address) ||  "울산시".equals(address) || "울산광역시".equals(address)) {
            return "31";
        } else if ( "경기".equals(address) ||  "경기도".equals(address)) {
            return "41";
        } else if ( "강원".equals(address) ||  "강원도".equals(address) || "강원특별자치도".equals(address)) {
            return "42";
        } else if ( "충북".equals(address) ||  "충청북도".equals(address)) {
            return "43";
        } else if ( "충남".equals(address) ||  "충청남도".equals(address)) {
            return "44";
        } else if ( "전북".equals(address) ||  "전라북도".equals(address)) {
            return "45";
        } else if ( "전남".equals(address) ||  "전라남도".equals(address)) {
            return "46";
        } else if ( "경북".equals(address) ||  "경상북도".equals(address)) {
            return "47";
        } else if ( "경남".equals(address) ||  "경상남도".equals(address)) {
            return "48";
        } else if ( "제주".equals(address) ||  "제주도".equals(address) || "제주특별자치도".equals(address)) {
            return "50";
        } else {
            return "";
        }

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
                return makeReturn("json", String.valueOf(response.code()), response.message());
            }
        } catch (Exception e){
            e.printStackTrace();
            return makeReturn("json", e.toString(), e.getMessage());
        }

    }
}
