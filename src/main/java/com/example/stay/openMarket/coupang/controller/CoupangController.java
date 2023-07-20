package com.example.stay.openMarket.coupang.controller;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.service.CommonService;
import com.example.stay.openMarket.coupang.Api.CoupangApi;
import com.example.stay.common.util.Constants;
import com.example.stay.openMarket.common.dto.CondoDto;
import com.example.stay.openMarket.common.dto.ToconDto;


import com.example.stay.openMarket.common.service.CommonApiService;
import com.example.stay.openMarket.coupang.service.CoupangService;
import com.example.stay.openMarket.coupang.service.LodgingsService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/coupang/*")
public class CoupangController {

    @Autowired
    private CommonApiService commonApiService;

    @Autowired
    private LodgingsService lodgingsService;

    @Autowired
    private CoupangService coupangService;

    /**
     * 숙박상품 생성
     */
    @GetMapping("/createAccomm")
    @ResponseBody
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return coupangService.createAccomm(dataType, intAID, httpServletRequest);

//        try {
//
//
//            String strPath =  Constants.LodgingsPath + "/v1/marketplace/travel/lodgings";
//
//
//
//            // 숙박상품 생성에 필요한 데이터 가져오기
//            // 숙박 상품 정보
//            AccommDto accommDto = commonService.getAcmInfo(intAID, omk);
//            if(accommDto != null){
//                // 룸타입별 정보
//                List<ToconDto> roomType = commonApiService.getroomType(intAID, omk);
//                // 취소규정
//                String cancelInfo = commonApiService.getCancelInfo(intAID);
//
//                // json 데이터 생성
//                JSONArray jsonArrData = new JSONArray();
//                JSONObject jsonRequest = new JSONObject();
//                JSONObject lodgingCreateDtos = new JSONObject();
//
//                lodgingCreateDtos.put("productDetailType", roomInfo.getStrFlag());
//                lodgingCreateDtos.put("travelType", "DOMESTIC");
//                lodgingCreateDtos.put("sellerProductId", roomInfo.getIntAID());
//                lodgingCreateDtos.put("nation", "KR");
//                lodgingCreateDtos.put("name", roomInfo.getStrAcmName());
//                lodgingCreateDtos.put("introduction", roomInfo.getStrSummary());
//                lodgingCreateDtos.put("regionKeyword", roomInfo.getStrLoc());
//
//                // images
//                JSONArray imagesArr = new JSONArray();
//                String[] imgList = roomInfo.getStrCondoPhotos().split("\\|");
//                for(int i=0; i<imgList.length; i++){
//                    JSONObject images = new JSONObject();
//                    images.put("sellerUrl", Constants.condoSellerUrl + imgList[i]);
//                    if(i == 0){
//                        images.put("representative", true);
//                        images.put("description", "대표이미지");
//                    }else{
//                        images.put("representative", false);
//                        images.put("description", "숙소이미지");
//                    }
//                    images.put("imageGroup","VIEW");
//                    images.put("seq", i);
//                    imagesArr.add(images);
//                }
//                lodgingCreateDtos.put("images", imagesArr);
//
//                lodgingCreateDtos.put("cancelType", "APPROVAL");
//    //            lodgingCreateDtos.put("cancelPolicyNotice", cancelInfo);  -> 밑에랑 중복 일단 주석처리.
//                lodgingCreateDtos.put("instantBooking", false);
//                lodgingCreateDtos.put("usageGuide", roomInfo.getStrDescription());
//                lodgingCreateDtos.put("usageNotice", roomInfo.getStrUsageNotice());
//                lodgingCreateDtos.put("usageNotice", "이용안내~~유의사항~~");
//
//                // addrress & localAddress
//                JSONObject address = new JSONObject();
//                address.put("latitude", roomInfo.getDecLat());
//                address.put("longitude", roomInfo.getDecLng());
//                address.put("roadNameAddress", roomInfo.getStrAddress());
//                address.put("landNumberAddress", roomInfo.getStrLandNumberAdr());
//                address.put("nation", "KR");
//                address.put("province", "도");
//                address.put("city", roomInfo.getStrCity());
//                address.put("district", roomInfo.getStrGugun());
//                address.put("town", "도");
//                address.put("zipCode", roomInfo.getStrZipCode());
//                lodgingCreateDtos.put("address", address);
//                lodgingCreateDtos.put("localAddress", address);
//
//                lodgingCreateDtos.put("representativePhone", "1588-0134");
//
//                // rooms, rates
//                JSONArray roomsArr = new JSONArray();
//                List<Integer> pyongIdxs = new LinkedList<>();
//                for(ToconDto t : roomType){
//                    JSONObject rooms = new JSONObject();
//                    rooms.put("sellerRoomId", t.getPyongIdx());
//                    rooms.put("name", t.getTocode());
//                    rooms.put("additionalInfo", t.getTocodeText());
//
//                    // room --------------------------------------------------------------------------------------------
//                    // images
//                    JSONArray roomImagesArr = new JSONArray();
//                    String[] pyongImgs = {"22782_1.jpg", "22782_2.jpg", "22782_3.jpg"};
//    //                    String[] pyongImgs = t.getPyongImgs().split("\\|");
//                    for(int j=0; j<pyongImgs.length; j++){
//                        JSONObject roomImages = new JSONObject();
//                        roomImages.put("sellerUrl", Constants.condoSellerUrl + pyongImgs[j]);
//    //                        roomImages.put("sellerUrl", Constants.toconSellerUrl + t.getConId() + "/rooms/" + t.getPyongIdx() + "/" + pyongImgs[j]);
//                        if(j == 0){
//                            roomImages.put("representative", true);
//                        }else{
//                            roomImages.put("representative", false);
//                        }
//                        roomImages.put("seq", j);
//
//                        roomImagesArr.add(roomImages);
//
//                    }
//
//                    rooms.put("images", roomImagesArr);
//
//                    // occupancy
//                    JSONObject occupancy = new JSONObject();
//                    occupancy.put("standardOccupancy", t.getStandpeopleCnt());
//                    occupancy.put("maximumOccupancy", t.getMaxpeopleCnt());
//
//                    rooms.put("occupancy", occupancy);
//
//                    roomsArr.add(rooms);
//
//                    // rates -------------------------------------------------------------------------------------------
//                    pyongIdxs.add(t.getPyongIdx());
//                }
//
//                lodgingCreateDtos.put("rooms", roomsArr);
//
//                // rates -----------------------------------------------------------------------------------------------
//                JSONObject rates = updateLodgingRate(intAID, roomInfo, pyongIdxs, cancelInfo);
//                lodgingCreateDtos.put("rates", rates);
//
//                // 판매 정지 값으로 테스트
//                lodgingCreateDtos.put("saleStatus", "SUSPENDED");
//
//
//                jsonArrData.add(lodgingCreateDtos);
//                jsonRequest.put("lodgingCreateDtos", jsonArrData);
//
//                // api로 보낼 json데이터 담을 변수
//                String strJsonData = "";
//
//                // Json 파싱 위해 Gson객체 생성
//                Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                // JsonObject -> String
//                strJsonData = gson.toJson(jsonRequest);
//
//                // 쿠팡 api 호출
//                CoupangApi coupangApi = new CoupangApi();
//
//                long APIStart = System.currentTimeMillis();
//                coupangApi.coupangPostApi(strJsonData, strPath);
//                System.out.println("쿠팡 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
//                System.out.println("쿠팡에 상품 등록 완료");
//
//    //            System.out.println("**********************************");
//    //            System.out.println(response);
//    //            System.out.println("**********************************");
//
//
//
//                System.out.println("숙박 상품 생성 완료");
//            }else{
//                System.out.println("해당 상품 정보가 존재하지 않습니다");
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("숙박 상품 등록 실패");
//        }

    }

    /**
     * 숙박상품 수정
     */
    @GetMapping("/updateAccomm")
    @ResponseBody
    public String updateAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return coupangService.updateAccomm(dataType, intAID, httpServletRequest);

        // 숙박상품 수정에 필요한 데이터 가져오기
