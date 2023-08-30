package com.example.stay.accommodation.gpension.service;

import com.example.stay.accommodation.gpension.mapper.AccommMapper;
import com.example.stay.common.util.Base64Encoder;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.UrlResourceDownloader;
import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service("gpension.AccommService")
public class AccommService {

    CommonFunction commonFunction = new CommonFunction();

    @Autowired
    private AccommMapper accommMapper;

    public String getPensionList() {

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date current = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(current);
        String lastDate = "";
        lastDate = sdf.format(c.getTime());
        c.add(Calendar.MINUTE, -15);

        String requesttURI = "?";
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
                return  commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message(), resultListMap);
            } else {
                return commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message(), result);
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp",String.valueOf(e),e.getMessage());
        }

    }

    public String getPensionInfo(String pensionId){

        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String requesttURI = "?";
        String pensionID = pensionId;
        String detailYN = "Y";
        requesttURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionID + "&detail_yn=" + detailYN;


        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_info.php" + requesttURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            //response 파싱
            String responseBody = response.body().string();

            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
            JSONObject resultJson = (JSONObject) responseJson.get("result");
            if(response.isSuccessful()) {
                if (!resultJson.get("result_cd").equals("S")) {
                    return commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message(), resultJson);
                }
                JSONObject pensionInfotData = (JSONObject) responseJson.get("pension_info");
                    String refundRule = (String) pensionInfotData.get("refund_rule");
                    String warningRule = (String) pensionInfotData.get("warning_rule");
                    String basicRule = (String) pensionInfotData.get("basic_rule");
                    String roomCnt = (String) pensionInfotData.get("room_cnt");
                    String pensionIntro = (String) pensionInfotData.get("pension_intro");
                    refundRule = base64Decode(refundRule);
                    warningRule = base64Decode(warningRule);
                    basicRule = base64Decode(basicRule);
                    pensionIntro = base64Decode(pensionIntro);
                pensionInfotData.put("refund_rule", refundRule);
                pensionInfotData.put("warning_rule", warningRule);
                pensionInfotData.put("basic_rule", basicRule);
                pensionInfotData.put("pension_intro", pensionIntro);

                List<Map<String, Object>> roomListData = (List<Map<String, Object>>) responseJson.get("room_data");
                List<Map<String, Object>> cancelListData = (List<Map<String, Object>>) responseJson.get("cancel_data");
                List<Map<String, Object>> roomListDataTmp = new ArrayList<>();
                for (Map<String, Object> roomMap : roomListData) {
                    String roomdetailInfo = (String) roomMap.get("room_info");
                    roomdetailInfo = base64Decode(roomdetailInfo);
                    roomMap.put("room_info", roomdetailInfo);
                    roomListDataTmp.add(roomMap);
                }

                responseJson.put("room_data", roomListDataTmp);
                responseJson.put("cancel_data", cancelListData);
                return commonFunction.makeReturn("jsonp","", "", responseJson);
            } else {
                return commonFunction.makeReturn("jsonp","", "", responseJson);
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getPensionStatus(String pensionId, String sDate, String eDate) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        String requesttURI = "?";

        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (sDate == null || sDate.equals("")) {
            sDate = dateFormat.format(nowDate);
        }

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
                JSONObject resultJson = (JSONObject) responseJson.get("result");
                if (resultJson.get("result_cd").equals("S")) {
                    JSONObject pensionJson = (JSONObject) responseJson.get("pension_info");
                    List<Map<String, Object>> roomResult = (List<Map<String, Object>>) responseJson.get("room_data");
//                System.out.println(base64Decode((String) pensionJson.get("pension_name")));
                    for (int i = 0; i < roomResult.size(); i++) {
                        Map<String, Object> roomMap = roomResult.get(i);
//                    System.out.println(roomMap.get("room_id"));
//                    System.out.println(base64Decode((String) roomMap.get("room_name")));
                    }
                    return commonFunction.makeReturn("jsonp","200", "OK", responseJson);

                } else {
                    return commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message(), resultJson);
                }
            } else {
                return commonFunction.makeReturn("jsonp",String.valueOf(response.code()), response.message(), "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp",String.valueOf(e), e.getMessage());
        }

    }

    public String getPensionDailyInfo(String pensionId, String sDate, String eDate) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS).build();
        String requestURI = "?";
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (sDate == null || sDate.equals("")) {
            sDate = dateFormat.format(nowDate);
        }
        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionId + "&sdate=" + sDate + "&edate=" + eDate;
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
                return commonFunction.makeReturn("jsonp","", "", responseJson);
            } else {
                return commonFunction.makeReturn("jsonp","", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp",e.toString(), e.getMessage(), "");
        }

    }

    public String getPensionMainList() {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth;
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
                return commonFunction.makeReturn("jsonp","", "", responseJson);
            } else {
                return commonFunction.makeReturn("jsonp","", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","", "", "");
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
                return commonFunction.makeReturn("jsonp","", "", responseJson);
            } else {
                return commonFunction.makeReturn("jsonp","", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","", "", "");
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
//                responseJson = (JSONObject) responseJson.get("result");
                List<Map<String, Object>> scheduleDataList = (List<Map<String, Object>>) responseJson.get("schedule_data");
                List<Map<String, Object>> scheduleTmp = new ArrayList<>();
                for (Map<String, Object> map : scheduleDataList) {
                    String scheduleName = (String) map.get("schedule_name");
                    scheduleName = base64Decode(scheduleName);
                    map.put("schedule_name", scheduleName);
                    scheduleTmp.add(map);
                }
                responseJson.put("schedule_data", scheduleTmp);
                return commonFunction.makeReturn("jsonp", "", "", responseJson);
            } else {
                return commonFunction.makeReturn("jsonp","", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","", "", "");
        }
    }

//    정보변경 내역 조회 (변경내역 조회하여 삭제된 숙소의 경우 ACCOMM테이블에 삭제여부, 노출여부 변경시켜줘야함)
//    변경된 항목은 일 배치를 통해 수정되니 제휴중단된 항목에 대해서만 삭제/노출 여부 변경
    public String getPensionModList(String lastDate){
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        Date nowDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (lastDate == null || lastDate.equals("")) {
            lastDate = dateFormat.format(nowDate);
        }
        requestURI += "auth_key=" + Constants.gpAuth + "&detail_yn=Y" + "&last_date=" + lastDate;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "pension_mod_list.php" + requestURI)
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
                //추가, 삭제, 변경 내역들 내려옴 (펜션 / 객실 ) -- 추가된항목은
                List<Map<String, Object>> pensionDeleteList = (List<Map<String, Object>>) responseJson.get("pension_del_list");
                List<Map<String, Object>> roomDeleteList = (List<Map<String, Object>>) responseJson.get("room_del_list");
                for (Map<String, Object> map : pensionDeleteList) {
                    String pensionId = (String) map.get("pension_id");
//                    accommMapper.updateDelPension(pensionId);
                }
                for (Map<String, Object> map : roomDeleteList) {
                    String pensionId = (String) map.get("pension_id");
                    String roomId = (String) map.get("room_id");
//                    accommMapper.updateDelRoom(pensionId, roomId);
                }
                return commonFunction.makeReturn("jsonp", "", "",  responseJson);

            } else {
                return commonFunction.makeReturn("jsonp", String.valueOf(response.code()), response.message(), "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp", "500", e.getMessage());
        }
    }


    //시설, 객실 INSERT
    public String insertGP() {
        String accommData = getPensionList();
        accommData = accommData.substring(5, accommData.length() - 1);
        String strAccommData = "";
        String strRoomData = "";
        String strStockData = "";
        Calendar cal = Calendar.getInstance();
        Date nowDate = new Date(cal.getTimeInMillis());
        cal.add(Calendar.MONTH, 1);
        Date eDate = new Date(cal.getTimeInMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(accommData);
            List<Map<String, Object>> responseList = (List<Map<String, Object>>) responseJson.get("result");
            for (Map<String, Object> map : responseList) {

                String districtCode2 = accommMapper.getDistrictCodeWithStr( commonFunction.addressToDistrictCode(map.get("pension_addr1").toString()), (String) map.get("pension_addr2"));
                String districtCode1 = districtCode2.substring(0, 2);
                String pensionId = (String) map.get("pension_id");
                String pensionName = (String) map.get("pension_name");
                String lati = (String) map.get("lati");
                String longi = (String) map.get("longi");


                String roomData = getPensionStatus(map.get("pension_id").toString(), dateFormat.format(nowDate), dateFormat.format(eDate));
                String pensionInfo = getPensionInfo(map.get("pension_id").toString());
                roomData = roomData.substring(5, roomData.length() - 1);
                JSONObject roomJson = (JSONObject) jsonParser.parse(roomData);
                roomJson = (JSONObject) roomJson.get("result");
                List<Map<String, Object>> roomList = (List<Map<String, Object>>) roomJson.get("room_data");
                for (Map<String, Object> roomMap : roomList) {
                    String roomId = (String) roomMap.get("room_id");
                    String childPrice = (String) roomMap.get("child_price");
                    String adultPrice = (String) roomMap.get("adult_price");
                    List<Map<String, Object>> roomPriceList = (List<Map<String, Object>>) roomMap.get("price_data");
                    if(roomPriceList == null) {
                        strStockData += pensionId + "|^|" + roomId + "|^||^||^||^||^|" + childPrice + "|^|" + adultPrice + "{{|}}";
                        continue;
                    }
                    for (Map<String, Object> roomPriceMap : roomPriceList) {
                        String date = (String) roomPriceMap.get("date");
                        String basicPrice = (String) roomPriceMap.get("basic_price");
                        String price = (String) roomPriceMap.get("price");
                        String stock = ("Y".equals(roomPriceMap.get("open_yn"))) ? "1" : "0";

                        strStockData += pensionId + "|^|" + roomId + "|^|" + date + "|^|" + basicPrice + "|^|" + price + "|^|" + stock + "|^|" + childPrice + "|^|" + adultPrice + "{{|}}";
                    }

                }
                pensionInfo = pensionInfo.substring(5, pensionInfo.length() - 1);
                JSONObject pensionJson = (JSONObject) jsonParser.parse(pensionInfo);
                pensionJson = (JSONObject) pensionJson.get("result");

                JSONObject pensionInfoJson = (JSONObject) pensionJson.get("pension_info");
                String pensionTel = (String) pensionInfoJson.get("pension_tel");
                String pensionAddr = (String) pensionInfoJson.get("pension_addr");
                String pensionCheckIn = (String) pensionInfoJson.get("checkin");
                String pensionCheckOut = (String) pensionInfoJson.get("checkout");
                String pensionTheme = (String) pensionInfoJson.get("theme");
                String pensionWebsite = (String) pensionInfoJson.get("homepage");
                String pensionRoomCnt = (String) pensionInfoJson.get("room_cnt");
                String pensionZip = commonFunction.getZipcodeByParcelAddress(pensionAddr);

                List<Map<String, Object>> facImgList = (List<Map<String, Object>>) pensionInfoJson.get("fac_img");
                List<Map<String, Object>> extImgList = (List<Map<String, Object>>) pensionInfoJson.get("ext_img");
                List<Map<String, Object>> mainImgList = (List<Map<String, Object>>) pensionInfoJson.get("main_img");
                if (facImgList == null) {
                    facImgList = new ArrayList<>();
                }
                if (extImgList == null) {
                    extImgList = new ArrayList<>();
                }
                if (mainImgList == null) {
                    mainImgList = new ArrayList<>();
                }
                String strImgList ="";
                for (Map<String, Object> facImgMap : facImgList) {
//                    System.out.println(facImgMap);
                    strImgList += accommPhotoContentsReg((String) facImgMap.get("img"), pensionId, "");

                }
                for (Map<String, Object> extImgMap : extImgList) {
//                    System.out.println(extImgMap);
                    strImgList += accommPhotoContentsReg((String) extImgMap.get("img"), pensionId, "");
                }
                for (Map<String, Object> mainImgMap : mainImgList) {
//                    System.out.println(mainImgMap);
                    strImgList += accommPhotoContentsReg((String) mainImgMap.get("img"), pensionId, "");
                }
//                strImgList += "{;

                List<Map<String, Object>> cancelList = (List<Map<String, Object>>) pensionJson.get("cancel_data");
                String strPenaltyData = "";
                if(cancelList == null) {

                } else {
                    for (Map<String, Object> cancelMap : cancelList) {
                        strPenaltyData += cancelMap.get("cancel_day") + "|~|" + cancelMap.get("cancel_rate") + "{{^}}";
                    }
                    strPenaltyData = strPenaltyData.substring(0, strPenaltyData.length() - 5);
                }


                List<Map<String, Object>> roomListData = (List<Map<String, Object>>) pensionJson.get("room_data");
                List<Map<String, Object>> roomListDataTmp = new ArrayList<>();
                for (Map<String, Object> roomMap : roomListData) {
//                    String roomInfo = (String) roomMap.get("room_info");
                    String roomId = (String) roomMap.get("room_id");
                    String roomName = (String) roomMap.get("room_name");
                    String maxPeople = (String) roomMap.get("max_people");
                    String basicPeople = (String) roomMap.get("basic_people");
                    String roomType = (String) roomMap.get("room_type");
                    String roomItem = (String) roomMap.get("room_item");
                    roomItem = roomItem.replaceAll("\\r\\n", "");
                    List<Map<String, Object>> roomImgDatas = (List<Map<String, Object>>) roomMap.get("room_img");
                    if (roomImgDatas == null) {
                        roomImgDatas = new ArrayList<>();
                    }
                    String strRoomImgData = "";
                    for (int i = 0 ; i < roomImgDatas.size(); i++) {
                        Map<String, Object> roomImgMap = roomImgDatas.get(i);
                        strRoomImgData += accommPhotoContentsReg((String) roomImgMap.get("img"), pensionId, roomId);
                        if (i < roomImgDatas.size() - 1) {
                            strRoomImgData += "{{^}}";
                        }
                    }

                    roomListDataTmp.add(roomMap);

                    strRoomData += pensionId + "|^|" + roomId + "|^|" + roomName + "|^|" + maxPeople + "|^|" + basicPeople + "|^|" + roomType + roomItem.trim() + "|^|" + strRoomImgData + "{{|}}";
                }
                pensionJson.put("room_data", roomListDataTmp);

                System.out.println(pensionZip);
                //펜션ID, 펜션명, 지역코드1, 지역코드2, 위도, 경도, 전화번호, 주소, 체크인시간, 체크아웃시간, 홈페이지주소, 판매가능객실수, 취소규정, 펜션이미지(부대시설, 전경, 메인)
                strAccommData += pensionId + "|^|" + pensionName + "|^|" + districtCode1 + "|^|" + districtCode2 + "|^|" + lati + "|^|" + longi + "|^|"
                        + pensionTel + "|^|" + pensionAddr + "|^|" + pensionCheckIn + "|^|" + pensionCheckOut + "|^|" + pensionWebsite + "|^|" + pensionRoomCnt + "|^|"
                        + strPenaltyData + "|^|" + "|^|" + pensionZip + "{{|}}";
            }

            strAccommData = strAccommData.substring(0, strAccommData.length()-5);
            strRoomData = strRoomData.substring(0, strRoomData.length()-5);

            System.out.println(strAccommData);
            System.out.println(strRoomData);

//            String insertResult = accommMapper.insertAccommTotal("", "", "", "GP");
//            String insertResult = accommMapper.insertAccommTotal(strAccommData, strRoomData, "", "GP");
//            System.out.println(insertResult);
//
            return commonFunction.makeReturn("jsonp", "200","", "insertResult");
        } catch (Exception e) {
            return commonFunction.makeReturn("jsonp","500", String.valueOf(e), e.getMessage());
        }


    }

    public String updatePenaltyData() throws ParseException {
        List<Map<String, Object>> list = accommMapper.getPensionList();
        for (Map<String, Object> s : list) {
            String result = getPensionInfo(s.get("GPID").toString());
            result = result.substring(5, result.length() - 1);
            JSONParser jsonParser = new JSONParser();
            JSONObject resultJson = (JSONObject) jsonParser.parse(result);
            resultJson = (JSONObject) resultJson.get("result");
            List<Map<String, Object>> cancelList = (List<Map<String, Object>>) resultJson.get("cancel_data");
            if (cancelList == null) {
                cancelList = new ArrayList<>();
            } else {
                int j = 0;
                System.out.println( cancelList.size());
                for (Map<String, Object> cancelMap : cancelList) {
                    String cancelDay = (String) cancelMap.get("cancel_day");
                    String cancelRate = (String) cancelMap.get("cancel_rate");
                    if (j<Integer.parseInt(cancelDay)) {
                        for (int i = j; i <= Integer.parseInt(cancelDay); i++) {
                            System.out.println(s.get("con_id").toString() + " : " + s.get("con_name").toString() + " : " + i + " : " + cancelRate);
                            accommMapper.insertPenaltyData(s.get("con_id").toString(), s.get("con_name").toString(), String.valueOf(i), cancelRate);
                        }
                        j=Integer.parseInt(cancelDay);
                    } else {
                        System.out.println(s.get("con_id").toString() + " : " + s.get("con_name").toString() + " : " + cancelDay + " : " + cancelRate);
                        accommMapper.insertPenaltyData(s.get("con_id").toString(), s.get("con_name").toString(), cancelDay, cancelRate);
                    }
                    j++;
                }
            }



        }
        return "";
    }

    //Base64 decode
    public String base64Decode(String s) throws Exception {
        return new String(Base64Encoder.decode(s));
    }

    // CONTENTS_PHOTO, CONDO_PHOTO에 INSERT
    public String accommPhotoContentsReg(String strImage, String strPropertyID, String strRmtypeID){
        String strAccommPhotoContent = "";
        try{
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 158; // 이미지 생성한사람 158 : 이운범(STAFFS테이블)
            int intModifiedSID = 158; // 이미지 수정한사람

            String[] filePathArr = strImage.split("/");
            String strFileName = "";
            for(int j=0; j< filePathArr.length; j++){
                if(j == (filePathArr.length - 1)){
                    strFileName = filePathArr[j];
                }
            }


            Path directoryPath = null;
            String filePath = "";
            String strFilePath = "";
            // 시설 이미지일 경우
            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            if(strRmtypeID.equals("")){
                directoryPath = Paths.get(Constants.gpensionFileDir + strPropertyID + "\\");
                filePath = Constants.gpensionFileDir + strPropertyID + "\\" + strFileName;
                strFilePath = "/gpension/" + strPropertyID + "/";
            }else{ // 객실 이미지일 경우
                directoryPath = Paths.get(Constants.gpensionFileDir + strPropertyID + "\\" + strRmtypeID + "\\");
                filePath = Constants.gpensionFileDir + strPropertyID + "\\" + strRmtypeID + "\\" + strFileName;
                strFilePath = "/gpension/" + strPropertyID + "/" + strRmtypeID + "/";
            }

            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            File file = new File(filePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(filePath, strImage);
                downloader.urlFileDownload();
            }else{
                System.out.println("ALREADY EXISTS PHOTO");
            }

            strAccommPhotoContent = strFilePath + "|~|" + strFileName + "|~|" + intCreatedSID + "|~|"
                    + intModifiedSID;

        }catch (Exception e){
            e.printStackTrace();
        }
        return strAccommPhotoContent;
    }

}
