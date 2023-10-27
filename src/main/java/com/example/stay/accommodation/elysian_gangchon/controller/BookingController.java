package com.example.stay.accommodation.elysian_gangchon.controller;

import com.example.stay.accommodation.elysian_gangchon.mapper.ElysianMapper;
import com.example.stay.accommodation.elysian_gangchon.service.BookingService;
import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.LogWriter;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("elysian_gangchon.BookingController")
@RequestMapping("/elysian_gangchon/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ElysianMapper elysianMapper;

    @Autowired
    private CommonAcmMapper commonAcmMapper;


//    // 예약 가능 수량 조회(재고 등록 및 수정)
//    @RequestMapping("updatePackageStock")
//    @ResponseBody
//    public String updatePackagetock(String dataType, HttpServletRequest httpServletRequest, String startDate, String endDate, int intRmIdx){
//        return bookingService.updatePackageStock(dataType, httpServletRequest, startDate, endDate, intRmIdx);
//    }

    // 예약 가능 수량 조회(재고 등록 및 수정) - 비동기
    @RequestMapping("updatePackageStock")
    @ResponseBody
    public String updatePackagetock(String dataType, HttpServletRequest httpServletRequest, String startDate, String endDate, int intRmIdx){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            int intAID = elysianMapper.getIntAID(intRmIdx);
            List<Map<String, Object>> strMapCodeList = commonAcmMapper.getStrPkgCodeList(intRmIdx, startDate, endDate);

            int intFailCount = 0;
            for(Map map : strMapCodeList) {
                Map<String, Object> MapCodeMap = map;
                String strMapCode = MapCodeMap.get("strMapCode").toString();
                String strDateMapping = MapCodeMap.get("dateMapping").toString();

                intFailCount += bookingService.getAvailCount(intAID, intRmIdx, strMapCode, strDateMapping);
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

    // 예약
    @RequestMapping("createBooking")
    @ResponseBody
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.createBooking(dataType, intRsvID, httpServletRequest);
    }

    // 예약 조회
    @RequestMapping("getBookingInfo")
    @ResponseBody
    public String getBookingInfo(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.getBookingInfo(dataType, intRsvID, httpServletRequest);
    }

    // 예약 취소
    @RequestMapping("cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return bookingService.cancelBooking(dataType, intRsvID, httpServletRequest);
    }

    // 예약 가능여부 조회
    @RequestMapping("checkAvailBooking")
    @ResponseBody
    public void checkAvailBooking(String pcode, String pcode_seq, String sdate, int cnt){
        bookingService.checkAvailBooking(pcode, pcode_seq, sdate, cnt);
    }

    // 예약 - 날짜로
    @RequestMapping("createBookingByDate")
    @ResponseBody
    public String createBookingByDate(String dataType, int intRsvID, String startDate, HttpServletRequest httpServletRequest){
        return bookingService.createBookingByDate(dataType, intRsvID, startDate, httpServletRequest);
    }
}