//        try{
//            // 숙박 상품 정보
//            CondoDto roomInfo = commonApiService.getInfo(intAID, omk);
//            // 취소규정
//            String cancelInfo = commonApiService.getCancelInfo(intAID);
//
//            // json 데이터 생성
//            JSONArray jsonArrData = new JSONArray();
//            JSONObject jsonRequest = new JSONObject();
//            JSONObject lodgingUpdateDtos = new JSONObject();
//
//            lodgingUpdateDtos.put("travelProductId", roomInfo.getStrItemID());
//            lodgingUpdateDtos.put("sellerProductId", roomInfo.getIntAID());
//            lodgingUpdateDtos.put("nation", "KR");
//            lodgingUpdateDtos.put("name", roomInfo.getStrAcmName());
//            lodgingUpdateDtos.put("introduction", roomInfo.getStrSummary());
//            lodgingUpdateDtos.put("regionKeyword", roomInfo.getStrLoc());
//
//            // images
//            JSONArray imagesArr = new JSONArray();
//            String[] imgList = roomInfo.getStrCondoPhotos().split("\\|");
//            for(int i=0; i<imgList.length; i++){
//                JSONObject images = new JSONObject();
//                images.put("sellerUrl", Constants.condoSellerUrl + imgList[i]);
//                if(i == 0){
//                    images.put("representative", true);
//                    images.put("description", "대표이미지");
//                }else{
//                    images.put("representative", false);
//                    images.put("description", "숙소이미지");
//                }
//                images.put("imageGroup","VIEW");
//                images.put("seq", i);
//                imagesArr.add(images);
//            }
//            lodgingUpdateDtos.put("images", imagesArr);
//
//            lodgingUpdateDtos.put("cancelType", "APPROVAL");
//            lodgingUpdateDtos.put("cancelPolicyNotice", cancelInfo);
//            lodgingUpdateDtos.put("instantBooking", false);
//            lodgingUpdateDtos.put("usageNotice", roomInfo.getStrUsageNotice());
//            lodgingUpdateDtos.put("usageNotice", "이용안내~~유의사항~~");
//
//            // addrress & localAddress
//            JSONObject address = new JSONObject();
//            address.put("latitude", roomInfo.getDecLat());
//            address.put("longitude", roomInfo.getDecLng());
//            address.put("roadNameAddress", roomInfo.getStrAddress());
//            address.put("landNumberAddress", roomInfo.getStrLandNumberAdr());
//            address.put("nation", "KR");
//            address.put("province", "도");
//            address.put("city", roomInfo.getStrCity());
//            address.put("district", roomInfo.getStrGugun());
//            address.put("town", "도");
//            address.put("zipCode", roomInfo.getStrZipCode());
//            lodgingUpdateDtos.put("address", address);
//            lodgingUpdateDtos.put("localAddress", address);
//
//            lodgingUpdateDtos.put("representativePhone", "1588-0134");
//
//            jsonArrData.add(lodgingUpdateDtos);
//            jsonRequest.put("lodgingUpdateDtos", jsonArrData);
//
//            // api로 보낼 json데이터 담을 변수
//            String strJsonData = "";
//            // Json 파싱 위해 Gson객체 생성
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            // JsonObject -> String
//            strJsonData = gson.toJson(jsonRequest);
//
//            // 쿠팡 api 호출
//            CoupangApi coupangApi = new CoupangApi();
//
//            long APIStart = System.currentTimeMillis();
//            JSONObject resultJson = coupangApi.coupangPutApi(strJsonData, strPath);
//            System.out.println("쿠팡 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
//            System.out.println("쿠팡에 상품 수정 완료");
//
//            /**
//             * 숙박상품 수정 성공하면 OMK_PRODUCT에 coupang_id UPDATE  -> 테스트해보고 바뀌면...
//             */
//            System.out.println("숙박 상품 수정 완료");
//
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("숙박 상품 수정 실패");
//        }
    }

    /**
     * 숙박상품 삭제
     */
    @GetMapping("/deleteAccomm")
    @ResponseBody
    public String deleteAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return coupangService.deleteAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 객실 생성/수정
     */
    @GetMapping("/creUpdRoom")
    @ResponseBody
    public String creUpdRoom(String dataType, int intRmIdx, HttpServletRequest httpServletRequest){
        return coupangService.creUpdRoom(dataType, intRmIdx, httpServletRequest);
        //roomtype 생성인지 수정인지 확인 -> travelItemId가 있으면 수정, 없으면 생성
//        String strMethod = "";
//        boolean createRoomOk = false;
//        try{
//            // 쿠팡에 시설이 생성되어있는지 확인
//            String coupangId = lodgingsService.getCoupangIdByconId(intAID);
//            if(coupangId != null && !coupangId.equals("")){
//                // 생성 or 수정할 상품 pyong_idx, rateId, travelItemId 가져오기 (동기화 버튼 있는 화면 상품들에서 strIngYn, coupang_yn = Y 인것만)
//                List<ToconDto> creUpdRoomType = lodgingsService.getCreUpdRoomByConId(intAID);
//
//                // api로 보낼 json데이터 담을 변수
//                String strJsonData = "";
//                String strPath = "";
//                CoupangApi coupangApi = new CoupangApi();
//
//                List<Integer> pyongIdxs = new LinkedList<>();
//                for(ToconDto t : creUpdRoomType){
//                    if(t.getTravelItemId() == null || t.getTravelItemId().equals("")){
//                        strMethod = "POST";
//                        strPath =  Constants.LodgingsPath + "v1/marketplace/travel/lodgings/" + coupangId + "/rooms";
//
//                        JSONObject returnJson = lodgingsService.getRoomTypeJson(t.getPyongIdx());
//                        if(returnJson != null){
//                            JSONArray jsonArrData = new JSONArray();
//                            jsonArrData.add(returnJson);
//
//                            JSONObject jsonRequest = new JSONObject();
//                            jsonRequest.put("lodgingRoomCreateDtos", jsonArrData);
//
//                            // Json 파싱 위해 Gson객체 생성
//                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                            // JsonObject -> String
//                            strJsonData = gson.toJson(jsonRequest);
//                            System.out.println("********************************************");
//                            System.out.println(strJsonData);
//
//                            long APIStart = System.currentTimeMillis();
//                            coupangApi.coupangPostApi(strJsonData, strPath);
//                            System.out.println("쿠팡 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
//
//                            // 성공하면
//                            System.out.println("쿠팡에 객실 상품 생성 완료");
//                            // 응답값 처리 후
//                            createRoomOk = true;
//                            System.out.println("객실 상품 생성 완료");
//
//                            pyongIdxs.add(t.getPyongIdx());
//
//                        }else{
//                            System.out.println("Json 데이터 생성 실패");
//                        }
//
//                    }else{
//                        strMethod = "PUT";
//                        strPath =  Constants.LodgingsPath + "v1/marketplace/travel/lodgings/" + coupangId + "/rooms/" + t.getTravelItemId();
//
//                        JSONObject returnJson = lodgingsService.getRoomTypeJson(t.getPyongIdx());
//                        if(returnJson != null){
//                            // Json 파싱 위해 Gson객체 생성
//                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//                            strJsonData = gson.toJson(returnJson);
//
//                            System.out.println("********************************************");
//                            System.out.println(strJsonData);
//
//                            long APIStart = System.currentTimeMillis();
//                            coupangApi.coupangPutApi(strJsonData, strPath);
//                            System.out.println("쿠팡 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
//
//                            // 성공하면
//                            System.out.println("쿠팡에 객실 상품 수정 완료");
//                            // 응답값 처리 후
//                            System.out.println("객실 상품 수정 완료");
//
//                        }else{
//                            System.out.println("Json 데이터 생성 실패");
//                        }
//                    }
//                }
//                // 룸 생성 성공시
//                // 시설 요금제 sellerRoomIds에 생성한 pyongIdx추가------------------------------------------------
//                // -> 시설 요금제 업데이트 api호출
//                if(createRoomOk){
//                    CondoDto roomInfo = commonApiService.getInfo(intAID, omk);
//                    String cancelInfo = commonApiService.getCancelInfo(intAID);
//
//                    JSONObject rates = updateLodgingRate(intAID, roomInfo, pyongIdxs, cancelInfo);
//
//                    JSONObject lodgingRateUpdateDto = new JSONObject();
//                    lodgingRateUpdateDto.put("rates", rates);
//                }
//            }else{
//                System.out.println("해당 객실의 coupang_id가 존재하지 않음. 시설이 쿠팡에 등록되어있는지 확인 필요");
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("객실 생성/수정 실패");
//        }
    }

    @GetMapping("/getAccomm")
    public void getAccomm(int intAID){
        coupangService.getAccomm(intAID);
    }

    /**
     * 숙박상품 조회 -> 변수명 cid(intAID) -> 이거로 coupang_id 조회해서 작업
     */
    @GetMapping("/getLodgingInfo")
    public void getLodgingInfo(){
        // 임의로 박아둔것. 나중에 빼고 매개변수로 받기
        String travelProductId = "512542156641013763";

//        try {
//            // 쿠팡 api 호출
//            String strPath =  Constants.LodgingsPath + "v1/marketplace/travel/lodgings/" + travelProductId;
//
//            CoupangApi coupangApi = new CoupangApi();
//
//            long APIStart = System.currentTimeMillis();
//            coupangApi.coupangGetApi(strPath);
//            System.out.println("쿠팡 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
//            System.out.println("숙박 상품 조회 완료");
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("API 호출 실패");
//        }
    }

    /**
     *  숙박 상품 요금 수정
     */
    @GetMapping("updateLodgingRate")
    public JSONObject updateLodgingRate(int intAID, CondoDto roomInfo, List<Integer> pyongIdxs, String cancelInfo){
        JSONObject lodgingRateUpdateDto = new JSONObject();
//        try{
//            String coupangId = roomInfo.getCoupangId();
//
//            lodgingRateUpdateDto.put("sellerRoomIds", pyongIdxs);
//            lodgingRateUpdateDto.put("name", "표준요금");
//
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//            SimpleDateFormat sdf = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
//            lodgingRateUpdateDto.put("saleStartedAt", sdf.format(timestamp));
//
//            JSONObject cancelPolicy = new JSONObject();
//            cancelPolicy.put("notice", cancelInfo);
//            lodgingRateUpdateDto.put("cancelPolicy", cancelPolicy);
//
//            JSONObject checkInOutPolicy = new JSONObject();
//            checkInOutPolicy.put("checkInStartTime", roomInfo.getStrTimeIn());
//            checkInOutPolicy.put("checkOutEndTime", roomInfo.getStrTimeOut());
//            lodgingRateUpdateDto.put("cancelPolicy", checkInOutPolicy);
//
//            // api로 보낼 json데이터 담을 변수
//            String strJsonData = "";
//
//            // Json 파싱 위해 Gson객체 생성
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            // JsonObject -> String
//            strJsonData = gson.toJson(lodgingRateUpdateDto);
//
//            String rateId = lodgingsService.getRateIdByConId(intAID);
//            // 쿠팡 api 호출
//            String strPath = Constants.LodgingsPath + "v1/marketplace/travel/lodgings/" + coupangId + "/rates/" + rateId;
//
//            CoupangApi coupangApi = new CoupangApi();
//
//            long APIStart = System.currentTimeMillis();
//            coupangApi.coupangPutApi(strJsonData, strPath);
//            System.out.println("쿠팡 API 호출 실행 시간 : " + (System.currentTimeMillis()-APIStart)/1000.0);
//
//            System.out.println("숙박 상품 요금 수정 완료");
//        }catch (Exception e){
//            e.printStackTrace();
//            System.out.println("API 호출 실패");
//        }
        return lodgingRateUpdateDto;
    }

    /**
     *  숙박 객실 요금/수량 등록/수정
     */
    @GetMapping("creUpdRoomRate")
    public void creUpdRoomRate(){
        // goods에 있는거로 일단 사용 오픈마켓


    }

}
