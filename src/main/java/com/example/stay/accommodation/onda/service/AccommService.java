package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.AccomodationMapper;
import com.example.stay.common.util.Constants;
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

    public List<JSONObject> getAccommListApi(String path){
        List<JSONObject> accommList = new ArrayList<>();

        try {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(path)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray accommArray = (JSONArray) responseJson.get("properties");

                for(int i=0; i<accommArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) accommArray.get(i);
                    accommList.add(jsonObject);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 전체 숙소 목록 가져오기 실패");
        }
        return accommList;
    }

    public JSONObject getAccommDetailApi(String property_id){
        JSONObject jsonObject = new JSONObject();
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
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("property");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 상세정보 가져오기 실패");
        }
        return jsonObject;
    }

    public List<JSONObject> getRoomTypeListApi(String property_id){
        List<JSONObject> roomTypeList = new ArrayList<>();
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
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray roomtypeArray = (JSONArray) responseJson.get("roomtypes");

                for(int i=0; i<roomtypeArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) roomtypeArray.get(i);
                    roomTypeList.add(jsonObject);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 숙소 전체 객실 목록 가져오기 실패");
        }
        return roomTypeList;
    }

    public JSONObject getRoomTypeDetail(String property_id, String roomtype_id){
        JSONObject jsonObject = new JSONObject();
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
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
                jsonObject = (JSONObject) responseJson.get("roomtype");

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실 상세정보 가져오기 실패");
        }
        return jsonObject;
    }

    public List<JSONObject> getRatePlanList(String property_id, String roomtype_id){
        List<JSONObject> packageList = new ArrayList<>();
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
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray packageArray = (JSONArray) responseJson.get("rateplans");

                for(int i=0; i<packageArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) packageArray.get(i);
                    packageList.add(jsonObject);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실의 전체 패키지 목록 가져오기 실패");
        }
        return packageList;
    }

    public JSONObject getRatePlanDetail(String property_id, String roomtype_id, String rateplan_id){
        JSONObject jsonObject = new JSONObject();
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
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();

                jsonObject = (JSONObject) jsonParser.parse(responseBody);

                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                jsonObject = (JSONObject) responseJson.get("rateplan");
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 패키지의 상세 정보 가져오기 실패");
        }
        return jsonObject;
    }

    public void insertInventories(int rateplan_id, String from, String to){
        try{
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(Constants.ondaPath + "inventories?rateplan_id=" + rateplan_id + "&from=" + from + "&to=" + to)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();
            if(response.isSuccessful()) {
                // response 파싱
                String responseBody = response.body().string();
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray inventoryArr = (JSONArray) responseJson.get("inventories");

                List<JSONObject> inventoryList = new ArrayList<>();
                for(int i=0; i<inventoryArr.size(); i++){
                    JSONObject jsonObject = (JSONObject) inventoryArr.get(i);
                    inventoryList.add(jsonObject);
                }

                for(Object inventories : inventoryList){
                    JSONObject inventoryJson = (JSONObject) inventories;

                    // rateplan_id에 해당하는 정보 DB에서 불러오기
                    StockDto idxDto = accomodationMapper.getIdxsByRatePlanId(rateplan_id);

                    if(idxDto != null){
                        int intCondoID = idxDto.getIntCondoID();
                        int intRoomID = idxDto.getIntRoomID();
                        int intRateID = idxDto.getIntRateID();
                        int intStock = Integer.parseInt(inventoryJson.get("vacancy").toString());
                        String strCheckInDate = inventoryJson.get("date").toString();
                        int intBasicPrice = Integer.parseInt(inventoryJson.get("basic_price").toString());
                        int intSalePrice = Integer.parseInt(inventoryJson.get("sale_price").toString());

                        JSONObject lengthOfStay = (JSONObject) inventoryJson.get("length_of_stay");

                        int intMinStay = Integer.parseInt(lengthOfStay.get("min").toString());
                        int intMaxStay = Integer.parseInt(lengthOfStay.get("max").toString());

                        String strGoodsIdx = accomodationMapper.insertGoods(intCondoID, intRoomID, intRateID,
                                intStock, strCheckInDate, intBasicPrice, intSalePrice, intMinStay, intMaxStay);

                        System.out.println("strGoodsIdx : " + strGoodsIdx);

                    }else{
                        System.out.println("해당하는 ratePlan이 없음. ratePlan 등록 필요");
                    }
                }
            }else{
                System.out.println("[onda API] 특정 패키지의 재고 및 요금 정보 가져오기 실패");
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 시설, 룸타입, ratePlan 등록
    public void insertAccommTotal(String path){
        long startTime = System.currentTimeMillis();
        // 전체 숙소 리스트 불러오기
        List<JSONObject> accommList = getAccommListApi(path);
        try{
            for(JSONObject accomm : accommList){
                String propertyId = accomm.get("id").toString();

                // 시설
                Map<String, Object> condoData = setCondoData(propertyId);

                // 룸타입
                List<JSONObject> roomTypeList = getRoomTypeListApi(propertyId);
                String strRoomNRatePlanDatas = "";
                int intAdminCount = 1;
                for (JSONObject roomType : roomTypeList) {
                    String roomTypeId = roomType.get("id").toString();
                    strRoomNRatePlanDatas = setRoomNRatePlanData(propertyId, roomTypeId, intAdminCount);

                    intAdminCount += 1;
                }
                strRoomNRatePlanDatas = strRoomNRatePlanDatas.substring(0, strRoomNRatePlanDatas.length()-5);

                String strAccommId = condoData.get("strAccommId").toString();
                String API_FLAG = condoData.get("API_FLAG").toString();
                String strCondoName = condoData.get("strCondoName").toString();
                String strConZip = condoData.get("strConZip").toString();
                String strConAddr1 = condoData.get("strConAddr1").toString();
                String strConAddr2 = condoData.get("strConAddr2").toString();
                String strConTel = condoData.get("strConTel").toString();
                String strConFax = condoData.get("strConFax").toString();
                String strConGekNum = condoData.get("strConGekNum").toString();
                String strConFlag = condoData.get("strConFlag").toString();
                String strLocation = condoData.get("strLocation").toString();
                String strHomepage = condoData.get("strHomepage").toString();
                String strTimeIn = condoData.get("strTimeIn").toString();
                String strTimeOut = condoData.get("strTimeOut").toString();
                String strConDisplay = condoData.get("strConDisplay").toString();
                String strMapX = condoData.get("strMapX").toString();
                String strMapY = condoData.get("strMapY").toString();
                String strMobileWarning = condoData.get("strMobileWarning").toString();
                String strCity = condoData.get("strCity").toString();
                String strNation = condoData.get("strNation").toString();
                String strConDesc = condoData.get("strConDesc").toString();
                String strConAround = condoData.get("strConAround").toString();
                String strConSookbak = condoData.get("strConSookbak").toString();
                String strTagName = condoData.get("strTagName").toString();

                String strImgData = condoData.get("strImgData").toString();
                String strPenaltyData = condoData.get("strPenaltyData").toString();

                String result = accomodationMapper.accommRegistTotal(strAccommId, API_FLAG, strCondoName, strConZip,
                        strConAddr1, strConAddr2, strConTel, strConFax, strConGekNum, strConFlag, strLocation, strHomepage,
                        strTimeIn, strTimeOut, strConDisplay, strMapX, strMapY, strMobileWarning, strCity, strNation,
                        strConDesc, strConAround, strConSookbak, strTagName, strImgData, strPenaltyData, strRoomNRatePlanDatas);

                System.out.println("result : " + result);
            }
            System.out.println("시설 INSERT 완료");
            System.out.println("시설 등록 실행 시간 : " + (System.currentTimeMillis()-startTime)/1000.0);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("시설 INSERT FAIL");
        }
    }

    // CONTENTS_PHOTO, CONDO_PHOTO에 INSERT
    public String accommPhotoContentsReg(String strImage, int intSize, String strAccommId, String strCondoName){
        String strAccommPhotoContent = "";
        try{
            /**
             * 임시로 하드코딩
             */
            int intCreatedSID = 50; // 이미지 생성한사람 50 : 손유정(employ테이블)
            int intModifiedSID = 50; // 이미지 수정한사람

            String[] filePathArr = strImage.split("/");
            String strFileName = "";
            for(int j=0; j< filePathArr.length; j++){
                if(j == (filePathArr.length - 1)){
                    strFileName = filePathArr[j];
                }
            }

            // 경로에 폴더 생성 -> 있으면 생성 안시킴
            Path directoryPath = Paths.get(Constants.ondaFileDir + strAccommId + "\\" + intSize + "\\");
            Files.createDirectories(directoryPath);

            // 파일 존재여부 체크
            String filePath = Constants.ondaFileDir + strAccommId + "\\" + intSize + "\\" + strFileName;
            File file = new File(filePath);
            if(!(file.exists())){
                // 이미지 저장
                UrlResourceDownloader downloader = new UrlResourceDownloader(filePath, strImage);
                downloader.urlFileDownload();
            }else{
                System.out.println("ALREADY EXISTS PHOTO");
            }

            String strFilePath = "/onda/" + strAccommId + "/" + intSize + "/";

            strAccommPhotoContent = strFilePath + "|^|" + strFileName + "|^|" + intCreatedSID + "|^|"
                                    + intModifiedSID;

        }catch (Exception e){
            e.printStackTrace();
        }
        return strAccommPhotoContent;
    }

    public String getStatusYn(String strRoomTypeStatus){
        String status = "";
        if(strRoomTypeStatus.equals("disabled") || strRoomTypeStatus.equals("deleted")){
            status = "N";
        }else if(strRoomTypeStatus.equals("enabled")){
            status = "Y";
        }
        return status;
    }

    // 시설 수정(시설+이미지+취소규정)
    public void updateAccomm(String propertyId){
        Map<String, Object> totalData = setCondoData(propertyId);
        String result = accomodationMapper.accommUpdate(totalData);
    }

    // 시설 수정(시설+이미지+취소규정)
    public Map<String, Object> setCondoData(String propertyId){
        Map<String, Object> condoData = new HashMap<>();
        try{
            JSONObject accommDetailJson = getAccommDetailApi(propertyId);
            System.out.println("accommDetailJson : " + gson.toJson(accommDetailJson));

            String strAccommId = accommDetailJson.get("id").toString();
            String strCondoName = accommDetailJson.get("name").toString();

            String strConDisplay = getStatusYn(accommDetailJson.get("status").toString());


            JSONObject address = (JSONObject) accommDetailJson.get("address");
            String strNation = address.get("country_code").toString();
            String strLocation = address.get("region").toString();
            String strCity = address.get("city").toString();

            /**
             * db addr관련 컬럼 정리 필요
             */
            String strConAddr1 = address.get("address1").toString();
            String strConAddr2 = address.get("address2").toString();
            String strConZip = address.get("postal_code").toString();

            JSONObject location = (JSONObject) address.get("location");
            String strMapX = location.get("longitude").toString();
            String strMapY = location.get("latitude").toString();

            String strConTel = accommDetailJson.get("phone").toString();
            String strConFax = accommDetailJson.get("fax").toString();
            String strTimeIn = accommDetailJson.get("checkin").toString();
            String strTimeOut = accommDetailJson.get("checkout").toString();

            JSONObject descriptions = (JSONObject) accommDetailJson.get("descriptions");
            String strMobileWarning = descriptions.get("reservation").toString();
            String strConDesc = descriptions.get("property").toString();

            /**
             * classifications
             * 현재 condo테이블의 con_flag -> int
             * 값이 호텔, 티켓 등 여러개일 가능성도 있음
             * -> 값을 어떻게 넣아햐는지
             */
            JSONArray classifications = (JSONArray) accommDetailJson.get("classifications");
//                List<String> classficationList = new ArrayList<>();
//                for(int i=0; i<classifications.size(); i++){
////                    String code = accomodationMapper.selectCode(classifications.get(i).toString());
//                    classficationList.add(accomodationMapper.selectCode(classifications.get(i).toString());
//                }
//                for(classficationList){
//                    accommDetail.setStrFlag(code);
//                }

            // 어차피 다 태그니까 한 번에 몰아넣자 tags 컬럼에
            JSONObject tags = (JSONObject) accommDetailJson.get("tags");
            JSONArray properties = (JSONArray) tags.get("properties") ;
            JSONArray facilities = (JSONArray) tags.get("facilities");
            JSONArray services = (JSONArray) tags.get("services");
            JSONArray attractions = (JSONArray) tags.get("attractions");

            List<String> tagList = new ArrayList<>();
            if(properties != null){
                for(Object p : properties){
                    tagList.add(p.toString());
                }
            }
            if(facilities != null){
                for(Object f : facilities){
                    tagList.add(f.toString());
                }
            }
            if(services != null){
                for(Object s : services){
                    tagList.add(s.toString());
                }
            }
            if(attractions != null){
                for(Object a : attractions){
                    tagList.add(a.toString());
                }
            }

            String strTagName = "";
            if(tagList != null){
                for(int i=0; i<tagList.size(); i++){
                    if(i == (tagList.size()-1)){
                        strTagName += tagList.get(i);
                    }else{
                        strTagName += tagList.get(i) + ", ";
                    }
                }
            }

            condoData.put("strAccommId", strAccommId);
            condoData.put("API_FLAG", "ONDA");
            condoData.put("strCondoName", strCondoName);
            condoData.put("strConZip", strConZip);
            condoData.put("strConAddr1", strConAddr1);
            condoData.put("strConAddr2", strConAddr2);
            condoData.put("strConTel", strConTel);
            condoData.put("strConFax", strConFax);
            condoData.put("strConGekNum", "");
            condoData.put("strConFlag", "");
            condoData.put("strLocation", strLocation);
            condoData.put("strHomepage", "");
            condoData.put("strTimeIn", strTimeIn);
            condoData.put("strTimeOut", strTimeOut);
            condoData.put("strConDisplay", strConDisplay);
            condoData.put("strMapX", strMapX);
            condoData.put("strMapY", strMapY);
            condoData.put("strMobileWarning", strMobileWarning);
            condoData.put("strCity", strCity);
            condoData.put("strNation", strNation);
            condoData.put("strConDesc", strConDesc);
            condoData.put("strConAround", "");
            condoData.put("strConSookbak", "");
            condoData.put("strTagName", strTagName);

            // 이미지------------------------------------------------------------------------------------------------
            // test_CONTENTS_PHOTO, test_CONDO_PHOTO 테이블에 INSERT
            JSONArray images = (JSONArray) accommDetailJson.get("images");

            String strImgData = "";
            for(int i=0; i<images.size(); i++){
                JSONObject image = (JSONObject) images.get(i);

                String str250Img = image.get("250px").toString();
                String str500Img = image.get("500px").toString();
                String str1000Img = image.get("1000px").toString();

                if(str250Img != null){
                    strImgData += accommPhotoContentsReg(str250Img, 250, strAccommId, strCondoName) + "{{|}}";
                }

                if(str500Img != null){
                    strImgData += accommPhotoContentsReg(str500Img, 500, strAccommId, strCondoName) + "{{|}}";
                }

                if(str1000Img != null){
                    strImgData += accommPhotoContentsReg(str1000Img, 1000, strAccommId, strCondoName) + "{{|}}";
                }
            }

            strImgData = strImgData.substring(0, strImgData.length()-5);

            condoData.put("strImgData", strImgData);

            // 환불 취소규정 ----------------------------------------------------------------------------------------
            // tbl_cancel_info_row 테이블에 INSERT
            JSONObject refunds = (JSONObject) accommDetailJson.get("property_refunds");
            String cnFlag = "ps";

            String strPenaltyData = "";
            for(int i=0; i<2; i++){
                String strCnFlag = cnFlag;
                for(int j=0; j<7; j++){
                    int intCnDcnt = j;
                    int intCnPer = 100 - (Integer.parseInt(refunds.get(j + "d").toString()));

                    strPenaltyData += strCnFlag + "|^|" + intCnDcnt + "|^|" + intCnPer + "|^|" + "{{|}}";
                }
                cnFlag = "of";
            }
            strPenaltyData = strPenaltyData.substring(0, strPenaltyData.length()-5);

            condoData.put("strPenaltyData", strPenaltyData);

        }catch (Exception e){
            e.printStackTrace();
        }

        return condoData;
    }
    public void updateRoomType(String propertyId, String roomTypeId){
//        Map<String, Object> totalData = setRoomTypeData(propertyId, roomTypeId);

//        String result = accomodationMapper.(totalData);
    }

    public String setRoomNRatePlanData(String propertyId, String roomTypeId, int intAdminCount){
        String strRoomNRatePlanData = "";
        try{
            JSONObject roomDetailJson = getRoomTypeDetail(propertyId, roomTypeId);
            System.out.println("roomTypeDetailJson : " + gson.toJson(roomDetailJson));

            JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");

            String strRoomTypeStatus = roomDetailJson.get("status").toString();

            strRoomNRatePlanData += roomDetailJson.get("name").toString() + "|^|" +
                    capacity.get("standard").toString() + "|^|" + capacity.get("max").toString() + "|^|" +
                    roomTypeId + "|^|" + intAdminCount + "|^|" +
                    getStatusYn(strRoomTypeStatus) + "|^|" + roomDetailJson.get("description").toString() + "|^|null|^|";

            // ratePlan-------------------------------------------------------------------------------------------------
            JSONObject bedTypeJson = (JSONObject) roomDetailJson.get("bedtype");

            String strBedType = "";

            String single = "싱글";
            String super_single = "슈퍼싱글";
            String double_bed = "더블";
            String queen = "퀸";
            String king = "킹";
            String sofa = "소파베드";
            String air = "에어베드";

            int single_cnt = Integer.parseInt(bedTypeJson.get("single_beds").toString());
            int super_single_cnt = Integer.parseInt(bedTypeJson.get("super_single_beds").toString());
            int double_cnt = Integer.parseInt(bedTypeJson.get("double_beds").toString());
            int queen_cnt = Integer.parseInt(bedTypeJson.get("queen_beds").toString());
            int king_cnt = Integer.parseInt(bedTypeJson.get("king_beds").toString());
            int sofa_cnt = Integer.parseInt(bedTypeJson.get("sofa_beds").toString());
            int air_cnt = Integer.parseInt(bedTypeJson.get("air_beds").toString());

            if(single_cnt > 0){
                for(int l=0; l<single_cnt; l++){
                    if(single_cnt == 1){
                        strBedType += single + " ";
                        break;
                    }else if(single_cnt == 3){
                        strBedType += "트리플" + " ";
                        break;
                    }else{
                        strBedType += single + " ";
                    }
                }
            }else if(super_single_cnt > 0){
                for(int l=0; l<super_single_cnt; l++){
                    strBedType += super_single + " ";
                }
            }else if(double_cnt > 0){
                for(int l=0; l<double_cnt; l++){
                    strBedType += double_bed + " ";
                }
            }else if(queen_cnt > 0){
                for(int l=0; l<queen_cnt; l++){
                    strBedType += queen + " ";
                }
            }else if(king_cnt > 0){
                for(int l=0; l<king_cnt; l++){
                    strBedType += king + " ";
                }
            }else if(sofa_cnt > 0){
                for(int l=0; l<sofa_cnt; l++){
                    strBedType += sofa + " ";
                }
            }else if(air_cnt > 0){
                for(int l=0; l<air_cnt; l++){
                    strBedType += air + " ";
                }
            }

            List<JSONObject> ratePlanList = getRatePlanList(propertyId, roomTypeId);
            String strRatePlanDatas = "";
            for(JSONObject ratePlan : ratePlanList){
                String ratePlanId = ratePlan.get("id").toString();

                JSONObject ratePlanDtlJson = getRatePlanDetail(propertyId, roomTypeId, ratePlanId);

                JSONObject mealJson = (JSONObject) ratePlanDtlJson.get("meal");
                boolean breakfastYn = (boolean) mealJson.get("breakfast");
                String strBreakFastYn = "";
                if(breakfastYn){
                    strBreakFastYn = "Y";
                }else{
                    strBreakFastYn = "N";
                }

                strRatePlanDatas += ratePlanId + "|~|" + ratePlanDtlJson.get("name").toString() + "|~|" + strBedType + "|~|"
                        + strBreakFastYn + "|~|" + 0 + "|~|" + 0 + "{{~}}";
            }
            strRatePlanDatas = strRatePlanDatas.substring(0, strRatePlanDatas.length()-5);

            strRoomNRatePlanData += strRatePlanDatas + "{{|}}";

        }catch (Exception e){
            e.printStackTrace();
        }
        return strRoomNRatePlanData;
    }

}
