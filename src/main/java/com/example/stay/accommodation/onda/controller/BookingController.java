package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller("onda.BookingController")
@RequestMapping("/onda/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

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


    @GetMapping("getCancelPolicy")
    @ResponseBody
    public void getCancelPolicy(String strPropertyID, String roomTypeId, String ratePlanId,
                                String strCheckInDate, String strCheckOutDate, HttpServletRequest httpServletRequest){
        bookingService.getCancelPolicy(strPropertyID, roomTypeId, ratePlanId, strCheckInDate, strCheckOutDate, httpServletRequest);
    }

    /**
     * 예약 대사자료 조회
     */
    @GetMapping("getBookingList")
    @ResponseBody
    public String getBookingList(String dataType, String option, String strFrom, String strTo, HttpServletRequest httpServletRequest){
        return bookingService.getBookingList(dataType, option, strFrom, strTo, httpServletRequest);
    }
}
