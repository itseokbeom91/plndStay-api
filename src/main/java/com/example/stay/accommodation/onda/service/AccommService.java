package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.OndaMapper;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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
    private OndaMapper ondaMapper;

    CommonFunction commonFunction = new CommonFunction();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // 시설, 룸타입, ratePlan 등록
    public String getAccommInfo(String dataType, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        // 전체 숙소 리스트 조회
        String accommListUrl = "properties?status=all";
        JSONObject accommListJson = callOndaAPI(accommListUrl);

        List<JSONObject> accommList = new ArrayList<>();
        JSONArray accommArray = (JSONArray) accommListJson.get("properties");
        for(int i=0; i<accommArray.size(); i++){
            JSONObject jsonObject = (JSONObject) accommArray.get(i);
            accommList.add(jsonObject);
        }

        try{
            for(JSONObject accomm : accommList){
                String strPropertyID = accomm.get("id").toString();
                
                // 시설 상세정보 조회
                String accommDetailUrl = "properties/" + strPropertyID;
                JSONObject acmDetailJson = callOndaAPI(accommDetailUrl);
                JSONObject accommDetailJson = (JSONObject) acmDetailJson.get("property");

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

                Map<String, String> districtMap = ondaMapper.getDistrictCode(strRegion, strCity);
                String strDistrict1 = districtMap.get("strDistrict1");
                String strDistrict2 = districtMap.get("strDistrict2");

                String address1 = address.get("address1").toString();
                String address2 = address.get("address2").toString();
                String addressDetail = address.get("address_detail").toString();
                String strAddr1 = address1 + address2;
                String strAddr2 = addressDetail;
                String strZipCode = address.get("postal_code").toString();

                JSONObject location = (JSONObject) address.get("location");
                String strLat = location.get("latitude").toString();
                String strLon = location.get("longitude").toString();

                JSONArray classifyArr = (JSONArray) accommDetailJson.get("classifications") ;
                String strType = classifyArr.get(0).toString();
                if(strType.equals("호텔")){
                    strType = "H";
                }else if(strType.equals("펜션")){
                    strType = "P";
                }else if(strType.equals("레지던스")){
                    strType = "R";
                }else if(strType.equals("게스트하우스")){
                    strType = "GH";
                }else if(strType.equals("모텔")){
                    strType = "M";
                }else if(strType.equals("카라반") || strType.equals("글램핑") || strType.equals("캠핑")){
                    strType = "CP";
                }else{
                    strType = "C";
                }

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
                if(!keywordList.isEmpty()){
                    for(int i=0; i<keywordList.size(); i++){
                        strKeyWordDatas += keywordList.get(i) + "{{|}}";
                    }
                    strKeyWordDatas = strKeyWordDatas.substring(0, strKeyWordDatas.length()-5);
                }

                String strAttractionDatas = "";
                if(!attractionList.isEmpty()){
                    for(int i=0; i<attractionList.size(); i++){
                        strAttractionDatas += attractionList.get(i) + "{{|}}";
                    }
                    strAttractionDatas = strAttractionDatas.substring(0, strAttractionDatas.length()-5);
                }

                String strFacilityDatas = "";
                String facility = "";
                if(!facilityList.isEmpty()){
                    for(int i=0; i<facilityList.size(); i++){

                        if(facilityList.get(i).equals("수화물 보관")){
                            facilityList.set(i, "수화물보관");
                        }
                        if(facilityList.get(i).equals("매점/편의점")){
                            facilityList.set(i, "마트/편의점");
                        }
                        if(facilityList.get(i).equals("피트니스")){
                            facilityList.set(i, "피트니스센터");
                        }

                        facility = ondaMapper.getStrCodeByStrName("ACCOMM_ADD_FAC", facilityList.get(i));

                        if(facility != null){
                            strFacilityDatas += facility + "{{|}}";
                        }
                    }

                    strFacilityDatas = strFacilityDatas.substring(0, strFacilityDatas.length()-5);
                }

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

                // 룸타입 리스트 조회
                String roomTypeListUrl = "properties/" + strPropertyID + "/roomtypes";
                JSONObject roomTypeListJson = callOndaAPI(roomTypeListUrl);

                JSONArray roomtypeArray = (JSONArray) roomTypeListJson.get("roomtypes");
                List<JSONObject> roomTypeList = new ArrayList<>();
                for(int i=0; i<roomtypeArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) roomtypeArray.get(i);
                    roomTypeList.add(jsonObject);
                }

                String strRmtypeDatas = "";
                for (JSONObject roomType : roomTypeList) {
                    String strRmtypeID = roomType.get("id").toString();

                    String roomDetailUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID;
                    JSONObject rmDetailJson = callOndaAPI(roomDetailUrl);
                    JSONObject roomDetailJson = (JSONObject) rmDetailJson.get("roomtype");

                    JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
                    int intQuanStd = Integer.parseInt(capacity.get("standard").toString());
                    int intQuanMax = Integer.parseInt(capacity.get("max").toString());

                    JSONObject rmTags = (JSONObject) roomDetailJson.get("tags");
                    JSONArray tagArr = (JSONArray) rmTags.get("views");
                    List<String> tagList = new ArrayList<>();
                    if(tagArr != null){
                        for(Object t : tagArr){
                           tagList.add(t.toString());
                        }

                    }

                    String strRmTagDatas = "";
                    if(!tagList.isEmpty()){
                        for(String t : tagList){
                            strRmTagDatas += ondaMapper.getStrCodeByStrName("RM_STD_VIEW", t) + "{{~}}";
                        }
                        strRmTagDatas = strRmTagDatas.substring(0, strRmTagDatas.length()-5);
                    }

                    String strRmDescription = roomDetailJson.get("description").toString();
                    int intRmsize = Integer.parseInt(roomDetailJson.get("size").toString());

                    String strRoomTypeStatus = roomDetailJson.get("status").toString();
                    Map<String, String> rmStatusMap = getStatusYn(strRoomTypeStatus);
                    String strRmDeleteYn = rmStatusMap.get("strDeleteYn");
                    String strIngYn = rmStatusMap.get("strIngYn");

                    String strRmtypeName = roomDetailJson.get("name").toString();

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

                    String strRmtypeData = strRmDeleteYn + "|^|" + strIngYn  + "|^|" + intQuanStd + "|^|" + intQuanMax + "|^|" +
                            intRmsize + "|^|" + strRmtypeName + "|^|" + strRmDescription + "|^|" + strRmtypeID + "|^|" + strRmTagDatas + "|^|";

                    // rateplan 리스트 조회
                    String ratePlanListUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID + "/rateplans";
                    JSONObject ratePlanJson = callOndaAPI(ratePlanListUrl);

                    JSONArray ratePlanArray = (JSONArray) ratePlanJson.get("rateplans");
                    List<JSONObject> ratePlanList = new ArrayList<>();
                    for(int i=0; i<ratePlanArray.size(); i++){
                        JSONObject jsonObject = (JSONObject) ratePlanArray.get(i);
                        ratePlanList.add(jsonObject);
                    }

                    for(JSONObject ratePlan : ratePlanList){
                        String strRatePlanId = ratePlan.get("id").toString();

                        // ratePlan 상세정보 조회
                        String ratePlanDtlUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID + "/rateplans/" + strRatePlanId;
                        JSONObject ratePlanDetailJson = callOndaAPI(ratePlanDtlUrl);
                        JSONObject ratePlanDtlJson = (JSONObject) ratePlanDetailJson.get("rateplan");

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
                            strBreakFastCode = ondaMapper.getStrCodeByStrName("RM_ICON", "조식");
                        }

                        strRmtypeDatas += strRmtypeData + strRatePlanId + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                                strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strRmImgDatas + "{{^}}";
                    }
                    strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5) + "{{|}}";
                }

                if(strRmtypeDatas.length() != 0){
                    strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5);
                }

                String result = ondaMapper.insertAccommTotal(strPropertyID, strDeleteYn, strViewYn, strType,
                        strDistrict1, strDistrict2, strSubject, strLat, strLon, strCheckIn, strCheckOut,
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
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 시설 수정(시설+이미지+취소규정+키워드)
    public JSONObject updateAccommInfo(String strPropertyID){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        boolean updateResult = false;
        JSONObject resultJson = new JSONObject();
        try{
            // 시설 상세정보 조회
            String accommDetailUrl = "properties/" + strPropertyID;
            JSONObject acmDetailJson = callOndaAPI(accommDetailUrl);
            JSONObject accommDetailJson = (JSONObject) acmDetailJson.get("property");

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

            Map<String, String> districtMap = ondaMapper.getDistrictCode(strRegion, strCity);
            String strDistrict1 = districtMap.get("strDistrict1");
            String strDistrict2 = districtMap.get("strDistrict2");

            String address1 = address.get("address1").toString();
            String address2 = address.get("address2").toString();
            String addressDetail = address.get("address_detail").toString();
            String strAddr1 = address1 + address2;
            String strAddr2 = addressDetail;
            String strZipCode = address.get("postal_code").toString();

            JSONObject location = (JSONObject) address.get("location");
            String strLat = location.get("latitude").toString();
            String strLon = location.get("longitude").toString();

            JSONArray classifyArr = (JSONArray) accommDetailJson.get("classifications") ;
            String strType = classifyArr.get(0).toString();
            if(strType.equals("호텔")){
                strType = "H";
            }else if(strType.equals("펜션")){
                strType = "P";
            }else if(strType.equals("레지던스")){
                strType = "R";
            }else if(strType.equals("게스트하우스")){
                strType = "GH";
            }else if(strType.equals("모텔")){
                strType = "M";
            }else if(strType.equals("카라반") || strType.equals("글램핑") || strType.equals("캠핑")){
                strType = "CP";
            }else{
                strType = "C";
            }

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
            if(!keywordList.isEmpty()){
                for(int i=0; i<keywordList.size(); i++){
                    strKeyWordDatas += keywordList.get(i) + "{{|}}";
                }
                strKeyWordDatas = strKeyWordDatas.substring(0, strKeyWordDatas.length()-5);
            }

            String strAttractionDatas = "";
            if(!attractionList.isEmpty()){
                for(int i=0; i<attractionList.size(); i++){
                    strAttractionDatas += attractionList.get(i) + "{{|}}";
                }
                strAttractionDatas = strAttractionDatas.substring(0, strAttractionDatas.length()-5);
            }

            String strFacilityDatas = "";
            String facility = "";
            if(!facilityList.isEmpty()){
                for(int i=0; i<facilityList.size(); i++){

                    if(facilityList.get(i).equals("수화물 보관")){
                        facilityList.set(i, "수화물보관");
                    }
                    if(facilityList.get(i).equals("매점/편의점")){
                        facilityList.set(i, "마트/편의점");
                    }

                    facility = ondaMapper.getStrCodeByStrName("ACCOMM_ADD_FAC", facilityList.get(i));

                    strFacilityDatas += facility + "{{|}}";
                }

                strFacilityDatas = strFacilityDatas.substring(0, strFacilityDatas.length()-5);
            }

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

            String result = ondaMapper.insertAccommTotal(strPropertyID, strDeleteYn, strViewYn, strType,
                    strDistrict1, strDistrict2, strSubject, strLat, strLon, strCheckIn, strCheckOut,
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
            e.printStackTrace();
        }

        resultJson.put("statusCode", statusCode);
        resultJson.put("message", message);
        resultJson.put("updateResult", updateResult);
        return resultJson;
    }

    // 룸타입+옵션 등록 및 수정
    public JSONObject updateRmtypeInfo(String strPropertyID, String strRmtypeID, String strRateplanID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        boolean updateResult = false;
        JSONObject resultJson = new JSONObject();
        try{
            String strRmtypeDatas = "";

            String roomDetailUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID;
            JSONObject rmDetailJson = callOndaAPI(roomDetailUrl);
            JSONObject roomDetailJson = (JSONObject) rmDetailJson.get("roomtype");

            JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
            int intQuanStd = Integer.parseInt(capacity.get("standard").toString());
            int intQuanMax = Integer.parseInt(capacity.get("max").toString());

            JSONObject rmTags = (JSONObject) roomDetailJson.get("tags");
            JSONArray tagArr = (JSONArray) rmTags.get("views");
            List<String> tagList = new ArrayList<>();
            if(tagArr != null){
                for(Object t : tagArr){
                    tagList.add(t.toString());
                }

            }

            String strRmTagDatas = "";
            if(!tagList.isEmpty()){
                for(String t : tagList){
                    strRmTagDatas += ondaMapper.getStrCodeByStrName("RM_STD_VIEW", t) + "{{~}}";
                }
                strRmTagDatas = strRmTagDatas.substring(0, strRmTagDatas.length()-5);
            }

            String strRmDescription = roomDetailJson.get("description").toString();

            String strRoomTypeStatus = roomDetailJson.get("status").toString();
            Map<String, String> rmStatusMap = getStatusYn(strRoomTypeStatus);
            String strRmDeleteYn = rmStatusMap.get("strDeleteYn");
            String strIngYn = rmStatusMap.get("strIngYn");

            String strRmtypeName = roomDetailJson.get("name").toString();

            int intRmsize = Integer.parseInt(roomDetailJson.get("size").toString());

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

            String strRmtypeData = strRmDeleteYn + "|^|" + strIngYn  + "|^|" + intQuanStd + "|^|" + intQuanMax + "|^|" +
                    intRmsize + "|^|" + strRmtypeName + "|^|" + strRmDescription + "|^|" + strRmtypeID + "|^|" + strRmTagDatas + "|^|";

            // strRateplanID가 특정되어있으면 반복문X
            if(strRateplanID.equals("")){
                // rateplan 리스트 조회
                String ratePlanListUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID + "/rateplans";
                JSONObject ratePlanJson = callOndaAPI(ratePlanListUrl);

                JSONArray ratePlanArray = (JSONArray) ratePlanJson.get("rateplans");
                List<JSONObject> ratePlanList = new ArrayList<>();
                for(int i=0; i<ratePlanArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) ratePlanArray.get(i);
                    ratePlanList.add(jsonObject);
                }

                for(JSONObject ratePlan : ratePlanList){
                    String strRatePlanId = ratePlan.get("id").toString();

                    // ratePlan 상세정보 조회
                    String ratePlanDtlUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID + "/rateplans/" + strRatePlanId;
                    JSONObject ratePlanDetailJson = callOndaAPI(ratePlanDtlUrl);
                    JSONObject ratePlanDtlJson = (JSONObject) ratePlanDetailJson.get("rateplan");

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
                        strBreakFastCode = ondaMapper.getStrCodeByStrName("RM_ICON", "조식");
                    }

                    strRmtypeDatas += strRmtypeData + strRatePlanId + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                            strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strRmImgDatas + "{{^}}";
                }
                strRmtypeDatas = strRmtypeDatas.substring(0, strRmtypeDatas.length()-5);
            }else{
                // ratePlan 상세정보 조회
                String ratePlanDtlUrl = "properties/" + strPropertyID + "/roomtypes/" + strRmtypeID + "/rateplans/" + strRateplanID;
                JSONObject ratePlanDetailJson = callOndaAPI(ratePlanDtlUrl);
                JSONObject ratePlanDtlJson = (JSONObject) ratePlanDetailJson.get("rateplan");

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
                    strBreakFastCode = ondaMapper.getStrCodeByStrName("RM_ICON", "조식");
                }

                strRmtypeDatas = strRmtypeData + strRateplanID + "|^|" + intMinSleep + "|^|" + intMaxSleep + "|^|" +
                        strBreakFastCode + "|^|" + strDepth + "|^|" + strRefundYn + "|^|" + strRmImgDatas;
            }
            
            String result = ondaMapper.updateRmtype(strPropertyID, strRmtypeDatas);

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
            e.printStackTrace();
        }
        resultJson.put("statusCode", statusCode);
        resultJson.put("message", message);
        resultJson.put("updateResult", updateResult);
        return resultJson;
    }


    // 특정 패키지의 재고 및 요금 정보 가져와서 insert or update
    public String updateRoomStock(String dataType, int intRmIdx, String from, String to){
        String statusCode = "200";
        String message = "";

        Map<String, Object> map = ondaMapper.getStrRateplanIDNIntAID(intRmIdx);
        String strRateplanID = map.get("strRateplanID").toString();
        int intAID = Integer.parseInt(map.get("intAID").toString());

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

                String strStockDatas = "";
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

                    /**
                     * 임시
                     */
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

                    strStockDatas += strDateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                            + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + doubleOmkSales+ "{{|}}";
                }

                strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                String result = ondaMapper.updateGoods(intAID, intRmIdx, strStockDatas);
                String strResult = result.substring(result.length()-4);
                if(strResult.equals("저장완료")){
                    message = "재고 등록/수정 완료";
                }else{
                    logWriter.add(result);
                    message = "재고 등록/수정 실패";
                }

            }else{
                logWriter.add("ONDA response code : " + response.code());
                message = "재고 등록/수정 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch(Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 기간내 최저가 숙소 정보 조회
    public String getLowestPrice(String dataType, int intAID, String from, String to, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());

        String statusCode = "200";
        String message = "";

        try{
            String strPropertyID = ondaMapper.getPropertyID(intAID);
            String url = "properties/" + strPropertyID + "/lowest_price?from=" + from + "&to=" + to;
            JSONObject lowestAccomm = callOndaAPI(url);
            
            // TODO : 조회한 날짜별 금액 어디에 저장해서 쓸건지 필요
            if(lowestAccomm != null){
                JSONArray lowestPriceArr = (JSONArray) lowestAccomm.get("lowest_prices");
                for(Object lp : lowestPriceArr){
                    JSONObject lowestJson = (JSONObject) lp;

                    String strDate = lowestJson.get("date").toString();
                    int intLowestPrice = Integer.parseInt(lowestJson.get("lowest_price").toString());
                }
            }else{
                message = "onda api 호출 실패";
            }
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
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
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        boolean result = false;
        try{
            String event_type = bodyJson.get("event_type").toString();
            if(event_type.equals("contents_updated")){
                JSONObject event_detail = (JSONObject) bodyJson.get("event_detail");
                String target = event_detail.get("target").toString();

                if(target.equals("property")){
                    String strPropertyID = event_detail.get("property_id").toString();
                    JSONObject updateJson = updateAccommInfo(strPropertyID);
                    result = (boolean) updateJson.get("updateResult");
                }else if(target.equals("roomtype") || target.equals("rateplan")){
                    // roomType & ratePlane은 DB구조상 데이터 조합해서 넣어야하기 때문에 같이 수정
                    String strPropertyID = event_detail.get("property_id").toString();
                    String strRmtypeID = event_detail.get("roomtype_id").toString();
                    String strRateplanID = "";
                    if(target.equals("rateplan")){
                        strRateplanID = event_detail.get("rateplan_id").toString();
                    }
                    JSONObject updateJson = updateRmtypeInfo(strPropertyID, strRmtypeID, strRateplanID, httpServletRequest);
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

                String updateStatusresult = ondaMapper.updateStatus(target, strDeleteYn, strViewYn, strPropertyID, strRmtypeID, strRateplanID);
                if(!updateStatusresult.equals("")){
                    result = true;
                }
            }else if(event_type.equals("inventory_updated")){
                JSONArray eventDetailsArr = (JSONArray) bodyJson.get("event_details");

                String strStockDatas = "";
                for(int i=0; i<eventDetailsArr.size(); i++){
                    JSONObject event_detail = (JSONObject) eventDetailsArr.get(i);

                    String strRmtypeID = event_detail.get("roomtype_id").toString();
                    String strRateplanID = event_detail.get("rateplan_id").toString();

                    int intStock = Integer.parseInt(event_detail.get("vacancy").toString());
                    String strDateSales = event_detail.get("date").toString();
                    int intCost = Integer.parseInt(event_detail.get("basic_price").toString());
                    int intSales = Integer.parseInt(event_detail.get("sale_price").toString());

                    int intOmkStock = intStock;
                    double doubleOmkSales = 0;

                    int year = Integer.parseInt(strDateSales.substring(0, 4));
                    int month = Integer.parseInt(strDateSales.substring(5, 7));
                    int day = Integer.parseInt(strDateSales.substring(8,10));

                    LocalDate date = LocalDate.of(year, month, day);
                    DayOfWeek dayOfWeek = date.getDayOfWeek();

                    /**
                     * 임시
                     */
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

                    int intExtraA = 0, intExtraC = 0, intExtraB = 0;

                    strStockDatas += strRmtypeID + "|^|" +strRateplanID  + "|^|" + strDateSales + "|^|" + intStock + "|^|" + intCost + "|^|" + intSales + "|^|"
                            + intExtraA + "|^|" + intExtraC + "|^|" + intExtraB + "|^|" + intOmkStock + "|^|"  + doubleOmkSales + "{{|}}";
                }
                strStockDatas = strStockDatas.substring(0, strStockDatas.length()-5);

                // 있으면 업데이트 없으면 생성
                String updateResult = ondaMapper.webhookUpdateGoods(strStockDatas);
                String strResult = updateResult.substring(updateResult.length()-4);
                if(strResult.equals("저장완료")){
                    result = true;
                }
            }
            logWriter.add("webhook process result : " + result);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return result;
    }

    public JSONObject callOndaAPI(String url){
        JSONObject responseJson = new JSONObject();
        String message = "";

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.ondaPath + url)
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
                responseJson = (JSONObject) jsonParser.parse(responseBody);
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
        return responseJson;
    }

}
