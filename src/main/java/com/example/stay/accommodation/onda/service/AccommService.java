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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccommService {

    @Autowired
    private AccomodationMapper accomodationMapper;

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
                System.out.println("responseBody : " + responseBody);

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
                System.out.println("responseBody : " + responseBody);

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
                System.out.println("responseBody : " + responseBody);

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

    public List<JSONObject> getPackageList(String property_id, String roomtype_id){
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
                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                JSONArray packageArray = (JSONArray) responseJson.get("rateplans");

                for(int i=0; i<packageArray.size(); i++){
                    JSONObject jsonObject = (JSONObject) packageArray.get(i);
                    packageList.add(jsonObject);
                }

//                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
//
//                // dto에 담기
//                JSONArray rateplans = (JSONArray) responseJson.get("rateplans");
//                for(int i=0; i<rateplans.size(); i++){
//                    JSONObject jsonObject = (JSONObject) rateplans.get(i);
////                    String id = jsonObject.get("id").toString();
//
//                }


            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("[onda API] 특정 객실의 전체 패키지 목록 가져오기 실패");
        }
        return packageList;
    }

    public JSONObject getPackageDetail(String property_id, String roomtype_id, String rateplan_id){
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
                System.out.println("responseBody : " + responseBody);

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

    // onda 최초 등록 시 사용 -> 추후 삭제? 아님 걍 다 주석처리해버려
//    public void firstAccommRegist(String path){
//        List<String> conIdList = new ArrayList<>();
//        List<String> accommIdList = new ArrayList<>();
//        List<String> toconIdxList = new ArrayList<>();
//
//        // 전체 숙소 id, 해당 id별 숙소 상세정보 불러오기
//        List<JSONObject> accommList = getAccommListApi(path);
//
//        List<String> propertyIdList = new ArrayList<>();
//        // 숙소 정보 INSERT
//        try{
//            for(JSONObject list : accommList){
//                String propertyId = list.get("id").toString();
//                propertyIdList.add(propertyId);
//                System.out.println("propertyId : " + propertyId);
//
//                JSONObject accommDetailJson = getAccommDetailApi(propertyId);
//                System.out.println("accommDetailJson : " + accommDetailJson);
//
//                // dto에 담기
//                CondoDto accommDetail = new CondoDto();
//
//                String strAccommId = accommDetailJson.get("id").toString();
//                accommDetail.setStrAccommId(strAccommId);
//                accommIdList.add(strAccommId);
//                String strAccommName = accommDetailJson.get("name").toString();
//                accommDetail.setStrAcmName(strAccommName);
//
//                String strAccommStatus = accommDetailJson.get("status").toString();
//                accommDetail.setStrConDisplay(getStatusYn(strAccommStatus));
//
//                JSONObject address = (JSONObject) accommDetailJson.get("address");
//                accommDetail.setStrNation(address.get("country_code").toString());
//                accommDetail.setStrLoc(address.get("region").toString());
//                accommDetail.setStrCity(address.get("city").toString());
//                /**
//                 * db addr관련 컬럼 정리 필요
//                 */
//                accommDetail.setStrAddress(address.get("address1").toString());
//                accommDetail.setStrConAddrNew(address.get("address2").toString());
//                accommDetail.setStrZipCode(address.get("postal_code").toString());
//                JSONObject location = (JSONObject) address.get("location");
//                accommDetail.setDecLat(location.get("latitude").toString());
//                accommDetail.setDecLng(location.get("longitude").toString());
//
//                accommDetail.setStrConTel(accommDetailJson.get("phone").toString());
//                accommDetail.setStrConFax(accommDetailJson.get("fax").toString());
//                accommDetail.setStrTimeIn(accommDetailJson.get("checkin").toString());
//                accommDetail.setStrTimeOut(accommDetailJson.get("checkout").toString());
//
//                JSONObject descriptions = (JSONObject) accommDetailJson.get("descriptions");
//                accommDetail.setStrSummary(descriptions.get("property").toString());
//                accommDetail.setStrUsageNotice(descriptions.get("reservation").toString());
//
//                /**
//                 * classifications
//                 * 현재 condo테이블의 con_flag -> int
//                 * 값이 호텔, 티켓 등 여러개일 가능성도 있음
//                 * -> 값을 어떻게 넣아햐는지
//                 */
//                JSONArray classifications = (JSONArray) accommDetailJson.get("classifications");
////                List<String> classficationList = new ArrayList<>();
////                for(int i=0; i<classifications.size(); i++){
//////                    String code = accomodationMapper.selectCode(classifications.get(i).toString());
////                    classficationList.add(accomodationMapper.selectCode(classifications.get(i).toString());
////                }
////                for(classficationList){
////                    accommDetail.setStrFlag(code);
////                }
//
//                // 어차피 다 태그니까 한 번에 몰아넣자 tags 컬럼에
//                JSONObject tags = (JSONObject) accommDetailJson.get("tags");
//                JSONArray properties = (JSONArray) tags.get("properties") ;
//                JSONArray facilities = (JSONArray) tags.get("facilities");
//                JSONArray services = (JSONArray) tags.get("services");
//                JSONArray attractions = (JSONArray) tags.get("attractions");
//
//                List<String> tagList = new ArrayList<>();
//                if(properties != null){
//                    for(Object p : properties){
//                        tagList.add(p.toString());
//                    }
//                }
//                if(facilities != null){
//                    for(Object f : facilities){
//                        tagList.add(f.toString());
//                    }
//                }
//                if(services != null){
//                    for(Object s : services){
//                        tagList.add(s.toString());
//                    }
//                }
//                if(attractions != null){
//                    for(Object a : attractions){
//                        tagList.add(a.toString());
//                    }
//                }
//
//                if(tagList != null){
//                    String strTags = "";
//                    for(int i=0; i<tagList.size(); i++){
//                        if(i == (tagList.size()-1)){
//                            strTags += tagList.get(i);
//                        }else{
//                            strTags += tagList.get(i) + ", ";
//                        }
//                    }
//                    accommDetail.setStrTagName(strTags);
//                }
//
//                accommDetail.setStrApiFlag("ONDA");
//
//                String strConId = accomodationMapper.accommRegist(accommDetail);
//                conIdList.add(strConId);
//                System.out.println("con_id : " + strConId);
//
//                // 이미지------------------------------------------------------------------------------------------------
//                // test_CONTENTS_PHOTO, test_CONDO_PHOTO 테이블에 INSERT
//                JSONArray images = (JSONArray) accommDetailJson.get("images");
//
//                for(int i=0; i<images.size(); i++){
//                    JSONObject image = (JSONObject) images.get(i);
//
//                    String str250Img = image.get("250px").toString();
//                    String str500Img = image.get("500px").toString();
//                    String str1000Img = image.get("1000px").toString();
//
//                    if(str250Img != null){
//                        accommPhotoContentsReg(str250Img, 250, strAccommId, strAccommName);
//                    }
//
//                    if(str500Img != null){
//                        accommPhotoContentsReg(str500Img, 500, strAccommId, strAccommName);
//                    }
//
//                    if(str1000Img != null){
//                        accommPhotoContentsReg(str1000Img, 1000, strAccommId, strAccommName);
//                    }
//
//                }
//
//                // 환불 취소규정 ----------------------------------------------------------------------------------------
//                // tbl_cancel_info_row 테이블에 INSERT
//                JSONObject refunds = (JSONObject) accommDetailJson.get("property_refunds");
//                CancelInfoDto cancelInfoDto = new CancelInfoDto();
//                cancelInfoDto.setStrCname(strAccommName);
//                cancelInfoDto.setIntCid(Integer.parseInt(strConId));
//
//                String strCnFlag = "ps";
//
//                for(int i=0; i<2; i++){
//                    System.out.println("i : " + i);
//                    System.out.println("strCnFlag : " + strCnFlag);
//                    cancelInfoDto.setStrCnFlag(strCnFlag);
//                    for(int j=0; j<7; j++){
//                        cancelInfoDto.setIntCnDcnt(j);
//                        cancelInfoDto.setIntCnPer(100 - (Integer.parseInt(refunds.get(j + "d").toString())));
//                        accomodationMapper.cancelInfoReg(cancelInfoDto);
//                    }
//                    strCnFlag = "of";
//                }
//
////                if(cancelInfoRegResult == 0){
////                    System.out.println("INSERT TBL_CANCEL_INFO_ROW FAIL");
////                }
//            }
//            System.out.println("시설정보, 이미지, 취소규정 INSERT SUCCESS");
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("시설정보, 이미지, 취소규정 INSERT FAIL");
//        }
//
//        // 룸타입정보 INSERT
//        List<String> roomTypeIdList = new ArrayList<>();
//        List<JSONObject> bedtypeList = new ArrayList<>();
//        List<Integer> standPeopleCntList = new ArrayList<>();
//        List<Integer> maxPeopleCntList = new ArrayList<>();
//        try{
//            for(int i=0; i<conIdList.size(); i++){
//                String strPropertyId = propertyIdList.get(i);
//                List<JSONObject> roomTypeList = getRoomTypeListApi(strPropertyId);
//
//                int intAdminCount = 1;
//                for (JSONObject list : roomTypeList) {
//                    String roomTypeId = list.get("id").toString();
//                    roomTypeIdList.add(roomTypeId);
//                    System.out.println("roomTypeId : " + roomTypeId);
//
//                    JSONObject roomDetailJson = getRoomTypeDetail(strPropertyId, roomTypeId);
//                    System.out.println("roomTypeDetailJson : " + roomDetailJson);
//
//                    // dto에 담기
//                    ToconDto roomDetail = new ToconDto();
//
//                    roomDetail.setRoomTypeId(roomTypeId);
//                    roomDetail.setConId(conIdList.get(i));
//
//                    roomDetail.setTocode(roomDetailJson.get("name").toString());
//
//                    JSONObject capacity = (JSONObject) roomDetailJson.get("capacity");
//                    roomDetail.setStandpeopleCnt(Integer.parseInt(capacity.get("standard").toString()));
//                    standPeopleCntList.add(Integer.parseInt(capacity.get("standard").toString()));
//                    roomDetail.setMaxpeopleCnt(Integer.parseInt(capacity.get("max").toString()));
//                    maxPeopleCntList.add(Integer.parseInt(capacity.get("max").toString()));
//
//                    // ONDA는 별도의 내부 순서를 주지 않으므로 불러오는 순서대로 임의로 줌
//                    // con_id로 먼저 등록되어있는 admin_count확인 후 추가
//                    int roomAdminCnt = accomodationMapper.getRoomAdminCnt(conIdList.get(i));
//                    if(roomAdminCnt > 0){
//                        roomDetail.setAdmin_count((roomAdminCnt+1));
//                    }else{
//                        roomDetail.setAdmin_count(intAdminCount);
//                        intAdminCount += 1;
//                    }
//
//                    String strRoomTypeStatus = roomDetailJson.get("status").toString();
//                    roomDetail.setStrIngYN(getStatusYn(strRoomTypeStatus));
//
//                    roomDetail.setTocodeText(roomDetailJson.get("description").toString());
//
//                    bedtypeList.add((JSONObject) roomDetailJson.get("bedtype"));
//
//                    String strToconIdx = accomodationMapper.roomTypeRegist(roomDetail);
//                    toconIdxList.add(strToconIdx);
//                    System.out.println("tocon_idx : " + strToconIdx);
//
//                }
//            }
//            System.out.println("룸타입 정보 INSERT SUCCESS");
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("룸타입 정보 INSERT FAIL");
//        }
//
//        // 패키지(RatePlan) INSERT
//        List<String> ratePlanIdList = new ArrayList<>();
//        try{
//            for(int i=0; i<conIdList.size(); i++){
//                String strPropertyId = propertyIdList.get(i);
//                for(int j=0; j<roomTypeIdList.size(); j++){
//                    String roomTypeId = roomTypeIdList.get(j);
//                    List<JSONObject> ratePlanList = getPackageList(strPropertyId, roomTypeId);
//
//                    for(int k=0; k<ratePlanList.size(); k++){
//                        String ratePlanId = ratePlanList.get(k).get("id").toString();
//                        ratePlanIdList.add(ratePlanId);
//                        System.out.println("ratePlanId : " + ratePlanId);
//
//                        JSONObject ratePlanDtlJson = getPackageDetail(strPropertyId, roomTypeId, ratePlanId);
//
//                        RatePlanDto ratePlanDetail = new RatePlanDto();
//
//                        ratePlanDetail.setIntConId(Integer.parseInt(conIdList.get(i)));
//                        ratePlanDetail.setStrAccommId(accommIdList.get(i));
//                        ratePlanDetail.setIntToconIdx(Integer.parseInt(toconIdxList.get(j)));
//                        ratePlanDetail.setStrRoomTypeId(roomTypeId);
//                        ratePlanDetail.setStrRatePlanId(ratePlanId);
//                        ratePlanDetail.setStrRatePlanName(ratePlanDtlJson.get("name").toString());
//
//                        JSONObject bedTypeJson = bedtypeList.get(j);
//
//                        String strBedType = "";
//
//                        String single = "싱글";
//                        String super_single = "슈퍼싱글";
//                        String double_bed = "더블";
//                        String queen = "퀸";
//                        String king = "킹";
//                        String sofa = "소파베드";
//                        String air = "에어베드";
//
//                        int single_cnt = Integer.parseInt(bedTypeJson.get("single_beds").toString());
//                        int super_single_cnt = Integer.parseInt(bedTypeJson.get("super_single_beds").toString());
//                        int double_cnt = Integer.parseInt(bedTypeJson.get("double_beds").toString());
//                        int queen_cnt = Integer.parseInt(bedTypeJson.get("queen_beds").toString());
//                        int king_cnt = Integer.parseInt(bedTypeJson.get("king_beds").toString());
//                        int sofa_cnt = Integer.parseInt(bedTypeJson.get("sofa_beds").toString());
//                        int air_cnt = Integer.parseInt(bedTypeJson.get("air_beds").toString());
//
//                        if(single_cnt > 0){
//                            for(int l=0; l<single_cnt; l++){
//                                if(single_cnt == 1){
//                                    strBedType += single + " ";
//                                    break;
//                                }else if(single_cnt == 3){
//                                    strBedType += "트리플" + " ";
//                                    break;
//                                }else{
//                                    strBedType += single + " ";
//                                }
//                            }
//                        }else if(super_single_cnt > 0){
//                            for(int l=0; l<super_single_cnt; l++){
//                                strBedType += super_single + " ";
//                            }
//                        }else if(double_cnt > 0){
//                            for(int l=0; l<double_cnt; l++){
//                                strBedType += double_bed + " ";
//                            }
//                        }else if(queen_cnt > 0){
//                            for(int l=0; l<queen_cnt; l++){
//                                strBedType += queen + " ";
//                            }
//                        }else if(king_cnt > 0){
//                            for(int l=0; l<king_cnt; l++){
//                                strBedType += king + " ";
//                            }
//                        }else if(sofa_cnt > 0){
//                            for(int l=0; l<sofa_cnt; l++){
//                                strBedType += sofa + " ";
//                            }
//                        }else if(air_cnt > 0){
//                            for(int l=0; l<air_cnt; l++){
//                                strBedType += air + " ";
//                            }
//                        }
//
//                        ratePlanDetail.setBedTypeCode(strBedType);
//
//                        JSONObject mealJson = (JSONObject) ratePlanDtlJson.get("meal");
//                        boolean breakfastYn = (boolean) mealJson.get("breakfast");
//                        if(breakfastYn){
//                            ratePlanDetail.setStrMealCode("Y");
//                        }else{
//                            ratePlanDetail.setStrMealCode("N");
//                        }
//
//                        ratePlanDetail.setIntMinPersons(standPeopleCntList.get(j));
//                        ratePlanDetail.setIntMaxPersons(maxPeopleCntList.get(j));
//
//                        String strRatePlanIdx = accomodationMapper.ratePlanRegist(ratePlanDetail);
//                        System.out.println("strRatePlanIdx : " + strRatePlanIdx);
//                    }
//                }
//            }
//            System.out.println("패키지(RatePlan) 정보 INSERT SUCCESS");
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("패키지(RatePlan) 정보 INSERT FAIL");
//        }
//
//        // 요금 및 재고(Inventory) INSERT
////        try{
////            for(String ratePlanId : ratePlanIdList){
////
////            }
////
////
////
////            System.out.println("요금 및 재고(Inventory) INSERT SUCCESS");
////        }catch (Exception e){
////            e.printStackTrace();
////            System.out.println("요금 및 재고(Inventory) INSERT FAIL");
////        }
//
//    }

    public List<JSONObject> getInventories(String rateplan_id, String from, String to){
        List<JSONObject> inventoryList = new ArrayList<>();
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

                for(int i=0; i<inventoryArr.size(); i++){
                    JSONObject jsonObject = (JSONObject) inventoryArr.get(i);
                    inventoryList.add(jsonObject);
                }

                for(Object inventories : inventoryList){
                    JSONObject inventoryJson = (JSONObject) inventories;

                    StockDto inventory = new StockDto();

                    // rateplan_id에 해당하는 정보 DB에서 불러오기
                    StockDto idxDto = accomodationMapper.getIdxsByRatePlanId(rateplan_id);

                    if(idxDto != null){

                        inventory.setIntConId(idxDto.getIntConId());
                        inventory.setIntToconIdx(idxDto.getIntToconIdx());
                        inventory.setIntStock(Integer.parseInt(inventoryJson.get("vacancy").toString()));
                        inventory.setCheckInDate(inventoryJson.get("date").toString());
                        inventory.setIntRatePlanIdx(idxDto.getIntRatePlanIdx());
                        inventory.setIntBasicPrice(Integer.parseInt(inventoryJson.get("basic_price").toString()));
                        inventory.setIntSalePrice(Integer.parseInt(inventoryJson.get("sale_price").toString()));

                        JSONObject lengthOfStay = (JSONObject) inventoryJson.get("length_of_stay");
                        inventory.setIntMinStay(Integer.parseInt(lengthOfStay.get("min").toString()));
                        inventory.setIntMinStay(Integer.parseInt(lengthOfStay.get("max").toString()));

                        String strGoodsIdx = accomodationMapper.goodsRegist(inventory);
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
        return  inventoryList;
    }

    // CONTENTS_PHOTO, CONDO_PHOTO에 INSERT
    public String accommPhotoContentsReg(String strImage, int intSize, String strAccommId, String accommName){
        String strAccommPhotoContent = "";
        try{
//            ContentsPhotoDto contentsPhotoDto = new ContentsPhotoDto();
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
//                    contentsPhotoDto.setStrFileName(strFileName);

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

//            contentsPhotoDto.setStrFilePath("/onda/" + strAccommId + "/" + intSize + "/");
            String strFilePath = "/onda/" + strAccommId + "/" + intSize + "/";
//            contentsPhotoDto.setIntCreatedSID(intCreatedSID);
//            contentsPhotoDto.setIntModifiedSID(intModifiedSID);
//            contentsPhotoDto.setStrSubject(strAccommName);
            String strAccommName = accommName;
//            contentsPhotoDto.setStrCid(strConId);


//            String insertResult = accomodationMapper.accommPhotoContentsReg(contentsPhotoDto);
//                if(insertResult == 0){
//                    System.out.println("INSERT CONTENTS_PHOTO FAIL");
//                }

            strAccommPhotoContent = strAccommName + "|^|" + strFilePath + "|^|" + strFileName + "|^|" + intCreatedSID + "|^|"
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

    // 시설 등록(시설+이미지+취소규정)
    public void accommRegist(String propertyId){
        try{
            // 해당 property_id로 상세정보 조회 API 호출
            // 없으면 INSERT, 있으면 UPDATE // -> 프로시저()로 만들어져있음 -> 여기서 실행시키면 되겠네

            JSONObject accommDetailJson = getAccommDetailApi(propertyId);
            System.out.println("accommDetailJson : " + accommDetailJson);

            // dto에 담기
//            CondoDto accommDetail = new CondoDto();

            String strAccommId = accommDetailJson.get("id").toString();
//            accommDetail.setStrAccommId(strAccommId);
            String strAccommName = accommDetailJson.get("name").toString();
//            accommDetail.setStrAcmName(strAccommName);

            String strConDisplay = getStatusYn(accommDetailJson.get("status").toString());
//            accommDetail.setStrConDisplay(getStatusYn(strAccommStatus));

            JSONObject address = (JSONObject) accommDetailJson.get("address");
//            accommDetail.setStrNation(address.get("country_code").toString());
            String strNation = address.get("country_code").toString();
//            accommDetail.setStrLoc(address.get("region").toString());
            String strLoc = address.get("region").toString();
//            accommDetail.setStrCity(address.get("city").toString());
            String strCity = address.get("city").toString();
            /**
             * db addr관련 컬럼 정리 필요
             */
//            accommDetail.setStrAddress(address.get("address1").toString());
            String strAddress = address.get("address1").toString();
//            accommDetail.setStrConAddrNew(address.get("address2").toString());
            String strConAddrNew = address.get("address2").toString();
//            accommDetail.setStrZipCode(address.get("postal_code").toString());
            String strZipcode = address.get("postal_code").toString();
            JSONObject location = (JSONObject) address.get("location");
//            accommDetail.setDecLat(location.get("latitude").toString());
            String strDecLat = location.get("latitude").toString();
//            accommDetail.setDecLng(location.get("longitude").toString());
            String strDecLng = location.get("longitude").toString();

//            accommDetail.setStrConTel(accommDetailJson.get("phone").toString());
            String strConTel = accommDetailJson.get("phone").toString();
//            accommDetail.setStrConFax(accommDetailJson.get("fax").toString());
            String strConFax = accommDetailJson.get("fax").toString();
//            accommDetail.setStrTimeIn(accommDetailJson.get("checkin").toString());
            String strTimeIn = accommDetailJson.get("checkin").toString();
//            accommDetail.setStrTimeOut(accommDetailJson.get("checkout").toString());
            String strTimeOut = accommDetailJson.get("checkout").toString();

            JSONObject descriptions = (JSONObject) accommDetailJson.get("descriptions");
//            accommDetail.setStrSummary(descriptions.get("property").toString());
            String strSummary = descriptions.get("property").toString();
//            accommDetail.setStrUsageNotice(descriptions.get("reservation").toString());
            String strUsageNotice = descriptions.get("reservation").toString();

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

            String strTags = "";
            if(tagList != null){
                for(int i=0; i<tagList.size(); i++){
                    if(i == (tagList.size()-1)){
                        strTags += tagList.get(i);
                    }else{
                        strTags += tagList.get(i) + ", ";
                    }
                }
//                accommDetail.setStrTagName(strTags);
            }

//            accommDetail.setStrApiFlag("ONDA");
            String strApiFlag = "ONDA";

//            String strConId = accomodationMapper.accommRegist(accommDetail);
//            System.out.println("con_id : " + strConId);

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
                    strImgData += accommPhotoContentsReg(str250Img, 250, strAccommId, strAccommName) + "{{|}}";
                }

                if(str500Img != null){
                    strImgData += accommPhotoContentsReg(str500Img, 500, strAccommId, strAccommName) + "{{|}}";
                }

                if(str1000Img != null){
                    strImgData += accommPhotoContentsReg(str1000Img, 1000, strAccommId, strAccommName) + "{{|}}";
                }
            }


            strImgData = strImgData.substring(0, strImgData.length()-5);

            // 환불 취소규정 ----------------------------------------------------------------------------------------
            // tbl_cancel_info_row 테이블에 INSERT
            JSONObject refunds = (JSONObject) accommDetailJson.get("property_refunds");
            CancelInfoDto cancelInfoDto = new CancelInfoDto();
//            cancelInfoDto.setStrCname(strAccommName);
            String strCname = strAccommName;
//            cancelInfoDto.setIntCid(Integer.parseInt(strConId));

            String cnFlag = "ps";

            String strPenaltyData = "";
            for(int i=0; i<2; i++){
//                cancelInfoDto.setStrCnFlag(cnFlag);
                String strCnFlag = cnFlag;
                for(int j=0; j<7; j++){
//                    cancelInfoDto.setIntCnDcnt(j);
                    int intCnDcnt = j;
//                    cancelInfoDto.setIntCnPer(100 - (Integer.parseInt(refunds.get(j + "d").toString())));
                    int intCnPer = 100 - (Integer.parseInt(refunds.get(j + "d").toString()));

                    strPenaltyData += strCnFlag + "|^|" + intCnDcnt + "|^|" + intCnPer + "|^|" + "{{|}}";
//                    accomodationMapper.cancelInfoReg(cancelInfoDto);
                }
                cnFlag = "of";
            }
            strPenaltyData = strPenaltyData.substring(0, strPenaltyData.length()-5);
//                if(cancelInfoRegResult == 0){
//                    System.out.println("INSERT TBL_CANCEL_INFO_ROW FAIL");
//                }


            String result = accomodationMapper.accommRegistTotal(strAccommId, strApiFlag, strAccommName, strZipcode
                    , strAddress, strConAddrNew, strConTel, strConFax, strLoc, strTimeIn, strTimeOut, strConDisplay
                    , strDecLng, strDecLat,strUsageNotice, strCity, strNation, strSummary, strTags, strImgData, strPenaltyData);

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("시설 등록 실패");
        }
    }



}
