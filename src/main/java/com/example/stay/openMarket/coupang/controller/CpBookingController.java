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

    /**
     * 예약 확정
     */
    @GetMapping("/confirmBooking")
    @ResponseBody
    public String confirmBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return cpBookingService.confirmBooking(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 예약 불가 처리(예약 대기중인 티켓 상태를 예약불가로 변경 -> 자동으로 취소 완료 처리됨)
     */
    @GetMapping("/rejectBooking")
    @ResponseBody
    public String rejectBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return cpBookingService.rejectBooking(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 예약 취소 승인(취소 대기중인 티켓 상태를 취소 승인으로 변경 -> 자동으로 취소 완료 처리됨)
     */
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return cpBookingService.cancelBooking(dataType, intRsvID, httpServletRequest);
    }

    /**
     * 주문 상태 이력 조회(예약대기, 예약 확정을 조회 가능)
     */
    @GetMapping("/getBookingStatus")
    @ResponseBody
    public String getBookingStatus(String dataType, int intRsvID, HttpServletRequest httpServletRequest){
        return cpBookingService.getBookingStatus(dataType, intRsvID, httpServletRequest);
    }
}
