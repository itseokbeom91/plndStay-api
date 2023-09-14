package com.example.stay.accommodation.elysian_gangchon.controller;

import com.example.stay.accommodation.elysian_gangchon.service.BookingService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("elysian_gangchon.BookingController")
@RequestMapping("/elysian_gangchon/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;


    // 재고 등록 및 수정
    @RequestMapping("updatePackagetock")
    @ResponseBody
    public String updatePackagetock(String dataType, HttpServletRequest httpServletRequest, String startDate, String endDate, int intRmIdx){
        return bookingService.updatePackagetock(dataType, httpServletRequest, startDate, endDate, intRmIdx);
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
}
