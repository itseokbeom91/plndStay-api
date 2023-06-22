package com.example.stay.accommodation.gpension.service;

import com.example.stay.common.util.Base64Encoder;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service("gpension.AccommService")
public class AccommService {

    CommonFunction commonFunction = new CommonFunction();

    public String getPensionList(){

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String requesttURI = "?";
        String lastDate = "2023-08-05";
        requesttURI += "auth_key=" + Constants.gpAuth + "&last_date=" + lastDate;


        Request request = new Request.Builder()
                .url(Constants.gpPath+ "pension_list.php" + requesttURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();


                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) responseJson.get("data");
                List<Map<String, Object>> resultListMap = new ArrayList<>();

                for (int i = 0 ; i < resultList.size() ; i++) {
                    JSONObject resultJson = new JSONObject();
                    resultJson.put("pension_addr1", base64Decode((String) resultList.get(i).get("pension_addr1")));
                    resultJson.put("pension_addr2", base64Decode((String) resultList.get(i).get("pension_addr2")));
                    resultJson.put("pension_name", base64Decode((String) resultList.get(i).get("pension_name")));
                    resultJson.put("pension_id", resultList.get(i).get("pension_id"));
                    resultJson.put("lati", resultList.get(i).get("lati"));
                    resultJson.put("longi", resultList.get(i).get("longi"));
                    resultJson.put("manage_type", resultList.get(i).get("manage_type"));
//                    System.out.print("pension_addr1 ::: ");
//                    System.out.println(base64Decode((String) resultList.get(i).get("pension_addr1")));
//                    System.out.print("pension_id ::: ");
//                    System.out.println(resultList.get(i).get("pension_id"));
//                    System.out.print("lati ::: ");
//                    System.out.println(resultList.get(i).get("lati"));
//                    System.out.print("longi ::: ");
//                    System.out.println(resultList.get(i).get("longi"));
//                    System.out.print("pension_name ::: ");
//                    System.out.println(base64Decode((String) resultList.get(i).get("pension_name")));
//                    System.out.print("manage_type ::: ");
//                    System.out.println(resultList.get(i).get("manage_type"));
//                    System.out.print("pension_addr2 ::: ");
//                    System.out.println(base64Decode((String) resultList.get(i).get("pension_addr2")));
//                    System.out.println("==========================================================");
//                    System.out.println("==========================================================");
                    resultListMap.add(resultJson);
                    String statResult = getPensionStatus((String) resultJson.get("pension_id"), "2023-06-23", "2023-08-05");
                    statResult = statResult.substring(5, statResult.length()-1);
                    JSONObject statJson = (JSONObject) jsonParser.parse(statResult);
                    JSONObject resultJson2 = (JSONObject) statJson.get("result");
                    JSONObject resultJson3 = (JSONObject) resultJson2.get("result");

                }
                return  commonFunction.makeReturn("", "", resultListMap);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return commonFunction.makeReturn("","");
    }

    public String getPensionInfo(String pensionId){

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String requesttURI = "?";
        String pensionID = pensionId;
        String detailYN = "Y";
        requesttURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionID + "detail_yn" + detailYN;


        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_status.php" + requesttURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();


                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                System.out.println(responseJson);
                return commonFunction.makeReturn("", "", responseJson);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return commonFunction.makeReturn("", "");
    }

    public String getPensionStatus(String pensionId, String sDate, String eDate) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String requesttURI = "?";

        requesttURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&sdate=" + sDate + "&edate=" + eDate;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_status.php" + requesttURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                JSONObject pensionJson = (JSONObject) responseJson.get("pension_info");
                List<Map<String, Object>> roomResult = (List<Map<String, Object>>) responseJson.get("room_data");
                System.out.println(base64Decode((String) pensionJson.get("pension_name")));
                for (int i = 0; i < roomResult.size(); i++) {
                    Map<String, Object> roomMap = roomResult.get(i);
                    System.out.println(roomMap.get("room_id"));
                    System.out.println(base64Decode((String) roomMap.get("room_name")));
                }
                return commonFunction.makeReturn("", "", responseJson);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return commonFunction.makeReturn("", "");

    }

    public String base64Decode(String s) throws Exception {
        return base64Decode(s);
    }

}
