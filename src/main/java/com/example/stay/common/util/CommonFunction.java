package com.example.stay.common.util;

import okhttp3.*;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.net.URLEncoder;

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
    주소로 신우편번호(5자리) 가져오기
    !상세주소로만 조회할것!
     */
    public String getNewAddressCodeByAddress(String address){

        try{
            StringBuilder urlBuilder = new StringBuilder("http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("ServiceKey","UTF-8") + "=" + Constants.openApiKey); /*Service Key 개인키로 차후 변경요망*/
            urlBuilder.append("&" + URLEncoder.encode("srchwrd","UTF-8") + "=" + URLEncoder.encode(address, "UTF-8")); /*검색어  추후 address 받아서 넣기 */
            URL url = new URL(urlBuilder.toString());
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json;");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            StringBuffer buffer = new StringBuffer();
            buffer.append(response.body().string());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(buffer.toString())));

            NodeList tags = document.getElementsByTagName("newAddressListAreaCdSearchAll");
            Node tagtext = tags.item(0).getFirstChild().getFirstChild();
            String tagvalue = tagtext.getNodeValue();

            System.out.println(tagvalue);

            return tagvalue;

        } catch (Exception e){
            e.printStackTrace();
            return makeReturn("", e.getMessage());
        }
    }
}
