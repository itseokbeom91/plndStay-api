package com.example.stay.accommodation.kumho.controller;

import com.example.stay.accommodation.kumho.mapper.KumhoMapper;
import com.example.stay.accommodation.kumho.service.BookingService;
import com.example.stay.common.mapper.CommonAcmMapper;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.LogWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller("kumho.BookingController")
@RequestMapping("/kumho/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private KumhoMapper kumhoMapper;

    @Autowired
    private CommonAcmMapper commonAcmMapper;

    /**
     * 예약 생성
     */
    @GetMapping("createBooking")
    @ResponseBody
    public String createBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        return bookingService.createBooking(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 예약 취소
     */
    @GetMapping("cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        return bookingService.cancelBooking(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 예약 현황 조회
     */
    @GetMapping("getBookingInfo")
    @ResponseBody
    public String getBookingInfo(String dataType, int intRsvID, HttpServletRequest httpServletRequest) {
        return bookingService.getBookingInfo(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 예약 대사자료 조회
     */
    @GetMapping("getBookingList")
    @ResponseBody
    public String getBookingList(String dataType, String startDate, String endDate, HttpServletRequest httpServletRequest){
        return bookingService.getBookingList(dataType, startDate, endDate, httpServletRequest);
    }

//    /**
//     * 재고 등록 및 수정
//     */
//    @GetMapping("updateRoomStock")
//    @ResponseBody
//    public String updateRoomStock(String dataType, String startDate, String endDate, int intRmIdx, HttpServletRequest httpServletRequest){
//        return bookingService.updateRoomStock(dataType, startDate, endDate, intRmIdx, httpServletRequest);
//    }


    /**
     * 재고 등록 및 수정
     */
    @GetMapping("updateRoomStock")
    @ResponseBody
    public String updateRoomStock(String dataType, String startDate, String endDate, int intRmIdx, HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String statusCode = "200";
        String message = "";

        try{
            Map<String, Object> idMap = kumhoMapper.getRmtypeInfo(intRmIdx);
            String strRmtypeID = idMap.get("strRmtypeID").toString();
            int intAID = Integer.parseInt(idMap.get("intAID").toString());
            String strLocalCode = idMap.get("strLocalCode").toString();

            List<Map<String, Object>> strMapCodeList = commonAcmMapper.getStrPkgCodeList(intRmIdx, startDate, endDate);

            int intFailCount = 0;
            for(Map map : strMapCodeList) {
                Map<String, Object> MapCodeMap = map;
                String strDateMapping = MapCodeMap.get("dateMapping").toString().replace("-","");

                intFailCount += bookingService.updateRoomStock(intAID, intRmIdx, strLocalCode, strRmtypeID, strDateMapping);
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
}
