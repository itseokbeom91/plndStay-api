package com.example.stay.openMarket.coupang.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CancelRulesDto;
import com.example.stay.openMarket.common.dto.RoomTypeDto;
import com.example.stay.openMarket.common.dto.StockDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.coupang.Api.CoupangApi;
import com.example.stay.openMarket.coupang.mapper.CoupangMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CpAccommService {

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CoupangMapper coupangMapper;

    @Autowired
    private CoupangApi coupangApi;

    @Autowired
    private CommonService commonService;

    CommonFunction commonFunction = new CommonFunction();

    private static int intOmkIdx = 12;

    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    // 숙박상품 생성
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            // 숙박상품 생성에 필요한 데이터 가져오기
            // =============================
            // 시설 정보
            // =============================
            AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);
            if(accommDto != null){
                JSONObject accommJson = new JSONObject();

                String strType = accommDto.getStrType();
                if(strType.equals("H") || strType.equals("R")){
                    strType = "HOTEL";
                }else if(strType.equals("C")){
                    strType = "RESORT";
                }else if(strType.equals("P")){
                    strType = "PENSION";
                }else if(strType.equals("GH")){
                    strType = "GUEST_HOUSE";
                }else if(strType.equals("CP")){
                    strType = "CAMPING";
                }else if(strType.equals("M")){
                    strType = "MOTEL";
                }

                accommJson.put("productDetailType", strType); // 시설 유형
                accommJson.put("travelType", "DOMESTIC"); // 국내 / 해외
                accommJson.put("sellerProductId", accommDto.getIntAID()); // 상품ID
                accommJson.put("nation", "KR"); // 시설 국가
                accommJson.put("name", accommDto.getStrSubject()); // 시설 이름
                accommJson.put("introduction", accommDto.getStrDescription()); // 시설 소개
                accommJson.put("regionKeyword", accommDto.getStrRegionKeyword()); // 지역 키워드

                // 태그 데이터 세팅
                List<String> strTagList = new ArrayList<>();
                if(accommDto.getStrKeywords() != null){
                    String strKeywords = accommDto.getStrKeywords();
                    strKeywords.replaceAll("\\s", "");
                    String[] keywordArr = strKeywords.split(",");

                    for(String keyword : keywordArr){
                        strTagList.add(keyword);
                    }
                }
                if(accommDto.getStrFac() != null){
                    String strFac = accommDto.getStrFac();
                    String[] facArr = strFac.split(",");

                    for(String fac : facArr){
                        strTagList.add(fac);
                    }
                }
                if(accommDto.getStrAround() != null){
                    String strAround = accommDto.getStrAround();
                    String[] aroundArr = strAround.split(",");

                    for(String around : aroundArr){
                        strTagList.add(around);
                    }
                }

                accommJson.put("searchTags", strTagList);

                // image
                List<JSONObject> imageList = new ArrayList<>();
                if(accommDto.getStrACMPhotos() != null){
                    String strAcmPhotos = accommDto.getStrACMPhotos();
                    String[] photoArr = strAcmPhotos.split("\\|");

                    for(int i=0; i< photoArr.length; i++){
                        JSONObject images = new JSONObject();
                        // TODO : 추후 이미지 저장 경로 정해지면 추가 할 것
                        images.put("sellerUrl", "https://condo24.com/" + photoArr[i]);
                        images.put("seq", i+1);
                        if(i==0){
                            images.put("representative", true);
                        }

                        imageList.add(images);
                    }
                }

                accommJson.put("images", imageList);

                accommJson.put("cancelType", "APPROVAL");

                String cancelNotice = getCancelInfo(intAID);
                accommJson.put("cancelPolicyNotice", cancelNotice);

                String usageNotice = accommDto.getStrAcmNotice();
                if(usageNotice == null){
                    usageNotice = "유의사항";
                }
                accommJson.put("usageNotice", usageNotice);

                JSONObject address = new JSONObject();
                address.put("latitude", accommDto.getDecLat());
                address.put("longitude", accommDto.getDecLon());
                address.put("roadNameAddress", accommDto.getStrAddr1());
                address.put("nation", "KR");
                address.put("city", accommDto.getStrRegionKeyword());
                address.put("district", accommDto.getStrDistrict2());
                address.put("zipCode", accommDto.getStrZipCode());

                accommJson.put("address", address);

                accommJson.put("representativePhone", "1588-0134");

                // =============================
                // 객실 정보
                // =============================
                List<JSONObject> roomsList = new ArrayList<>();
                List<RoomTypeDto> roomTypeDtoList = commonMapper.getRoomList(intAID, intOmkIdx);
                List<JSONObject> ratesList = new ArrayList<>();
                if(roomTypeDtoList != null){
                    for(RoomTypeDto r : roomTypeDtoList){
                        JSONObject rooms = new JSONObject();
                        rooms.put("sellerRoomId", r.getIntIdx());
                        rooms.put("name", r.getStrRmtypeName());
                        rooms.put("additionalInfo", r.getStrShortDesc());

                        // image
                        List<JSONObject> rmimagesList = new ArrayList<>();
                        if(r.getStrRmPhotos() != null){
                            String strAcmPhotos = r.getStrRmPhotos();
                            String[] photoArr = strAcmPhotos.split("\\|");

                            for(int i=0; i< photoArr.length; i++){
                                JSONObject rmimages = new JSONObject();
                                // TODO : 추후 이미지 저장 경로 정해지면 추가 할 것
                                rmimages.put("sellerUrl", "https://condo24.com/" + photoArr[i]);
                                rmimages.put("seq", i+1);

                                if(i==0){
                                    rmimages.put("representative", true);
                                }

                                rmimagesList.add(rmimages);
                            }
                        }
                        rooms.put("images", rmimagesList);

                        JSONObject occupancy = new JSONObject();
                        occupancy.put("standardOccupancy", r.getIntQuanStd());
                        occupancy.put("maximumOccupancy", r.getIntQuanMax());

                        rooms.put("occupancy", occupancy);

                        roomsList.add(rooms);


                        // =============================
                        // rate 정보
                        // =============================
                        JSONObject rates = new JSONObject();

                        List<Integer> intIdxList = new ArrayList<>();
                        intIdxList.add(r.getIntIdx());
                        rates.put("sellerRoomIds", intIdxList);

                        rates.put("sellerRateId", r.getIntIdx());

                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                        rates.put("saleStartedAt", sdf.format(timestamp));

                        // 취소 환불 규정
                        JSONObject cancelPolicy = new JSONObject();
                        // 쿠팡은 성수기/비수기 구분 없이 보내기 때문에 일단 비수기로 넣고, 추가로 notice에 string으로 성수기/비수기 규정 만들어서 보내기
                        cancelPolicy.put("notice", cancelNotice);

                        String strRefundYn = r.getStrRefundYn();
                        // 취소/환불정책 가져오기
                        List<CancelRulesDto> cancelDtoList = commonMapper.getCancelRuleList(intAID);
                        if(strRefundYn.equals("N")){ // 환불 불가
                            cancelPolicy.put("refundable", false);
                        }else{ // 환불 가능
                            List<JSONObject> refundList = new ArrayList<>();

                            for(CancelRulesDto c : cancelDtoList){
                                JSONObject refundJson = new JSONObject();
                                if(c.getStrFlag().equals("OOF")){
                                    refundJson.put("days", c.getIntDay());
                                    refundJson.put("refundRate", c.getIntPercent());

                                    refundList.add(refundJson);
                                }
                            }
                            cancelPolicy.put("refundRates", refundList);
                        }
                        rates.put("cancelPolicy", cancelPolicy);

                        // 추가요금
                        JSONObject extraGuestPolicy = new JSONObject();
                        if(r.getIntExtraA() != 0 || r.getIntExtraC() != 0){
                            extraGuestPolicy.put("surchargeAvailable", true);

                            List<JSONObject> surchargesList = new ArrayList<>();
                            JSONObject surcharges = new JSONObject();
                            if(r.getIntExtraA() != 0){
                                surcharges.put("ageType", "ADULT");
                                // TODO : 어린이 요금 기준 나이 확인 필요
                                surcharges.put("minAge", 2);
                                surcharges.put("surcharge", r.getIntExtraA());

                                surchargesList.add(surcharges);
                            }
                            if(r.getIntExtraC() != 0){
                                surcharges.put("ageType", "CHILD");
                                // TODO : 어린이 요금 기준 나이 확인 필요
                                surcharges.put("minAge", 2);
                                surcharges.put("surcharge", r.getIntExtraC());
                                surchargesList.add(surcharges);
                            }
                            rates.put("extraGuestPolicy", extraGuestPolicy);
                        }

                        // 체크인 체크아웃
                        JSONObject checkInOutPolicy = new JSONObject();
                        checkInOutPolicy.put("checkInStartTime", accommDto.getStrCheckIn());
                        checkInOutPolicy.put("checkOutEndTime", accommDto.getStrCheckOut());

                        rates.put("checkInOutPolicy", checkInOutPolicy);

                        ratesList.add(rates);
                    }

                    accommJson.put("rooms", roomsList);
                    accommJson.put("rates", ratesList);

                    // 판매 정지 상태로 테스트
                    accommJson.put("saleStatus", "SUSPENDED");

                    JSONArray lodgingCreateDtos = new JSONArray();
                    lodgingCreateDtos.add(accommJson);

                    JSONObject requestJson = new JSONObject();
                    requestJson.put("lodgingCreateDtos", lodgingCreateDtos);

                    System.out.println(requestJson);

//                    // API 호출
//                    JSONObject returnJson = coupangApi.coupangPostApi(gson.toJson(requestJson), "travel/lodgings");
//                    // 응답값 처리
//                    String returnCode = returnJson.get("code").toString();
//                    if(returnCode.equals("200")){
//                        JSONObject dataJson = (JSONObject) returnJson.get("data");
//                        JSONArray successArr = (JSONArray) dataJson.get("success");
//                        JSONArray failArr = (JSONArray) dataJson.get("fail");
//
//                        for(Object f : failArr) {
//                            JSONObject jsonObject = (JSONObject) f;
//                            if(jsonObject != null){
//                                String failReason = jsonObject.get("reason").toString();
//
//                                message = "상품 생성 실패";
//                                logWriter.add(failReason);
//                            }
//                            break;
//                        }
//
//                        for(Object s : successArr){
//                            JSONObject jsonObject = (JSONObject) s;
//                            if(jsonObject != null){
//                                String strPdtCode = jsonObject.get("travelProductId").toString();
//                                String strPdtSubject = jsonObject.get("name").toString();
//                                String strDetailInfo = commonService.getStrPdtDtlInfo(accommDto, intAID, intOmkIdx);
//
//                                String itemCodeDatas = "";
//                                JSONArray roomArr = (JSONArray) jsonObject.get("rooms");
//                                for(Object r : roomArr){
//                                    JSONObject roomJson = (JSONObject) r;
//                                    String strCpItemCode = roomJson.get("travelItemId").toString();
//                                    int intRmIdx = Integer.parseInt(roomJson.get("sellerRoomId").toString());
//
//                                    itemCodeDatas += intRmIdx + "|^|" + strCpItemCode + "{{|}}";
//                                }
//                                itemCodeDatas = itemCodeDatas.substring(0, itemCodeDatas.length()-5);
//
//                                String rateCodeDatas = "";
//                                JSONArray rateArr = (JSONArray) jsonObject.get("rates");
//                                for(Object r : rateArr){
//                                    JSONObject rateJson = (JSONObject) r;
//                                    String strCpRateCode = rateJson.get("rateId").toString();
//                                    int intRmIdx = Integer.parseInt(rateJson.get("sellerRateId").toString());
//
//                                    rateCodeDatas += intRmIdx + "|^|" + strCpRateCode + "{{|}}";
//                                }
//                                rateCodeDatas = rateCodeDatas.substring(0, rateCodeDatas.length()-5);
//
//                                String insertResult = coupangMapper.insertCpCodes(intAID, strPdtCode, strPdtSubject, strDetailInfo, itemCodeDatas, rateCodeDatas);
//
//                                if(insertResult.substring(insertResult.length()-4).equals("수정완료")){
//                                    message = "상품 등록 완료";
//                                }else{
//                                    message = "상품 등록 완료 / 쿠팡 코드 DB 등록 실패";
//                                }
//                            }
//                            break;
//                        }
//                    }else{
//                        message = "쿠팡 api 호출 실패";
//                        logWriter.add("code : " + returnCode);
//                        String returnMsg = returnJson.get("message").toString();
//                        logWriter.add(returnMsg);
//                    }
                }else{
                    message = "객실 정보가 존재하지 않습니다(쿠팡 연동여부 확인 필요)";
                }
            }else{
                message = "시설 정보가 존재하지 않습니다(쿠팡 연동여부 확인 필요)";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 생성 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 객실 생성/수정
    public String creUpdRoom(String dataType, int intRmIdx, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            RoomTypeDto roomTypeDto = commonMapper.getRmtpeInfo(intRmIdx, intOmkIdx);
            // strPdtCode, strCpItemCode 확인
            // -> strCpItemCode가 존재하면 업데이트, 없으면 생성
            String strPdtCode = roomTypeDto.getStrPdtCode();
            String strCpItemCode = roomTypeDto.getStrCpItemCode();

            if(strPdtCode != null){

                // =============================
                // 객실 정보
                // =============================

                JSONObject rooms = new JSONObject();
                rooms.put("sellerRoomId", intRmIdx);
                rooms.put("name", roomTypeDto.getStrRmtypeName());
                rooms.put("additionalInfo", roomTypeDto.getStrShortDesc());

                // image
                List<JSONObject> rmimagesList = new ArrayList<>();
                if(roomTypeDto.getStrRmPhotos() != null){
                    String strAcmPhotos = roomTypeDto.getStrRmPhotos();
                    String[] photoArr = strAcmPhotos.split("\\|");

                    for(int i=0; i< photoArr.length; i++){
                        JSONObject rmimages = new JSONObject();
                        // TODO : 추후 이미지 저장 경로 정해지면 추가 할 것
                        rmimages.put("sellerUrl", "https://condo24.com/" + photoArr[i]);
                        rmimages.put("seq", i+1);

                        if(i==0){
                            rmimages.put("representative", true);
                        }

                        rmimagesList.add(rmimages);
                    }
                }
                rooms.put("images", rmimagesList);

                JSONObject occupancy = new JSONObject();
                occupancy.put("standardOccupancy", roomTypeDto.getIntQuanStd());
                occupancy.put("maximumOccupancy", roomTypeDto.getIntQuanMax());

                rooms.put("occupancy", occupancy);

                // =============================
                // rate 정보
                // =============================
                int intAID = roomTypeDto.getIntAID();
                JSONObject rates = new JSONObject();

                List<Integer> intIdxList = new ArrayList<>();
                intIdxList.add(intRmIdx);
                rates.put("sellerRoomIds", intIdxList);

                rates.put("sellerRateId", intRmIdx);

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                rates.put("saleStartedAt", sdf.format(timestamp));

                // 취소 환불 규정
                JSONObject cancelPolicy = new JSONObject();
                // 쿠팡은 성수기/비수기 구분 없이 보내기 때문에 일단 비수기로 넣고, 추가로 notice에 string으로 성수기/비수기 규정 만들어서 보내기
                String cancelNotice = getCancelInfo(intAID);
                cancelPolicy.put("notice", cancelNotice);

                String strRefundYn = roomTypeDto.getStrRefundYn();
                // 취소/환불정책 가져오기
                List<CancelRulesDto> cancelDtoList = commonMapper.getCancelRuleList(intAID);
                if(strRefundYn.equals("N")){ // 환불 불가
                    cancelPolicy.put("refundable", false);
                }else{ // 환불 가능
                    List<JSONObject> refundList = new ArrayList<>();

                    for(CancelRulesDto c : cancelDtoList){
                        JSONObject refundJson = new JSONObject();
                        if(c.getStrFlag().equals("OOF")){
                            refundJson.put("days", c.getIntDay());
                            refundJson.put("refundRate", c.getIntPercent());

                            refundList.add(refundJson);
                        }
                    }
                    cancelPolicy.put("refundRates", refundList);
                }
                rates.put("cancelPolicy", cancelPolicy);

                // 추가요금
                JSONObject extraGuestPolicy = new JSONObject();
                if(roomTypeDto.getIntExtraA() != 0 || roomTypeDto.getIntExtraC() != 0){
                    extraGuestPolicy.put("surchargeAvailable", true);

                    List<JSONObject> surchargesList = new ArrayList<>();
                    JSONObject surcharges = new JSONObject();
                    if(roomTypeDto.getIntExtraA() != 0){
                        surcharges.put("ageType", "ADULT");
                        // TODO : 어린이 요금 기준 나이 확인 필요
                        surcharges.put("minAge", 2);
                        surcharges.put("surcharge", roomTypeDto.getIntExtraA());

                        surchargesList.add(surcharges);
                    }
                    if(roomTypeDto.getIntExtraC() != 0){
                        surcharges.put("ageType", "CHILD");
                        // TODO : 어린이 요금 기준 나이 확인 필요
                        surcharges.put("minAge", 2);
                        surcharges.put("surcharge", roomTypeDto.getIntExtraC());
                        surchargesList.add(surcharges);
                    }
                    rates.put("extraGuestPolicy", extraGuestPolicy);
                }

                // 체크인 체크아웃
                JSONObject checkInOutPolicy = new JSONObject();
                AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);
                checkInOutPolicy.put("checkInStartTime", accommDto.getStrCheckIn());
                checkInOutPolicy.put("checkOutEndTime", accommDto.getStrCheckOut());

                rates.put("checkInOutPolicy", checkInOutPolicy);

                if(strCpItemCode != null){ // 수정
                    // =============================
                    // 객실 수정
                    // =============================
                    // api 호출

                    JSONObject returnJson = coupangApi.coupangPutApi(gson.toJson(rooms), "travel/lodgings/" + strPdtCode + "/rooms/" + strCpItemCode);
                    // 응답값 처리
                    String returnCode = returnJson.get("code").toString();
                    if(returnCode.equals("200")){
                        // =============================
                        // rate 수정
                        // =============================

                        String strCpRateCode = roomTypeDto.getStrCprateCode();
                        // api 호출
                        returnJson = coupangApi.coupangPutApi(gson.toJson(rates), "travel/lodgings/" + strPdtCode + "/rates/" + strCpRateCode);
                        // 응답값 처리
                        returnCode = returnJson.get("code").toString();
                        if(returnCode.equals("200")){
                            message = "객실 수정 완료";
                        }else{
                            message = "rate(옵션) 수정 실패";
                            logWriter.add("code : " + returnCode);
                            String returnMsg = returnJson.get("message").toString();
                            logWriter.add(returnMsg);
                        }
                    }else{
                        message = "객실 수정 실패";
                        logWriter.add("code : " + returnCode);
                        String returnMsg = returnJson.get("message").toString();
                        logWriter.add(returnMsg);
                    }
                }else{ // 생성
                    // =============================
                    // 객실 생성
                    // =============================
                    List<JSONObject> roomList = new ArrayList<>();
                    roomList.add(rooms);

                    JSONObject lodgingRoomCreateDtos = new JSONObject();
                    lodgingRoomCreateDtos.put("lodgingRoomCreateDtos", roomList);

                    // api 호출
                    JSONObject returnJson = coupangApi.coupangPostApi(gson.toJson(lodgingRoomCreateDtos), "travel/lodgings/" + strPdtCode + "/rooms");
                    // 응답값 처리
                    String returnCode = returnJson.get("code").toString();
                    if(returnCode.equals("200")) {
                        JSONArray dataJsonArr = (JSONArray) returnJson.get("data");
                        for(Object d : dataJsonArr){
                            JSONObject jsonObject = (JSONObject) d;
                            strCpItemCode = jsonObject.get("travelItemId").toString();

                            // =============================
                            // rate 생성
                            // =============================

                            List<JSONObject> rateList = new ArrayList<>();
                            rateList.add(rates);

                            JSONObject lodgingRateCreateDtos = new JSONObject();
                            lodgingRateCreateDtos.put("lodgingRateCreateDtos", rateList);

                            // api 호출
                            returnJson = coupangApi.coupangPostApi(gson.toJson(lodgingRateCreateDtos), "travel/lodgings/" + strPdtCode + "/rates");
                            // 응답값 처리
                            returnCode = returnJson.get("code").toString();
                            String strCpRateCode = "";
                            if(returnCode.equals("200")){
                                dataJsonArr = (JSONArray) returnJson.get("data");
                                for(Object da : dataJsonArr) {
                                    jsonObject = (JSONObject) da;
                                    strCpRateCode = jsonObject.get("sellerRateId").toString();
                                }

                                // db에 strCpItemCode, strCpRateCode 업데이트
                                int updateResult = coupangMapper.updateCpCodes(strCpItemCode, strCpRateCode, intRmIdx);
                                if(updateResult > 0){
                                    message = "객실 생성 완료";
                                }else{
                                    message = "객실 생성 실패";
                                }

                            }else{
                                message = "rate(옵션) 생성 실패";
                                logWriter.add("code : " + returnCode);
                                String returnMsg = returnJson.get("message").toString();
                                logWriter.add(returnMsg);
                            }
                        }
                    }else{
                        message = "객실 생성 실패";
                        logWriter.add("code : " + returnCode);
                        String returnMsg = returnJson.get("message").toString();
                        logWriter.add(returnMsg);
                    }
                }
            }else{
                message = "시설 정보가 존재하지 않습니다(쿠팡 연동여부 확인 필요)";
            }

        }catch (Exception e){
            e.printStackTrace();
            message = "객실 생성/수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 숙박상품 수정
    public String updateAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest) {
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            // intAID로 strPdtCode 조회
            String strPdtCode = coupangMapper.getStrPdtCode(intAID);
//            strPdtCode = "10000002706646";
            if(strPdtCode != null){
                AccommDto accommDto = commonMapper.getAcmInfo(intAID, intOmkIdx);

                JSONObject accommJson = new JSONObject();
                accommJson.put("travelProductId", Long.parseLong(strPdtCode));
                accommJson.put("sellerProductId", intAID);
                accommJson.put("nation", "KR"); // 시설 국가
                accommJson.put("name", URLEncoder.encode(accommDto.getStrSubject(), "utf-8")); // 시설 이름
                accommJson.put("introduction", accommDto.getStrDescription()); // 시설 소개
                accommJson.put("regionKeyword", accommDto.getStrRegionKeyword()); // 지역 키워드

                // 태그 데이터 세팅
                List<String> strTagList = new ArrayList<>();
                if(accommDto.getStrKeywords() != null){
                    String strKeywords = accommDto.getStrKeywords();
                    strKeywords.replaceAll("\\s", "");
                    String[] keywordArr = strKeywords.split(",");

                    for(String keyword : keywordArr){
                        strTagList.add(keyword);
                    }
                }
                if(accommDto.getStrFac() != null){
                    String strFac = accommDto.getStrFac();
                    String[] facArr = strFac.split(",");

                    for(String fac : facArr){
                        strTagList.add(fac);
                    }
                }
                if(accommDto.getStrAround() != null){
                    String strAround = accommDto.getStrAround();
                    String[] aroundArr = strAround.split(",");

                    for(String around : aroundArr){
                        strTagList.add(around);
                    }
                }

                accommJson.put("searchTags", strTagList);

                // image
                List<JSONObject> imageList = new ArrayList<>();
                if(accommDto.getStrACMPhotos() != null){
                    String strAcmPhotos = accommDto.getStrACMPhotos();
                    String[] photoArr = strAcmPhotos.split("\\|");

                    for(int i=0; i< photoArr.length; i++){
                        JSONObject images = new JSONObject();
                        // TODO : 추후 이미지 저장 경로 정해지면 추가 할 것
                        images.put("sellerUrl", "https://condo24.com/" + photoArr[i]);
                        images.put("seq", i+1);
                        if(i==0){
                            images.put("representative", true);
                        }
                        imageList.add(images);
                    }
                }


                accommJson.put("images", imageList);

                accommJson.put("cancelType", "APPROVAL");

                String cancelNotice = getCancelInfo(intAID);
                accommJson.put("cancelPolicyNotice", cancelNotice);

                String usageNotice = accommDto.getStrAcmNotice();
                if(usageNotice == null){
                    usageNotice = "유의사항";
                }
                accommJson.put("usageNotice", usageNotice);

                JSONObject address = new JSONObject();
                address.put("latitude", accommDto.getDecLat());
                address.put("longitude", accommDto.getDecLon());
                address.put("roadNameAddress", accommDto.getStrAddr1());
                address.put("nation", "KR");
                address.put("city", accommDto.getStrRegionKeyword());
                address.put("district", accommDto.getStrDistrict2());
                address.put("zipCode", accommDto.getStrZipCode());

                accommJson.put("address", address);

                accommJson.put("representativePhone", "1588-0134");

                JSONArray lodgingUpdateDtos = new JSONArray();
                lodgingUpdateDtos.add(accommJson);

                JSONObject requestJson = new JSONObject();
                requestJson.put("lodgingUpdateDtos", lodgingUpdateDtos);

                // api 호출
                JSONObject returnJson = coupangApi.coupangPutApi(gson.toJson(requestJson), "travel/lodgings");
                // 응답값 처리
                String returnCode = returnJson.get("code").toString();
                if(returnCode.equals("200")){
                    JSONObject dataJson = (JSONObject) returnJson.get("data");
                    JSONArray successArr = (JSONArray) dataJson.get("success");
                    JSONArray failArr = (JSONArray) dataJson.get("fail");

                    for(Object f : failArr) {
                        JSONObject jsonObject = (JSONObject) f;
                        if(jsonObject != null){
                            String failReason = jsonObject.get("reason").toString();

                            message = "상품 수정 실패";
                            logWriter.add(failReason);
                        }
                        break;
                    }

                    for(Object s : successArr){
                        JSONObject jsonObject = (JSONObject) s;
                        if(jsonObject != null) {
                            String name = jsonObject.get("name").toString();
                            System.out.println("name : " + name);
                            System.out.println(URLDecoder.decode(name, "utf-8"));
                            message = "상품 수정 완료";
                        }
                        break;
                    }

                }else{
                    message = "쿠팡 api 호출 실패";
                    logWriter.add("code : " + returnCode);
                    String returnMsg = returnJson.get("message").toString();
                    logWriter.add(returnMsg);
                }

            }else{
                message = "쿠팡 상품 아이디가 존재하지 않습니다";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 숙박상품 삭제
    public String deleteAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest) {
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try {
            // intAID로 strPdtCode 조회
            String strPdtCode = coupangMapper.getStrPdtCode(intAID);
//            strPdtCode = "10000002706646";
            if(strPdtCode != null){
                // API 호출
                JSONObject returnJson = coupangApi.coupangDeleteApi("travel/lodgings/" + strPdtCode);
                // 응답값 처리
                String returnCode = returnJson.get("code").toString();
                String returnMsg = returnJson.get("message").toString();
                if(returnCode.equals("200")){
                    message = "상품 삭제 완료";
                }else{
                    message = "쿠팡 api 호출 실패";
                    logWriter.add("code : " + returnCode);
                    logWriter.add(returnMsg);
                }
            }else{
                message = "쿠팡 상품 아이디가 존재하지 않습니다";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();
            message = "상품 삭제 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 숙박 상품 조회
    public String getAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        JSONObject dataJson = new JSONObject();

        try{
            String strPdtCode = coupangMapper.getStrPdtCode(intAID);
//            strPdtCode = "10000002706646";

            JSONObject returnJson = coupangApi.coupangGetApi("travel/lodgings/" + strPdtCode);

            // 응답값 처리
            String returnCode = returnJson.get("code").toString();

            if(returnCode.equals("200")){
                dataJson = (JSONObject) returnJson.get("data");

                String name = dataJson.get("name").toString();
                System.out.println(name);
                System.out.println("name : " + URLDecoder.decode(name, "utf-8"));

            }else if(returnCode.equals("410")){
                message = "이미 삭제된 상품입니다";
            }else{
                message = "쿠팡 api 호출 실패";
            }
            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();message = "상품 조회 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);

        }

        return commonFunction.makeReturn(dataType, statusCode, message, dataJson);
    }

    // 취소규정 생성
    public String getCancelInfo(int intAID){
        List<CancelRulesDto> cancelDtoList = commonMapper.getCancelRuleList(intAID);

        String cancelPolicyNotice = "";

        // 위약금이 있는 날짜+1로 '위약금 없음' 을 만들기 위해 카운트
        int opsCnt = 0;
        int oofCnt = 0;
        for(int i = 0; i< cancelDtoList.size(); i++){
            if(cancelDtoList.get(i).getStrFlag().equals("OPS")){
                opsCnt += 1;
            }else{
                oofCnt += 1;
            }
        }

        String oofCancelInfo = ""; // 비수기 취소규정
        String opsCancelInfo = ""; // 성수기 취소규정

        int opsCount = opsCnt;
        int oofCount = oofCnt;
        // 비수기 취소규정 만들기
        for(int i = cancelDtoList.size()-1; i>=0; i--){
            int intDay = cancelDtoList.get(i).getIntDay();
            int intPercent = cancelDtoList.get(i).getIntPercent();
            if(cancelDtoList.get(i).getStrFlag().equals("OOF")){

                if(intDay == 0){
                    oofCancelInfo += "- 입실당일취소/No-show 시 : 위약금 100%\n";
                }else{
                    if(oofCnt == oofCount){
                        oofCancelInfo += "- 기준일 " + (intDay+1) + "일 전 : 위약금 없음\n";
                    }
                    if(intPercent == 0){
                        oofCancelInfo += "- 기준일 " + intDay + "일 전 : 위약금 없음\n";
                    }else{
                        oofCancelInfo += "- 기준일 " + intDay + "일 전 : 위약금" + intPercent + "%\n";
                    }
                }
                oofCount -= 1;
            } // 성수기 취소규정 만들기
            else if(cancelDtoList.get(i).getStrFlag().equals("OPS")){
//                System.out.println(cancelDtoList.get(i));
                if(intDay == 0){
                    opsCancelInfo += "- 입실당일취소/No-show 시 : 위약금 100% \n";
                }else{
                    if(opsCnt == opsCount){
                        opsCancelInfo += "- 기준일 " + (intDay+1) + "일 전 : 위약금 없음 \n";
                    }
                    if(intPercent == 0){
                        opsCancelInfo += "- 기준일 " + intDay + "일 전 : 위약금 없음\n";
                    }else{
                        opsCancelInfo += "- 기준일 " + intDay + "일 전 : 위약금" + intPercent + "%\n";
                    }
                }
                opsCount += 1;
            }
        }

        // Default 취소규정 생성
        cancelPolicyNotice =
                "☎ 문의전화 1588-0134 / 평일 09:00~17:00 \n" +
                        "취소수수료 안내 \n" +
                        "* 예약 확정 후 취소, 변경 신청 시 위약금이 발생합니다. \n" +
                        "위약금은 성수기, 비수기 등에 따라 다르므로 주의하시기 바랍니다. \n" +
                        "\n 비수기 - 입실일 기준 \n \n" +
                        oofCancelInfo +
                        "\n 성수기 및 연휴 - 입실일 기준 \n \n" +
                        opsCancelInfo  +
                        "\n취소 수수료는 예약일기준이 아닌 체크인(숙박일) 날짜 기준으로 발생됩니다. \n" +
                        "업체 확인 후 취소가 완료됩니다. \n" +
                        "영업일(주말, 토/공휴일 제외) 17시 기준으로 취소 위약금이 부과됩니다. \n";

        return cancelPolicyNotice;
    }

    // 요금/수량
    public String creUpdGoods(String dataType, int intRmIdx, String strDate, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";
        try{
            Map<String, Object> map = coupangMapper.getIntAIDnPdtCode(intRmIdx);
            int intAID = Integer.parseInt(map.get("intAID").toString());
            String strPdtCode = map.get("strPdtCode").toString();

            int failCnt = 0;
            List<StockDto> stockList = commonMapper.getStockList(intAID, intOmkIdx, strDate);
            for(StockDto stock : stockList){
                JSONArray lodgingInventoryDtos = new JSONArray();
                JSONObject stockJson = new JSONObject();
                stockJson.put("rateId", Long.parseLong(stock.getStrCprateCode()));

                String strCheckInDate = stock.getDateSales().trim();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                strCheckInDate = sdf.format(simpleDateFormat.parse(strCheckInDate));

                stockJson.put("checkInDate", strCheckInDate);
                stockJson.put("stockCount", stock.getIntStock());
                stockJson.put("originalPrice", stock.getMoneyCost());
                stockJson.put("salePrice", stock.getMoneySales());

                lodgingInventoryDtos.add(stockJson);

                JSONObject requestJson = new JSONObject();
                requestJson.put("lodgingInventoryDtos", lodgingInventoryDtos);

                // API 호출
                JSONObject returnJson = coupangApi.coupangPatchApi(gson.toJson(requestJson), "travel/lodgings/" + strPdtCode + "/rooms/" + stock.getStrCpItemCode());
                // 응답값 처리
                String returnCode = returnJson.get("code").toString();
                String returnMsg = returnJson.get("message").toString();
                if(!returnCode.equals("200")){
                    failCnt ++;

                    logWriter.add("code : " + returnCode + " >> " + returnMsg);
                }
            }
            if(failCnt > 0){
                message = "쿠팡 api 호출 실패";
            }else{
                message = "재고&요금 등록/수정 완료";
            }

            logWriter.add(message);
            logWriter.log(0);
        }catch (Exception e){
            e.printStackTrace();

            message = "재고&요금 등록/수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return commonFunction.makeReturn(dataType, statusCode, message);
    }

}
