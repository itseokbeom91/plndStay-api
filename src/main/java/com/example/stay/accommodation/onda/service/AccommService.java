package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.AccomodationMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.common.util.ResponseResult;
import com.example.stay.common.util.UrlResourceDownloader;
import com.example.stay.openMarket.common.dto.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AccommService {

    @Autowired
    private AccomodationMapper accomodationMapper;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 시설, 룸타입, ratePlan 등록
    public ResponseResult insertAccommTotal(HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        // 전체 숙소 리스트 불러오기
//        List<JSONObject> accommList = getAccommListApi(Constants.ondaPath + "properties?status=all");
        try{

            String testAccommList = "{\n" +
                    "      \"id\": \"54207\",\n" +
                    "      \"name\": \"채널 테스트&숙소(테스트계정)\",\n" +
                    "      \"status\": \"enabled\",\n" +
                    "      \"updated_at\": \"2023-06-09T01:55:50+09:00\"\n" +
                    "    }";

            String testAccommList2 = "{\n" +
                    "      \"id\": \"130517\",\n" +
                    "      \"name\": \"에드워드호텔(거제 호텔상상)\",\n" +
                    "      \"status\": \"enabled\",\n" +
                    "      \"updated_at\": \"2023-06-09T02:05:27+09:00\"\n" +
                    "    }";
            JSONParser jsonParser = new JSONParser();

            Object obj = jsonParser.parse(testAccommList);
            JSONObject jsonObj = (JSONObject) obj;

            Object obj2 = jsonParser.parse(testAccommList2);
            JSONObject jsonObj2 = (JSONObject) obj2;

            List<JSONObject> accommList = new LinkedList<>();
            accommList.add(jsonObj);
            accommList.add(jsonObj2);




            for(JSONObject accomm : accommList){
                String strPropertyID = accomm.get("id").toString();
                // 시설
                Map<String, Object> accommData = setAccommData(strPropertyID);
                // 룸타입
                List<JSONObject> roomTypeList = getRoomTypeListApi(strPropertyID);
                String strRmtypeDatas = "";
                for (JSONObject roomType : roomTypeList) {
                    String strRmtypeID = roomType.get("id").toString();
                    strRmtypeDatas += setRmtypeDatas(strPropertyID, strRmtypeID, "") + "{{|}}";

                }

                if(strRmtypeDatas.length() != 0){
                    strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5);
                }

                String strDeleteYn = accommData.get("strDeleteYn").toString();
                String strViewYn = accommData.get("strViewYn").toString();
                String strType = accommData.get("strType").toString();
                String strDistrict1 = accommData.get("strDistrict1").toString();
                String strDistrict2 = accommData.get("strDistrict2").toString();
                String strDistrict3 = accommData.get("strDistrict3").toString();
                if(strDistrict3.equals("")){
                    strDistrict3 = null;
                }
                String strSubject = accommData.get("strSubject").toString();

                String strLat = accommData.get("strLat").toString();
                String strLon = accommData.get("strLon").toString();
                String strCheckIn = accommData.get("strCheckIn").toString();
                String strCheckOut = accommData.get("strCheckOut").toString();
                String strPhone = accommData.get("strPhone").toString();
                String strFax = accommData.get("strFax").toString();
                String strEmail = accommData.get("strEmail").toString();
                String strZipCode = accommData.get("strZipCode").toString();
                String strAddr1 = accommData.get("strAddr1").toString();
                String strAddr2 = accommData.get("strAddr2").toString();
                String strDescription = accommData.get("strDescription").toString();
                String strRsvGuide = accommData.get("strRsvGuide").toString();
                String strAcmNotice = accommData.get("strAcmNotice").toString();

                String strImgDatas = accommData.get("strImgDatas").toString();
                String strPenaltyDatas = accommData.get("strPenaltyDatas").toString();
                String strKeyWordDatas = accommData.get("strKeyWordDatas").toString();
                String strFacilityDatas = accommData.get("strFacilityDatas").toString();

                String result = accomodationMapper.insertAccommTotal(strPropertyID, strDeleteYn, strViewYn, strType,
                        strDistrict1, strDistrict2, strDistrict3, strSubject, strLat, strLon, strCheckIn, strCheckOut,
                        strPhone, strFax, strEmail, strZipCode, strAddr1, strAddr2, strDescription, strRsvGuide,
                        strAcmNotice, strImgDatas, strPenaltyDatas, strKeyWordDatas, strFacilityDatas, strRmtypeDatas);

                if(result.equals("")){
                    message = "시설 등록 완료";
                }else{
                    message = "시설 등록 실패";
                }
            }
            
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            message = "시설 등록 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return new ResponseResult<>(statusCode, message);
    }

    // 시설 수정(시설+이미지+취소규정+키워드)
    public boolean updateAccomm(String strPropertyID){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String message = "";
        boolean updateResult = false;
        try{
            Map<String, Object> accommData = setAccommData(strPropertyID);

            String strDeleteYn = accommData.get("strDeleteYn").toString();
            String strViewYn = accommData.get("strViewYn").toString();
            String strType = accommData.get("strType").toString();
            String strDistrict1 = accommData.get("strDistrict1").toString();
            String strDistrict2 = accommData.get("strDistrict2").toString();
            String strDistrict3 = accommData.get("strDistrict3").toString();
            if(strDistrict3.equals("")){
                strDistrict3 = null;
            }

            String strSubject = accommData.get("strSubject").toString();

            String strLat = accommData.get("strLat").toString();
            String strLon = accommData.get("strLon").toString();
            String strCheckIn = accommData.get("strCheckIn").toString();
            String strCheckOut = accommData.get("strCheckOut").toString();
            String strPhone = accommData.get("strPhone").toString();
            String strFax = accommData.get("strFax").toString();
            String strEmail = accommData.get("strEmail").toString();
            String strZipCode = accommData.get("strZipCode").toString();
            String strAddr1 = accommData.get("strAddr1").toString();
            String strAddr2 = accommData.get("strAddr2").toString();
            String strDescription = accommData.get("strDescription").toString();
            String strRsvGuide = accommData.get("strRsvGuide").toString();
            String strAcmNotice = accommData.get("strAcmNotice").toString();

            String strImgDatas = accommData.get("strImgDatas").toString();
            String strPenaltyDatas = accommData.get("strPenaltyDatas").toString();
            String strKeyWordDatas = accommData.get("strKeyWordDatas").toString();

            String strFacilityDatas = accommData.get("strFacilityDatas").toString();

            String result = accomodationMapper.insertAccommTotal(strPropertyID, strDeleteYn, strViewYn, strType,
                    strDistrict1, strDistrict2, strDistrict3, strSubject, strLat, strLon, strCheckIn, strCheckOut,
                    strPhone, strFax, strEmail, strZipCode, strAddr1, strAddr2, strDescription, strRsvGuide,
                    strAcmNotice, strImgDatas, strPenaltyDatas, strKeyWordDatas, strFacilityDatas, "");

            if(result.equals("완료")){
                message = "시설 등록 완료";
            }else{
                message = result;
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return updateResult;
    }

    // 시설 생성 및 수정 데이터 세팅(시설+이미지+취소규정)
    public Map<String, Object> setAccommData(String strPropertyID){
        Map<String, Object> accommData = new HashMap<>();
        try{
            JSONObject accommDetailJson = getAccommDetailApi(strPropertyID);

            String strSubject = "";
            if(accommDetailJson.get("name") != null){
                strSubject = accommDetailJson.get("name").toString();
            }

            Map<String, String> statusMap = getStatusYn(accommDetailJson.get("status").toString());
            String strDeleteYn = statusMap.get("strDeleteYn");
            String strViewYn = statusMap.get("strIngYn");

            JSONObject address = (JSONObject) accommDetailJson.get("address");
            String strRegion = address.get("region").toString();

            Map<String, String> regionMap = new HashMap<>();
            regionMap.put("충북", "충청북도");
            regionMap.put("충남", "충청남도");
            regionMap.put("전북", "전라북도");
            regionMap.put("전남", "전라남도");
            regionMap.put("경북", "경상북도");
            regionMap.put("경남", "경상남도");

            Iterator<String> keys = regionMap.keySet().iterator();
            while(keys.hasNext()){
                String key = keys.next();
                if(key.equals(strRegion)){
                    strRegion = regionMap.get(key);
                }
            }

            String strDistrict1 = accomodationMapper.getDistrictCodeByStrName(strRegion);

            String strCity = address.get("city").toString();
            String strDistrict2 = accomodationMapper.getDistrictCodeByStrName(strCity);

            String[] strDistrictArr = address.get("address2").toString().split(" "); // 일운면 거제대로 2752
            String district3 = strDistrictArr[0];
            String strDistrict3 = accomodationMapper.getDistrictCodeByStrName2(district3);
            if(strDistrict3 == null){
                strDistrict3 = "";
            }

            String address1 = address.get("address1").toString();
            String address2 = address.get("address2").toString();
            String addressDetail = address.get("address_detail").toString();
            String strAddr1 = address1 + address2;
            String strAddr2 = addressDetail;
            String strZipCode = address.get("postal_code").toString();

            JSONObject location = (JSONObject) address.get("location");
            String strLat = location.get("latitude").toString();
            String strLon = location.get("longitude").toString();

            String strPhone = "";
            if(accommDetailJson.get("phone") != null){
                strPhone = accommDetailJson.get("phone").toString();
            }
            String strFax = "";
            if(accommDetailJson.get("fax") != null){
                strFax = accommDetailJson.get("fax").toString();
            }
            String strEmail = "";
            if(accommDetailJson.get("email") != null){
                strEmail = accommDetailJson.get("email").toString();
            }
            String strCheckIn = accommDetailJson.get("checkin").toString();
            strCheckIn = strCheckIn.substring(0, strCheckIn.length()-3);
            String strCheckOut = accommDetailJson.get("checkout").toString();
            strCheckOut = strCheckOut.substring(0, strCheckOut.length()-3);

            JSONObject descriptions = (JSONObject) accommDetailJson.get("descriptions");
            String strDescription = "";
            if(descriptions.get("property") != null){
                strDescription = descriptions.get("property").toString();
            }
            String strRsvGuide = "";
            if(descriptions.get("reservation") != null){
                strRsvGuide = descriptions.get("reservation").toString();
            }
            String strAcmNotice = "";
            if(descriptions.get("notice") != null){
                strAcmNotice = descriptions.get("notice").toString();
            }

            JSONObject tags = (JSONObject) accommDetailJson.get("tags");
            JSONArray properties = (JSONArray) tags.get("properties") ;
            JSONArray facilities = (JSONArray) tags.get("facilities");
            JSONArray services = (JSONArray) tags.get("services");
            JSONArray attractions = (JSONArray) tags.get("attractions");

            List<String> keywordList = new ArrayList<>();
            List<String> facilityList = new ArrayList<>();

            if(properties != null){
                for(Object p : properties){
                    keywordList.add(p.toString());
                }
            }
            if(facilities != null){
                for(Object f : facilities){
                    facilityList.add(f.toString());
                }
            }
            if(services != null){
                for(Object s : services){
                    facilityList.add(s.toString());
                }
            }
            if(attractions != null){
                for(Object a : attractions){
                    keywordList.add(a.toString());
                }
            }

            String strKeyWordDatas = "";
            if(keywordList != null){
                for(int i=0; i<keywordList.size(); i++){
                    strKeyWordDatas += keywordList.get(i) + "{{|}}";
                }
                strKeyWordDatas = strKeyWordDatas.substring(0, strKeyWordDatas.length()-5);
            }
            String strFacilityDatas = "";
            String facility = "";
            if(facilityList != null){
                for(int i=0; i<facilityList.size(); i++){

                    if(facilityList.get(i).equals("수화물 보관")){
                        facilityList.set(i, "수화물보관");
                    }
                    if(facilityList.get(i).equals("매점/편의점")){
                        facilityList.set(i, "마트/편의점");
                    }

                    facility = accomodationMapper.getStrCodeByStrName("ACCOMM_ADD_FAC", facilityList.get(i));

                    strFacilityDatas += facility + "{{|}}";
                }

                strFacilityDatas = strFacilityDatas.substring(0, strFacilityDatas.length()-5);
            }


            accommData.put("strKeyWordDatas", strKeyWordDatas);
            accommData.put("strFacilityDatas", strFacilityDatas);

            // ACCOMM
            accommData.put("strDeleteYn", strDeleteYn);
            accommData.put("strViewYn", strViewYn);

            String strType = accomodationMapper.getStrCodeByStrName("ACCOMM_TYPE", "온다");
            accommData.put("strType", strType);

            accommData.put("strDistrict1", strDistrict1);
            accommData.put("strDistrict2", strDistrict2);
            accommData.put("strDistrict3", strDistrict3);
            accommData.put("strSubject", strSubject);

            // ACCOMM_INFO
            accommData.put("strLat", strLat);
            accommData.put("strLon", strLon);
            accommData.put("strCheckIn", strCheckIn);
            accommData.put("strCheckOut", strCheckOut);
            accommData.put("strPhone", strPhone);
            accommData.put("strFax", strFax);
            accommData.put("strEmail", strEmail);
            accommData.put("strZipCode", strZipCode);
            accommData.put("strAddr1", strAddr1);
            accommData.put("strAddr2", strAddr2);
            accommData.put("strDescription", strDescription);
            accommData.put("strRsvGuide", strRsvGuide);
            accommData.put("strAcmNotice", strAcmNotice);

            // 이미지------------------------------------------------------------------------------------------------
            // CONTENTS_PHOTO, ACCOMM_PHOTO 테이블에 INSERT
            JSONArray images = (JSONArray) accommDetailJson.get("images");

            String strImgDatas = "";
            for(int i=0; i<images.size(); i++){
                JSONObject image = (JSONObject) images.get(i);

                String str250Img = image.get("250px").toString();
                String str500Img = image.get("500px").toString();
                String str1000Img = image.get("1000px").toString();

                if(str250Img != null){
                    strImgDatas += accommPhotoContentsReg(str250Img, 250, strPropertyID, "") + "{{~}}";
                }

                if(str500Img != null){
                    strImgDatas += accommPhotoContentsReg(str500Img, 500, strPropertyID, "") + "{{~}}";
                }

                if(str1000Img != null){
                    strImgDatas += accommPhotoContentsReg(str1000Img, 1000, strPropertyID, "") + "{{~}}";
                }
            }

            if(strImgDatas.length() != 0){
                strImgDatas = strImgDatas.substring(0, strImgDatas.length()-5);
            }

            accommData.put("strImgDatas", strImgDatas);

            // 환불 취소규정 ----------------------------------------------------------------------------------------
            // ACCOMM_CANCEL_RULES 테이블에 INSERT
            JSONObject refunds = (JSONObject) accommDetailJson.get("property_refunds");
            String strFlag = "OPS";

            String strPenaltyDatas = "";
            for(int i=0; i<2; i++){
                for(int j=0; j<refunds.size(); j++){
                    int intDay = j;
                    int intPercent = 100 - (Integer.parseInt(refunds.get(j + "d").toString()));

                    strPenaltyDatas += strFlag + "|^|" + intDay + "|^|" + intPercent + "|^|" + "{{|}}";
                }
                strFlag = "OOF";
            }
            strPenaltyDatas = strPenaltyDatas.substring(0, strPenaltyDatas.length()-5);

            accommData.put("strPenaltyDatas", strPenaltyDatas);

        }catch (Exception e){
            e.printStackTrace();
        }

        return accommData;
    }

    // 룸타입+옵션 등록 및 수정
    public void updateRmtype(String strPropertyID, String strRmtypeID, String strRateplanID){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String message = "";
        try{
            String strRmtypeDatas = setRmtypeDatas(strPropertyID, strRmtypeID, strRateplanID);
            String strType = accomodationMapper.getStrCodeByStrName("ACCOMM_TYPE", "온다");

            String result = accomodationMapper.updateRoomNRatePlan(strPropertyID, strType, strRmtypeDatas);

            if(result.equals("")){
                message = "객실 등록 및 수정 완료";
            }else{
                message = "객실 등록 및 수정 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
    }

    // 룸타입, ratePlan 데이터 세팅
    public String setRmtypeDatas(String strPropertyID, String strRmtypeID, String strRateplanID){
        String rmtypeData = "";
        try{
            JSONObject roomDetailJson = getRoomTypeDetail(strPropertyID, strRmtypeID);

            JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
            int intQuanStd = Integer.parseInt(capacity.get("standard").toString());
            int intQuanMax = Integer.parseInt(capacity.get("max").toString());

            String strDescription = roomDetailJson.get("description").toString();

            String strRoomTypeStatus = roomDetailJson.get("status").toString();
            Map<String, String> statusMap = getStatusYn(strRoomTypeStatus);
            String strDeleteYn = statusMap.get("strDeleteYn");
            String strIngYn = statusMap.get("strIngYn");

            String strSubject = roomDetailJson.get("name").toString();

            // 이미지------------------------------------------------------------------------------------------------
            // CONTENTS_PHOTO, RM_PHOTO 테이블에 INSERT
            JSONArray images = (JSONArray) roomDetailJson.get("images");

            String strImgDatas = "";
            for(int i=0; i<images.size(); i++){
                JSONObject image = (JSONObject) images.get(i);

                String str250Img = image.get("250px").toString();
                String str500Img = image.get("500px").toString();
                String str1000Img = image.get("1000px").toString();

                if(str250Img != null){
                    strImgDatas += accommPhotoContentsReg(str250Img, 250, strPropertyID, strRmtypeID) + "{{~}}";
                }

                if(str500Img != null){
                    strImgDatas += accommPhotoContentsReg(str500Img, 500, strPropertyID, strRmtypeID) + "{{~}}";
                }

                if(str1000Img != null){
                    strImgDatas += accommPhotoContentsReg(str1000Img, 1000, strPropertyID, strRmtypeID) + "{{~}}";
                }
            }

            if(strImgDatas.length() != 0){
                strImgDatas = strImgDatas.substring(0, strImgDatas.length()-5);
            }

            String strRmtypeData = strDeleteYn + "|^|" + strIngYn  + "|^|" + intQuanStd + "|^|" +
                    intQuanMax + "|^|" + strSubject + "|^|" + strDescription + "|^|" + strRmtypeID + "|^|";

            // strRateplanID가 특정되어있으면 반복문X
            if(strRateplanID.equals("")){
                List<JSONObject> ratePlanList = getRatePlanList(strPropertyID, strRmtypeID);

                for(JSONObject ratePlan : ratePlanList){
                    String strRatePlanId = ratePlan.get("id").toString();

                    JSONObject ratePlanDtlJson = getRatePlanDetail(strPropertyID, strRmtypeID, strRatePlanId);

                    JSONObject lengthOfStay = (JSONObject) ratePlanDtlJson.get("length_of_stay");
                    int intMinSleep = Integer.parseInt(lengthOfStay.get("min").toString());
                    int intMaxSleep = Integer.parseInt(lengthOfStay.get("max").toString());

                    boolean refundYn = (boolean) ratePlanDtlJson.get("refundable");
                    String strRefundYn = "";
                    if(refundYn){
                        strRefundYn = "Y";
                    }else{
                        strRefundYn = "N";
                    }

                    String type = ratePlanDtlJson.get("type").toString();
                    String strDepth = "";
                    if(type.equals("standalone")){ // roomOnly
                        strDepth = "1";
                    }else{ // package
                        strDepth = "2";
                    }

                    JSONObject mealJson = (JSONObject) ratePlanDtlJson.get("meal");
                    boolean breakfastYn = (boolean) mealJson.get("breakfast");
                    String strBreakFastCode = "";
                    if(breakfastYn){
                        strBreakFastCode = accomodationMapper.getStrCodeByStrName("RM_ICON", "조식");
                    }

                    rmtypeData += strRmtypeData + strRatePlanId + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                            strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strImgDatas + "{{^}}";
                }
                rmtypeData = rmtypeData.substring(0, rmtypeData.length()-5);
            }else{
                JSONObject ratePlanDtlJson = getRatePlanDetail(strPropertyID, strRmtypeID, strRateplanID);

                JSONObject lengthOfStay = (JSONObject) ratePlanDtlJson.get("length_of_stay");
                int intMinSleep = Integer.parseInt(lengthOfStay.get("min").toString());
                int intMaxSleep = Integer.parseInt(lengthOfStay.get("max").toString());

                boolean refundYn = (boolean) ratePlanDtlJson.get("refundable");
                String strRefundYn = "";
                if(refundYn){
                    strRefundYn = "Y";
                }else{
                    strRefundYn = "N";
                }

                String type = ratePlanDtlJson.get("type").toString();
                String strDepth = "";
                if(type.equals("standalone")){ // roomOnly
                    strDepth = "1";
                }else{ // package
                    strDepth = "2";
                }

                JSONObject mealJson = (JSONObject) ratePlanDtlJson.get("meal");
                boolean breakfastYn = (boolean) mealJson.get("breakfast");
                String strBreakFastCode = "";
                if(breakfastYn){
                    strBreakFastCode = accomodationMapper.getStrCodeByStrName("RM_ICON", "조식");
                }

                rmtypeData += strRmtypeData + strRateplanID + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                        strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strImgDatas + "{{^}}";
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return rmtypeData;
    }

    // 특정 패키지의 재고 및 요금 정보 가져와서 insert or update
    public void updateGoods(int rateplan_id, String from, String to){
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "inventories?rateplan_id=" + rateplan_id + "&from=" + from + "&to=" + to)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                message = gson.toJson(responseJson);

                JSONArray inventoryArr = (JSONArray) responseJson.get("inventories");

                List<JSONObject> inventoryList = new ArrayList<>();
                for(int i=0; i<inventoryArr.size(); i++){
                    JSONObject jsonObject = (JSONObject) inventoryArr.get(i);
                    inventoryList.add(jsonObject);
                }

                for(Object inventories : inventoryList){
                    JSONObject inventoryJson = (JSONObject) inventories;

                    int intStock = Integer.parseInt(inventoryJson.get("vacancy").toString());
                    String strCheckInDate = inventoryJson.get("date").toString();
                    int intBasicPrice = Integer.parseInt(inventoryJson.get("basic_price").toString());
                    int intSalePrice = Integer.parseInt(inventoryJson.get("sale_price").toString());

                    JSONObject lengthOfStay = (JSONObject) inventoryJson.get("length_of_stay");

                    int intMinSleep = Integer.parseInt(lengthOfStay.get("min").toString());
                    int intMaxSleep = Integer.parseInt(lengthOfStay.get("max").toString());


                    String strGoodsIdx = accomodationMapper.updateGoods(rateplan_id, intStock, strCheckInDate,
                            intBasicPrice, intSalePrice, intMinSleep, intMaxSleep);

                    if(strGoodsIdx.equals("")){
                        message = "strGoodsIdx : " + strGoodsIdx + " - > update success";
                    }else{
                        message = "strGoodsIdx : " + strGoodsIdx + " - > update fail";
                    }
                }
            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch(Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
    }

    // CONTENTS_PHOTO, CONDO_PHOTO에 INSERT
    public String accommPhotoContentsReg(String strImage, int intSize, String strPropertyID, String strRmtypeID){
        String strAccommPhotoContent = "";
        try{
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 148; // 이미지 생성한사람 148 : 손유정(employ테이블)
            int intModifiedSID = 148; // 이미지 수정한사람

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
                directoryPath = Paths.get(Constants.ondaFileDir + strPropertyID + "\\" + intSize + "\\");
                filePath = Constants.ondaFileDir + strPropertyID + "\\" + intSize + "\\" + strFileName;
                strFilePath = "/onda/" + strPropertyID + "/" + intSize + "/";
            }else{ // 객실 이미지일 경우
                directoryPath = Paths.get(Constants.ondaFileDir + strPropertyID + "\\" + strRmtypeID + "\\" + intSize + "\\");
                filePath = Constants.ondaFileDir + strPropertyID + "\\" + strRmtypeID + "\\" + intSize + "\\" + strFileName;
                strFilePath = "/onda/" + strPropertyID + "/" + strRmtypeID + "/" + intSize + "/";
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

    // 상태값 세팅
    public Map getStatusYn(String strRoomTypeStatus){
        Map<String, Object> statusMap = new HashMap<>();
        String strDeleteYn = "";
        String strIngYn = "";
        if(strRoomTypeStatus.equals("enabled")){
            strDeleteYn = "N";
            strIngYn = "Y";
        }else if(strRoomTypeStatus.equals("disabled")){
            strDeleteYn = "N";
            strIngYn = "N";
        }else if(strRoomTypeStatus.equals("deleted")){
            strDeleteYn = "Y";
            strIngYn = "N";
        }
        statusMap.put("strDeleteYn", strDeleteYn);
        statusMap.put("strIngYn", strIngYn);
        return statusMap;
    }

    public boolean webhookProcess(JSONObject bodyJson, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        boolean result = false;
        try{
            String event_type = bodyJson.get("event_type").toString();
            if(event_type.equals("contents_updated")){
                JSONObject event_detail = (JSONObject) bodyJson.get("event_detail");
                String target = event_detail.get("target").toString();

                if(target.equals("property")){
                    String strPropertyID = event_detail.get("property_id").toString();
                    result = updateAccomm(strPropertyID);
                }else if(target.equals("roomtype") || target.equals("rateplan")){
                    // roomType & ratePlane은 DB구조상 데이터 조합해서 넣어야하기 때문에 같이 수정
                    String strPropertyID = event_detail.get("property_id").toString();
                    String strRmtypeID = event_detail.get("roomtype_id").toString();
                    String strRateplanID = "";
                    if(target.equals("rateplan")){
                        strRateplanID = event_detail.get("rateplan_id").toString();
                    }
                    updateRmtype(strPropertyID, strRmtypeID, strRateplanID);
                }
            }else if(event_type.equals("status_updated")){
                JSONObject event_detail = (JSONObject) bodyJson.get("event_detail");
                String target = event_detail.get("target").toString();

                String strPropertyID = "";
                String strRmtypeID = "";
                String strRateplanID = "";

                if(target.equals("property")){
                    strPropertyID = event_detail.get("property_id").toString();

                }else if(target.equals("roomtype")){
                    strPropertyID = event_detail.get("property_id").toString();
                    strRmtypeID = event_detail.get("roomtype_id").toString();

                }else if(target.equals("rateplan")){
                    strPropertyID = event_detail.get("property_id").toString();
                    strRmtypeID = event_detail.get("roomtype_id").toString();
                    strRateplanID = event_detail.get("rateplan_id").toString();
                }

                String status = event_detail.get("status").toString();
                Map<String, String> statusMap = getStatusYn(status);
                String strDeleteYn = statusMap.get("strDeleteYn");
                String strViewYn = statusMap.get("strIngYn");

                String updateStatusresult = accomodationMapper.updateStatus(target, strDeleteYn, strViewYn, strPropertyID, strRmtypeID, strRateplanID);
                if(!updateStatusresult.equals("")){
                    result = true;
                }
            }else if(event_type.equals("inventory_updated")){
                JSONArray eventDetailsArr = (JSONArray) bodyJson.get("event_details");
                for(int i=0; i<eventDetailsArr.size(); i++){
                    JSONObject event_detail = (JSONObject) eventDetailsArr.get(i);

//                String propertyId = event_detail.get("property_id").toString();
//                String roomtypeId = event_detail.get("roomtype_id").toString();
                    int rateplanId = Integer.parseInt(event_detail.get("rateplan_id").toString());
                    String strCheckInDate = event_detail.get("date").toString();
                    int intBasicPrice = Integer.parseInt(event_detail.get("basic_price").toString());
                    int intSalePrice = Integer.parseInt(event_detail.get("sale_price").toString());
//                String promotionType = event_detail.get("promotion_type").toString();
                    int intStock = Integer.parseInt(event_detail.get("vacancy").toString());

                    // 있으면 업데이트 없으면 생성
                    accomodationMapper.updateGoods(rateplanId, intStock, strCheckInDate,
                            intBasicPrice, intSalePrice, 0, 0);

                }
            }
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return result;
    }


    public List<JSONObject> getAccommListApi(String path){
        List<JSONObject> accommList = new ArrayList<>();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(path)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

            LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try {
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                message = gson.toJson(responseJson);

                JSONArray accommArray = (JSONArray) responseJson.get("properties");

                for(int i=0; i<accommArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) accommArray.get(i);
                    accommList.add(jsonObject);
                }
            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return accommList;
    }

    public JSONObject getAccommDetailApi(String property_id){
        JSONObject jsonObject = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + property_id)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try{
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("property");

                message = gson.toJson(responseJson);
            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return jsonObject;
    }

    public List<JSONObject> getRoomTypeListApi(String property_id){
        List<JSONObject> roomTypeList = new ArrayList<>();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try{
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                message = gson.toJson(responseJson);

                JSONArray roomtypeArray = (JSONArray) responseJson.get("roomtypes");

                for(int i=0; i<roomtypeArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) roomtypeArray.get(i);
                    roomTypeList.add(jsonObject);
                }
            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return roomTypeList;
    }

    public JSONObject getRoomTypeDetail(String property_id, String roomtype_id){
        JSONObject jsonObject = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try{
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("roomtype");

                message = gson.toJson(responseJson);

            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return jsonObject;
    }

    public List<JSONObject> getRatePlanList(String property_id, String roomtype_id){
        List<JSONObject> packageList = new ArrayList<>();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id + "/rateplans")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try{
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                message = gson.toJson(responseJson);

                JSONArray packageArray = (JSONArray) responseJson.get("rateplans");

                for(int i=0; i<packageArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) packageArray.get(i);
                    packageList.add(jsonObject);
                }
            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return packageList;
    }

    public JSONObject getRatePlanDetail(String property_id, String roomtype_id, String rateplan_id){
        JSONObject jsonObject = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "properties/" + property_id + "/roomtypes/" + roomtype_id + "/rateplans/" + rateplan_id)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", Constants.ondaAuth)
                .build();

        LogWriter logWriter = new LogWriter(request.method(), request.url().toString(), System.currentTimeMillis());
        try{
            Response response = client.newCall(request).execute();
            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("rateplan");

                message = gson.toJson(responseJson);
            }else{
                message = "response code : " + response.code();
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return jsonObject;
    }

}
