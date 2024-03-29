package com.example.stay.accommodation.wellihilli.controller;

import com.example.stay.accommodation.wellihilli.mapper.WellihilliMapper;
import com.example.stay.accommodation.wellihilli.service.BookingService;
import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("wellihilli.BookingController")
@RequestMapping("/wellihilli/booking/*")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private CommonAcmMapper commonAcmMapper;

    @Autowired
    private WellihilliMapper wellihilliMapper;

    CommonFunction commonFunction = new CommonFunction();

    // 예약 가능 여부 조회
//    @GetMapping("checkAvailBooking")
//    public void checkAvailBooking(String pyung, String sDate, long sleep, int roomCount, String roomType){
//        bookingService.checkAvailBooking(pyung, sDate, sleep, roomCount, roomType);
//    }

//    // 재고 등록 및 수정
//    @GetMapping("getPackageStock")
//    @ResponseBody
//    public String getPackageStock(String dataType, HttpServletRequest httpServletRequest, int intRmIdx, String startDate, String endDate){
//        return bookingService.getPackageStock(dataType, httpServletRequest, intRmIdx, startDate, endDate);
//    }

    // 재고 등록 및 수정
    @GetMapping("getPackageStock")
    @ResponseBody
    public String getPackageStock(String dataType, HttpServletRequest httpServletRequest, int intRmIdx, String startDate, String endDate){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            Map<String, Object> idMap = wellihilliMapper.getStrRmtypeNAID(intRmIdx);
            String rmtypeID = idMap.get("strRmtypeID").toString();
            int intAID = Integer.parseInt(idMap.get("intAID").toString());

            List<Map<String, Object>> strMapCodeList = commonAcmMapper.getStrPkgCodeList(intRmIdx, startDate, endDate);

            int intFailCount = 0;
            for(Map map : strMapCodeList) {
                Map<String, Object> MapCodeMap = map;
                String strMapCode = MapCodeMap.get("strMapCode").toString();
                String strDateMapping = MapCodeMap.get("dateMapping").toString().replace("-","");

                intFailCount += bookingService.getPackageStock(intAID, intRmIdx, rmtypeID, strMapCode, strDateMapping);
            }

            if(intFailCount == 0){
                message = "재고 등록 및 수정 완료";
            }else{
                message = "재고 등록 및 수정 " + intFailCount + "건 실패";
            }
        }catch (Exception e){
            message = "재고 등록 및 수정 실패";
            statusCode = "500";
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
            e.printStackTrace();
        }

        logWriter.add(message);
        logWriter.log(0);

        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.makeReturn(dataType, statusCode, message);
    }

    // 체크인 날짜에 해당되는 객실 수량 및 계산된 총 요금 조회
    @GetMapping("getTotalPrice")
    @ResponseBody
    public String getTotalPrice(String dataType, String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        return bookingService.getTotalPrice(dataType, pyung, sDate, eDate, sleep, roomCount, roomType, pkgCode);
    }

    // 1박 이상일경우 일자별 요금 데이터 조회
    @GetMapping("getDayPrice")
    @ResponseBody
    public String getDayPrice(String dataType, String pyung, String sDate, String eDate, String sleep, String roomCount, String roomType, String pkgCode){
        return bookingService.getDayPrice(dataType, pyung, sDate, eDate, sleep, roomCount, roomType, pkgCode);
    }

    // 예약
    @GetMapping("createBooking")
    @ResponseBody
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.createBooking(dataType, intRsvID, httpServletRequest);
    }

    // 예약 취소
    @GetMapping("cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.cancelBooking(dataType, intRsvID, httpServletRequest);
    }

    // 예약 수정
    @GetMapping("updateBooking")
    @ResponseBody
    public String updateBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.updateBooking(dataType, intRsvID, httpServletRequest);
    }

    // 예약 상세 조회
    @GetMapping("getBookingInfo")
    @ResponseBody
    public String getBookingInfo(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.getBookingInfo(dataType, intRsvID, httpServletRequest);
    }

    // 예약 리스트 조회(체크인날짜 기준)
    // 예약번호, 예약자 이름으로 검색 가능
    @GetMapping("getBookingList")
    @ResponseBody
    public String getBookingList(String dataType, HttpServletRequest httpServletRequest,
                                   @Nullable String searchFlag, @Nullable String searchData, String sDate, String eDate,
                                   @Nullable String rsvFlag, String strPkgCode){
        return bookingService.getBookingList(dataType, httpServletRequest, searchFlag, searchData,
                                                sDate, eDate, rsvFlag, strPkgCode);
    }


}
