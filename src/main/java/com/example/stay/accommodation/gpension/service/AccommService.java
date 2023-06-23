package com.example.stay.accommodation.gpension.service;

import com.example.stay.accommodation.gpension.mapper.AccommMapper;
import com.example.stay.common.util.Base64Encoder;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service("gpension.AccommService")
public class AccommService {

    CommonFunction commonFunction = new CommonFunction();

    @Autowired
    private AccommMapper accommMapper;

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
            String resultBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject result = (JSONObject) jsonParser.parse(resultBody);
            JSONObject resultmsg = (JSONObject) result.get("result");

            if(response.isSuccessful() && resultmsg.get("result_cd").equals("S")) {
                //response 파싱
//                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
//                System.out.println(responseJson);
                List<Map<String, Object>> resultList = (List<Map<String, Object>>) result.get("data");
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
                    resultListMap.add(resultJson);
                }
                return  commonFunction.makeReturn("200", "OK", resultListMap);
            } else {
                return commonFunction.makeReturn(String.valueOf(response.code()), response.message(), result);
            }
        } catch (Exception e) {
            return commonFunction.makeReturn(String.valueOf(e),e.getMessage());
        }

    }

    public String getPensionInfo(String pensionId){

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String requesttURI = "?";
        String pensionID = pensionId;
        String detailYN = "Y";
        requesttURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionID + "&detail_yn" + detailYN;


        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_info.php" + requesttURI)
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

        requesttURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&sdate=" + sDate + "&edate=" + eDate + "&detail_yn=Y";
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
//                System.out.println(base64Decode((String) pensionJson.get("pension_name")));
                for (int i = 0; i < roomResult.size(); i++) {
                    Map<String, Object> roomMap = roomResult.get(i);
//                    System.out.println(roomMap.get("room_id"));
//                    System.out.println(base64Decode((String) roomMap.get("room_name")));
                }
                return commonFunction.makeReturn("", "", responseJson);
            } else {
                return commonFunction.makeReturn(String.valueOf(response.code()), response.message(), "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn(String.valueOf(e), e.getMessage());
        }

    }

    public String getPensionDailyInfo(String pensionId, String sDate, String eDate) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS).build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&sdate=" + sDate + "&edate=" + eDate + "&detail_yn=Y";
        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_daily_info.php" + requestURI)
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
                return commonFunction.makeReturn("", "", responseJson);
            } else {
                return commonFunction.makeReturn("", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("", "", "");
        }

    }

    public String getPensionMainList(String pensionId) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&detail_yn=Y";
        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_main_list.php" + requestURI)
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
                return commonFunction.makeReturn("", "", responseJson);
            } else {
                return commonFunction.makeReturn("", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("", "", "");
        }
    }

    public String getRoomInfo(String pensionId, String roomId) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&room_id=" + roomId + "&detail_yn=Y";
        Request request = new Request.Builder()
                .url(Constants.gpPath + "room_info.php" + requestURI)
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
                return commonFunction.makeReturn("", "", responseJson);
            } else {
                return commonFunction.makeReturn("", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("", "", "");
        }
    }

    public String getRoomPriceInfo(String pensionId) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&detail_yn=Y";
        Request request = new Request.Builder()
                .url(Constants.gpPath + "room_price_info.php" + requestURI)
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
                return commonFunction.makeReturn("", "", responseJson);
            } else {
                return commonFunction.makeReturn("", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("", "", "");
        }
    }

    public String insertGP() {
        String accommData = getPensionList();
        accommData = accommData.substring(5, accommData.length() - 1);
        String strAccommData = "";
        String strRoomData = "";
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(accommData);
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseJson.get("result");
            for (Map<String, Object> map : responseList) {
                String pension_addr1 = (String) map.get("pension_addr1");
                String pension_addr2 = (String) map.get("pension_addr2");

                String districtCode2 = accommMapper.getDistrictCodeWithStr( addressToDistrictCode(map.get("pension_addr1").toString()), (String) map.get("pension_addr2"));
                String districtCode1 = districtCode2.substring(0, 2);
                String pensionId = (String) map.get("pension_id");
                String pensionName = (String) map.get("pension_name");
                String lati = (String) map.get("lati");
                String longi = (String) map.get("longi");


                String roomData = getPensionStatus(map.get("pension_id").toString(), "2023-06-23", "2023-07-20");
                String pensionInfo = getPensionInfo(map.get("pension_id").toString());
                roomData = roomData.substring(5, roomData.length() - 1);
                JSONObject roomJson = (JSONObject) jsonParser.parse(roomData);
                roomJson = (JSONObject) roomJson.get("result");
                List<Map<String, Object>> roomList = (List<Map<String, Object>>) roomJson.get("room_data");
                for (Map<String, Object> roomMap : roomList) {
//                    System.out.println(roomMap.get("room_id"));
                    String roomdetailData = getRoomInfo(map.get("pension_id").toString(), roomMap.get("room_id").toString());
                    roomdetailData = roomdetailData.substring(5, roomdetailData.length() - 1);
                    JSONObject roomdetailJson = (JSONObject) jsonParser.parse(roomdetailData);
                    roomdetailJson = (JSONObject) roomdetailJson.get("result");
                    List<Map<String, Object>> roomdetailList = (List<Map<String, Object>>) roomdetailJson.get("room_data");
                    for (Map<String, Object> roomdetailMap : roomdetailList) {
//                        System.out.println(roomdetailMap);
                    }

                }
                pensionInfo = pensionInfo.substring(5, pensionInfo.length() - 1);
                JSONObject pensionJson = (JSONObject) jsonParser.parse(pensionInfo);
                pensionJson = (JSONObject) pensionJson.get("result");
                pensionJson = (JSONObject) pensionJson.get("pension_info");
                String pensionTel = (String) pensionJson.get("pension_tel");
                String pensionAddr = (String) pensionJson.get("pension_addr");
                String pensionCi = (String) pensionJson.get("checkin");
                String pensionCo = (String) pensionJson.get("checkout");




                strAccommData += pensionId + "|^|" + pensionName + "|^|" + districtCode1 + "|^|" + districtCode2 + "|^|" + lati + "|^|" + longi + "|^|"
                        + pensionTel + "|^|" + pensionAddr + "|^|" + pensionCi + "|^|" + pensionCo + "{{|}}";
            }

            strAccommData = strAccommData.substring(0, strAccommData.length()-5);

            System.out.println(strAccommData);

            //String insertResult = accommMapper.insertAccommTotal(strAccommData, "", "", "GP");

            return commonFunction.makeReturn("", "", responseJson);
        } catch (Exception e) {
            return commonFunction.makeReturn(String.valueOf(e), e.getMessage(), "");
        }


    }

    //Base63 decode
    public String base64Decode(String s) throws Exception {
        return new String(Base64Encoder.decode(s));
    }

    /*
    한글 지역명을 코드로
    지역이 같아도 지역명이 다른경우가 있어 매칭 선처리
    ex) 강원, 강원특별자치도, 강원도 => 42
     */
    public String addressToDistrictCode(String address) {
        String result = "";
        if ("서울".equals(address) || "서울시".equals(address) || "서울특별시".equals(address)) {
            result = "11";
        } else if ( "부산".equals(address) ||  "부산시".equals(address) || "부산광역시".equals(address)) {
            result = "26";
        } else if ( "대구".equals(address) ||  "대구시".equals(address) || "대구광역시".equals(address)) {
            result = "27";
        } else if ( "인천".equals(address) ||  "인천시".equals(address) || "인천광역시".equals(address)) {
            result = "28";
        } else if ( "광주".equals(address) ||  "광주시".equals(address) || "광주광역시".equals(address)) {
            result = "29";
        } else if ( "대전".equals(address) ||  "대전시".equals(address) || "대전광역시".equals(address)) {
            result = "30";
        } else if ( "울산".equals(address) ||  "울산시".equals(address) || "울산광역시".equals(address)) {
            result = "31";
        } else if ( "경기".equals(address) ||  "경기도".equals(address)) {
            result = "41";
        } else if ( "강원".equals(address) ||  "강원도".equals(address) || "강원특별자치도".equals(address)) {
            result = "42";
        } else if ( "충북".equals(address) ||  "충청북도".equals(address)) {
            result = "43";
        } else if ( "충남".equals(address) ||  "충청남도".equals(address)) {
            result = "44";
        } else if ( "전북".equals(address) ||  "전라북도".equals(address)) {
            result = "45";
        } else if ( "전남".equals(address) ||  "전라남도".equals(address)) {
            result = "46";
        } else if ( "경북".equals(address) ||  "경상북도".equals(address)) {
            result = "47";
        } else if ( "경남".equals(address) ||  "경상남도".equals(address)) {
            result = "48";
        } else if ( "제주".equals(address) ||  "제주도".equals(address) || "제주특별자치도".equals(address)) {
            result = "50";
        }

        return result;
    }

}
