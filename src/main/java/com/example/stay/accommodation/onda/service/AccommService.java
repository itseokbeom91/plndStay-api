package com.example.stay.accommodation.onda.service;

import com.example.stay.common.util.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccommService {

    public void getAccommListApi(){
        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray accommArray = (JSONArray) responseJson.get("properties");
                List<JSONObject> accommList = new ArrayList<>();
                for(int i=0; i<accommArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) accommArray.get(i);
                    accommList.add(jsonObject);
                }

                // dto에 담기
                for(JSONObject list : accommList){
//                    list.get("id").toString()
//                    accommList.get(0).get("id")

                    // 정보 가지고와서 그 다음은 뭐함?
                    // DB에 insert?????
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 전체 숙소 목록 가져오기 실패");
        }
        
    }

    public void getAccommDetailApi(String property_id){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONObject jsonProperty = (JSONObject) responseJson.get("property");

                // dto에 담기
                // 주소
                JSONObject address = (JSONObject) jsonProperty.get("address");
                JSONObject location = (JSONObject) address.get("location");

                JSONArray classifications = (JSONArray) jsonProperty.get("classifications");

                JSONObject descriptions = (JSONObject) jsonProperty.get("descriptions");

                JSONObject tags = (JSONObject) jsonProperty.get("tags");
                JSONArray properties = (JSONArray) tags.get("properties");
                JSONArray facilities = (JSONArray) tags.get("facilities");
                JSONArray services = (JSONArray) tags.get("services");
                JSONArray attractions = (JSONArray) tags.get("attractions");

                JSONArray images = (JSONArray) jsonProperty.get("images");
                for(int i=0; i<images.size(); i++){
                    JSONObject image = (JSONObject) images.get(i);
                    String original = image.get("original").toString();
                }




            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 상세정보 가져오기 실패");
        }
    }

    public void getRoomTypeListApi(String property_id){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray roomtypeArray = (JSONArray) responseJson.get("roomtypes");

                List<JSONObject> roomTypeList = new ArrayList<>();
                for(int i=0; i<roomtypeArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) roomtypeArray.get(i);
                    roomTypeList.add(jsonObject);
                    System.out.println(roomtypeArray);
                }

                // dto에 담기
                for(JSONObject list : roomTypeList){
//                    list.get("id").toString();
                    System.out.println(list.get("id").toString());
                }



            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 전체 객실 목록 가져오기 실패");
        }
    }

    public void getRoomTypeDetail(String property_id, String roomtype_id){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONObject jsonRoomType = (JSONObject) responseJson.get("roomtype");

                // dto에 담기
                JSONObject capacity = (JSONObject) jsonRoomType.get("capacity");

                JSONObject tags = (JSONObject)jsonRoomType.get("tags");
                JSONArray roomtypes = (JSONArray) tags.get("roomtypes");
                JSONArray views = (JSONArray) tags.get("views");
                JSONArray amenities = (JSONArray) tags.get("amenities");

                JSONObject details = (JSONObject) jsonRoomType.get("details");
//                String room = details.get("room").toString(); // String으로 받든 int로 받든...

                JSONObject bedtype = (JSONObject) jsonRoomType.get("bedtype");
//                String room = details.get("single_beds").toString();

                JSONArray images = (JSONArray) jsonRoomType.get("images");
                for(int i=0; i<images.size(); i++){
                    JSONObject image = (JSONObject) images.get(i);
//                    String original = image.get("original").toString();
                }



            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실 상세정보 가져오기 실패");
        }
    }

    public void getPackageList(String property_id, String roomtype_id){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id + "/rateplans")
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                // dto에 담기
                JSONArray rateplans = (JSONArray) responseJson.get("rateplans");
                for(int i=0; i<rateplans.size(); i++){
                    JSONObject jsonObject = (JSONObject) rateplans.get(i);
//                    String id = jsonObject.get("id").toString();

                }


            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실의 전체 패키지 목록 가져오기 실패");
        }
    }

    public void getPackageDetail(String property_id, String roomtype_id, String rateplan_id){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id + "/rateplans/" + rateplan_id)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                JSONObject rateplan = (JSONObject) responseJson.get("rateplan");

                // dto에 담기
                JSONObject lengthOfStay = (JSONObject) responseJson.get("length_of_stay");

                JSONObject salesTerms = (JSONObject) responseJson.get("sales_terms");

                JSONObject meal = (JSONObject) responseJson.get("meal");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 정 패키지의 상세 정보 가져오기 실패");
        }
    }



}
