package com.example.stay.openMarket.coupang.controller;

import com.example.stay.openMarket.coupang.service.CpBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/coupang/booking/*")
public class CpBookingController {
    @Autowired
    private CpBookingService cpBookingService;

    /**
     * 예약 목록 조회
     */
    @GetMapping("/getBookingList")
    @ResponseBody
    public String getBookingList(String dataType, @Nullable String strStartDate, @Nullable String strEndDate,
                                 HttpServletRequest httpServletRequest){
        return cpBookingService.getBookingList(dataType, strStartDate, strEndDate, httpServletRequest);
    }

    /**
     * 예약 단일 조회
     */
    @GetMapping("/getBooking")
    @ResponseBody
    public String getBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return cpBookingService.getBooking(dataType, intRsvID, httpServletRequest);
    }
}
