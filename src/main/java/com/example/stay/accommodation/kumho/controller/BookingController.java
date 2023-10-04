package com.example.stay.accommodation.kumho.controller;

import com.example.stay.accommodation.kumho.mapper.KumhoMapper;
import com.example.stay.accommodation.kumho.service.BookingService;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.LogWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller("kumho.BookingController")
@RequestMapping("/kumho/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private KumhoMapper kumhoMapper;

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

    /**
     * 재고 등록 및 수정
     */
    @GetMapping("updateRoomStock")
    @ResponseBody
    public String updateRoomStock(String dataType, String startDate, String endDate, int intRmIdx, HttpServletRequest httpServletRequest){
        return bookingService.updateRoomStock(dataType, startDate, endDate, intRmIdx, httpServletRequest);
    }

}
