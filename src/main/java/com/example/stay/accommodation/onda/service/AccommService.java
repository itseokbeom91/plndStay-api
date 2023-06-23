package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.AccommMapper;
import com.example.stay.common.util.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@Service("onda.AccommService")
public class AccommService {

    @Autowired
    private AccommMapper accommMapper;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 시설, 룸타입, ratePlan 등록
    public String insertAccommTotal(HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        // 전체 숙소 리스트 불러오기
        List<JSONObject> accommList = getAccommListApi(Constants.ondaPath + "properties?status=all");
        try{

//            String testAccommList = "{\n" +
//                    "      \"id\": \"54207\",\n" +
//                    "      \"name\": \"채널 테스트&숙소(테스트계정)\",\n" +
//                    "      \"status\": \"enabled\",\n" +
//                    "      \"updated_at\": \"2023-06-09T01:55:50+09:00\"\n" +
//                    "    }";
//
//            String testAccommList2 = "{\n" +
//                    "      \"id\": \"130517\",\n" +
//                    "      \"name\": \"에드워드호텔(거제 호텔상상)\",\n" +
//                    "      \"status\": \"enabled\",\n" +
//                    "      \"updated_at\": \"2023-06-09T02:05:27+09:00\"\n" +
//                    "    }";
//            JSONParser jsonParser = new JSONParser();
//
//            Object obj = jsonParser.parse(testAccommList);
//            JSONObject jsonObj = (JSONObject) obj;
//
//            Object obj2 = jsonParser.parse(testAccommList2);
//            JSONObject jsonObj2 = (JSONObject) obj2;
//
//            List<JSONObject> accommList = new LinkedList<>();
//            accommList.add(jsonObj);
//            accommList.add(jsonObj2);




            for(JSONObject accomm : accommList){
                String strPropertyID = accomm.get("id").toString();
                // 시설

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
                regionMap.put("제주도", "제주특별자치도");

                Iterator<String> keys = regionMap.keySet().iterator();
                while(keys.hasNext()){
                    String key = keys.next();
                    if(key.equals(strRegion)){
                        strRegion = regionMap.get(key);
                    }
                }

                String strCity = address.get("city").toString();

                Map<String, Integer> districtMap = accommMapper.getDistrictCode(strRegion, strCity);
                int intDistrict1 = districtMap.get("intDistrict1");
                int intDistrict2 = districtMap.get("intDistrict2");

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
                List<String> attractionList = new ArrayList<>();

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

                String strAttractionDatas = "";
                if(keywordList != null){
                    for(int i=0; i<attractionList.size(); i++){
                        strAttractionDatas += attractionList.get(i) + "{{|}}";
                    }
                    strAttractionDatas = strAttractionDatas.substring(0, strAttractionDatas.length()-5);
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

                        facility = accommMapper.getStrCodeByStrName("ACCOMM_ADD_FAC", facilityList.get(i));

                        strFacilityDatas += facility + "{{|}}";
                    }

                    strFacilityDatas = strFacilityDatas.substring(0, strFacilityDatas.length()-5);
                }

                String strType = accommMapper.getStrCodeByStrName("ACCOMM_TYPE", "온다");

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



                // 룸타입
                List<JSONObject> roomTypeList = getRoomTypeListApi(strPropertyID);
                String strRmtypeDatas = "";
                for (JSONObject roomType : roomTypeList) {
                    String strRmtypeID = roomType.get("id").toString();

                    JSONObject roomDetailJson = getRoomTypeDetail(strPropertyID, strRmtypeID);

                    JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
                    int intQuanStd = Integer.parseInt(capacity.get("standard").toString());
                    int intQuanMax = Integer.parseInt(capacity.get("max").toString());

                    String strRmDescription = roomDetailJson.get("description").toString();

                    String strRoomTypeStatus = roomDetailJson.get("status").toString();
                    Map<String, String> rmStatusMap = getStatusYn(strRoomTypeStatus);
                    String strRmDeleteYn = rmStatusMap.get("strDeleteYn");
                    String strIngYn = rmStatusMap.get("strIngYn");

                    String strRmSubject = roomDetailJson.get("name").toString();

                    // 이미지------------------------------------------------------------------------------------------------
                    // CONTENTS_PHOTO, RM_PHOTO 테이블에 INSERT
                    JSONArray rmImages = (JSONArray) roomDetailJson.get("images");

                    String strRmImgDatas = "";
                    for(int i=0; i<rmImages.size(); i++){
                        JSONObject image = (JSONObject) rmImages.get(i);

                        String str250Img = image.get("250px").toString();
                        String str500Img = image.get("500px").toString();
                        String str1000Img = image.get("1000px").toString();

                        if(str250Img != null){
                            strRmImgDatas += accommPhotoContentsReg(str250Img, 250, strPropertyID, strRmtypeID) + "{{~}}";
                        }

                        if(str500Img != null){
                            strRmImgDatas += accommPhotoContentsReg(str500Img, 500, strPropertyID, strRmtypeID) + "{{~}}";
                        }

                        if(str1000Img != null){
                            strRmImgDatas += accommPhotoContentsReg(str1000Img, 1000, strPropertyID, strRmtypeID) + "{{~}}";
                        }
                    }

                    if(strRmImgDatas.length() != 0){
                        strRmImgDatas = strRmImgDatas.substring(0, strRmImgDatas.length()-5);
                    }

                    String strRmtypeData = strRmDeleteYn + "|^|" + strIngYn  + "|^|" + intQuanStd + "|^|" +
                            intQuanMax + "|^|" + strRmSubject + "|^|" + strRmDescription + "|^|" + strRmtypeID + "|^|";

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
                            strBreakFastCode = accommMapper.getStrCodeByStrName("RM_ICON", "조식");
                        }

                        strRmtypeDatas += strRmtypeData + strRatePlanId + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                                strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strRmImgDatas + "{{^}}";
                    }
                    strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5) + "{{|}}";
                }

                if(strRmtypeDatas.length() != 0){
                    strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5);
                }

                String result = accommMapper.insertAccommTotal(strPropertyID, strDeleteYn, strViewYn, strType,
                        intDistrict1, intDistrict2, strSubject, strLat, strLon, strCheckIn, strCheckOut,
                        strPhone, strFax, strEmail, strZipCode, strAddr1, strAddr2, strDescription, strRsvGuide,
                        strAcmNotice, strImgDatas, strPenaltyDatas, strKeyWordDatas, strAttractionDatas, strFacilityDatas, strRmtypeDatas);

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
        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(statusCode, message);
    }

    // 시설 수정(시설+이미지+취소규정+키워드)
    public JSONObject updateAccomm(String strPropertyID){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        boolean updateResult = false;
        JSONObject resultJson = new JSONObject();
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
            regionMap.put("제주도", "제주특별자치도");

            Iterator<String> keys = regionMap.keySet().iterator();
            while(keys.hasNext()){
                String key = keys.next();
                if(key.equals(strRegion)){
                    strRegion = regionMap.get(key);
                }
            }

            String strCity = address.get("city").toString();

            Map<String, Integer> districtMap = accommMapper.getDistrictCode(strRegion, strCity);
            int intDistrict1 = districtMap.get("intDistrict1");
            int intDistrict2 = districtMap.get("intDistrict2");

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
            List<String> attractionList = new ArrayList<>();

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
                    attractionList.add(a.toString());
                }
            }

            String strKeyWordDatas = "";
            if(keywordList != null){
                for(int i=0; i<keywordList.size(); i++){
                    strKeyWordDatas += keywordList.get(i) + "{{|}}";
                }
                strKeyWordDatas = strKeyWordDatas.substring(0, strKeyWordDatas.length()-5);
            }

            String strAttractionDatas = "";
            if(keywordList != null){
                for(int i=0; i<attractionList.size(); i++){
                    strAttractionDatas += attractionList.get(i) + "{{|}}";
                }
                strAttractionDatas = strAttractionDatas.substring(0, strAttractionDatas.length()-5);
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

                    facility = accommMapper.getStrCodeByStrName("ACCOMM_ADD_FAC", facilityList.get(i));

                    strFacilityDatas += facility + "{{|}}";
                }

                strFacilityDatas = strFacilityDatas.substring(0, strFacilityDatas.length()-5);
            }

            String strType = accommMapper.getStrCodeByStrName("ACCOMM_TYPE", "온다");

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

            String result = accommMapper.insertAccommTotal(strPropertyID, strDeleteYn, strViewYn, strType,
                    intDistrict1, intDistrict2, strSubject, strLat, strLon, strCheckIn, strCheckOut,
                    strPhone, strFax, strEmail, strZipCode, strAddr1, strAddr2, strDescription, strRsvGuide,
                    strAcmNotice, strImgDatas, strPenaltyDatas, strKeyWordDatas, strAttractionDatas, strFacilityDatas, "");

            if(result.equals("")){
                message = "시설 수정 완료";
                updateResult = true;
            }else{
                logWriter.add(result);
                message = "시설 수정 실패";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            statusCode = "500";

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        resultJson.put("statusCode", statusCode);
        resultJson.put("message", message);
        resultJson.put("updateResult", updateResult);
        return resultJson;
    }

    // 룸타입+옵션 등록 및 수정
    public JSONObject updateRmtype(String strPropertyID, String strRmtypeID, String strRateplanID){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        boolean updateResult = false;
        JSONObject resultJson = new JSONObject();
        try{
            String strRmtypeDatas = "";
            JSONObject roomDetailJson = getRoomTypeDetail(strPropertyID, strRmtypeID);

            JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
            int intQuanStd = Integer.parseInt(capacity.get("standard").toString());
            int intQuanMax = Integer.parseInt(capacity.get("max").toString());

            String strRmDescription = roomDetailJson.get("description").toString();

            String strRoomTypeStatus = roomDetailJson.get("status").toString();
            Map<String, String> rmStatusMap = getStatusYn(strRoomTypeStatus);
            String strRmDeleteYn = rmStatusMap.get("strDeleteYn");
            String strIngYn = rmStatusMap.get("strIngYn");

            String strRmSubject = roomDetailJson.get("name").toString();

            // 이미지------------------------------------------------------------------------------------------------
            // CONTENTS_PHOTO, RM_PHOTO 테이블에 INSERT
            JSONArray rmImages = (JSONArray) roomDetailJson.get("images");

            String strRmImgDatas = "";
            for(int i=0; i<rmImages.size(); i++){
                JSONObject image = (JSONObject) rmImages.get(i);

                String str250Img = image.get("250px").toString();
                String str500Img = image.get("500px").toString();
                String str1000Img = image.get("1000px").toString();

                if(str250Img != null){
                    strRmImgDatas += accommPhotoContentsReg(str250Img, 250, strPropertyID, strRmtypeID) + "{{~}}";
                }

                if(str500Img != null){
                    strRmImgDatas += accommPhotoContentsReg(str500Img, 500, strPropertyID, strRmtypeID) + "{{~}}";
                }

                if(str1000Img != null){
                    strRmImgDatas += accommPhotoContentsReg(str1000Img, 1000, strPropertyID, strRmtypeID) + "{{~}}";
                }
            }

            if(strRmImgDatas.length() != 0){
                strRmImgDatas = strRmImgDatas.substring(0, strRmImgDatas.length()-5);
            }

            String strRmtypeData = strRmDeleteYn + "|^|" + strIngYn  + "|^|" + intQuanStd + "|^|" +
                    intQuanMax + "|^|" + strRmSubject + "|^|" + strRmDescription + "|^|" + strRmtypeID + "|^|";

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
                        strBreakFastCode = accommMapper.getStrCodeByStrName("RM_ICON", "조식");
                    }

                    strRmtypeDatas += strRmtypeData + strRatePlanId + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                            strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strRmImgDatas + "{{^}}";
                }
                strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5);
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
                    strBreakFastCode = accommMapper.getStrCodeByStrName("RM_ICON", "조식");
                }

                strRmtypeDatas = strRmtypeData + strRateplanID + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                        strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strRmImgDatas;
            }

            String strType = accommMapper.getStrCodeByStrName("ACCOMM_TYPE", "온다");

            String result = accommMapper.updateRmtype(strPropertyID, strType, strRmtypeDatas);

            if(result.equals("")){
                message = "객실 등록 및 수정 완료";
                updateResult = true;
            }else{
                logWriter.add(result);
                message = "객실 등록 및 수정 실패";
            }
            logWriter.add(message);
            logWriter.log(0);

        }catch (Exception e){
            statusCode = "500";

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        resultJson.put("statusCode", statusCode);
        resultJson.put("message", message);
        resultJson.put("updateResult", updateResult);
        return resultJson;
    }


    // 특정 패키지의 재고 및 요금 정보 가져와서 insert or update
    public void updateGoods(String strRateplanID, String strRmtypeID, String from, String to){
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + "inventories?rateplan_id=" + strRateplanID + "&from=" + from + "&to=" + to)
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
                    String strDateSales = inventoryJson.get("date").toString();
                    int intCost = Integer.parseInt(inventoryJson.get("basic_price").toString());
                    int intSales = Integer.parseInt(inventoryJson.get("sale_price").toString());

                    int intExtraA = Integer.parseInt(inventoryJson.get("extra_adult").toString());
                    int intExtraC= Integer.parseInt(inventoryJson.get("extra_child").toString());
                    int intExtraB = Integer.parseInt(inventoryJson.get("extra_infant").toString());

                    int intOmkStock = intStock;
                    double doubleOmkSales = 0;

                    int year = Integer.parseInt(strDateSales.substring(0, 4));
                    int month = Integer.parseInt(strDateSales.substring(5, 7));
                    int day = Integer.parseInt(strDateSales.substring(8,10));

                    LocalDate date = LocalDate.of(year, month, day);
                    DayOfWeek dayOfWeek = date.getDayOfWeek();

                    double weekday = 1.09; // 일~목
                    double friday = 1.09; // 금
                    double saturday = 1.1; // 토

                    if(dayOfWeek.getValue() == 7 || dayOfWeek.getValue() == 1 || dayOfWeek.getValue() == 2 ||
                            dayOfWeek.getValue() == 3 || dayOfWeek.getValue() == 4){
                        doubleOmkSales = intSales * weekday;
                    }else if(dayOfWeek.getValue() == 5){
                        doubleOmkSales = intSales * friday;
                    }else if(dayOfWeek.getValue() == 6){
                        doubleOmkSales = intSales * saturday;
                    }


                    String result = accommMapper.updateGoods(strRateplanID, strRmtypeID, strDateSales, intStock,
                            intCost, intSales, intExtraA, intExtraC, intExtraB, intOmkStock, doubleOmkSales);

                    String strResult = result.substring(result.length()-4);
                    if(strResult.equals("저장완료")){
                        message = "재고 등록/수정 완료";
                    }else{
                        logWriter.add(result);
                        message = "재고 등록/수정 실패";
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
                    JSONObject updateJson = updateAccomm(strPropertyID);
                    result = (boolean) updateJson.get("updateResult");
                }else if(target.equals("roomtype") || target.equals("rateplan")){
                    // roomType & ratePlane은 DB구조상 데이터 조합해서 넣어야하기 때문에 같이 수정
                    String strPropertyID = event_detail.get("property_id").toString();
                    String strRmtypeID = event_detail.get("roomtype_id").toString();
                    String strRateplanID = "";
                    if(target.equals("rateplan")){
                        strRateplanID = event_detail.get("rateplan_id").toString();
                    }
                    JSONObject updateJson = updateRmtype(strPropertyID, strRmtypeID, strRateplanID);
                    result = (boolean) updateJson.get("updateResult");
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

                String updateStatusresult = accommMapper.updateStatus(target, strDeleteYn, strViewYn, strPropertyID, strRmtypeID, strRateplanID);
                if(!updateStatusresult.equals("")){
                    result = true;
                }
            }else if(event_type.equals("inventory_updated")){
                JSONArray eventDetailsArr = (JSONArray) bodyJson.get("event_details");
                for(int i=0; i<eventDetailsArr.size(); i++){
                    JSONObject event_detail = (JSONObject) eventDetailsArr.get(i);

                    String strRmtypeID = event_detail.get("roomtype_id").toString();
                    String strRateplanID = event_detail.get("rateplan_id").toString();

                    int intStock = Integer.parseInt(event_detail.get("vacancy").toString());
                    String strDateSales = event_detail.get("date").toString();
                    int intCost = Integer.parseInt(event_detail.get("basic_price").toString());
                    int intSales = Integer.parseInt(event_detail.get("sale_price").toString());

//                    int intExtraA = Integer.parseInt(event_detail.get("extra_adult").toString());
//                    int intExtraC= Integer.parseInt(event_detail.get("extra_child").toString());
//                    int intExtraB = Integer.parseInt(event_detail.get("extra_infant").toString());

                    int intOmkStock = intStock;
                    double doubleOmkSales = 0;

                    int year = Integer.parseInt(strDateSales.substring(0, 4));
                    int month = Integer.parseInt(strDateSales.substring(5, 7));
                    int day = Integer.parseInt(strDateSales.substring(8,10));

                    LocalDate date = LocalDate.of(year, month, day);
                    DayOfWeek dayOfWeek = date.getDayOfWeek();

                    double weekday = 1.09; // 일~목
                    double friday = 1.09; // 금
                    double saturday = 1.1; // 토

                    if(dayOfWeek.getValue() == 7 || dayOfWeek.getValue() == 1 || dayOfWeek.getValue() == 2 ||
                            dayOfWeek.getValue() == 3 || dayOfWeek.getValue() == 4){
                        doubleOmkSales = intSales * weekday;
                    }else if(dayOfWeek.getValue() == 5){
                        doubleOmkSales = intSales * friday;
                    }else if(dayOfWeek.getValue() == 6){
                        doubleOmkSales = intSales * saturday;
                    }

                    // 있으면 업데이트 없으면 생성
                    String goodsResult = accommMapper.updateGoods(strRateplanID, strRmtypeID, strDateSales, intStock,
                            intCost, intSales, 0, 0, 0, intOmkStock, doubleOmkSales);

                    String strResult = goodsResult.substring(goodsResult.length()-4);
                    if(strResult.equals("저장완료")){
                        result = true;
                    }else{
                        logWriter.add(goodsResult);
                    }
                }
            }
            logWriter.log(0);
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
