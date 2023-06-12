package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.BookingService;
import com.example.stay.common.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Controller
@RequestMapping("/onda/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 예약 생성
     */
    @GetMapping("createBooking")
    @ResponseBody
    public ResponseResult createBooking(int intBookingID, HttpServletRequest httpServletRequest) {

        ResponseResult responseResult = bookingService.createBookingInfo(intBookingID, httpServletRequest);
        return responseResult;
    }

    /**
     * 예약 취소
     */
    @GetMapping("cancelBooking")
    @ResponseBody
    public ResponseResult cancelBooking(int intBookingID, HttpServletRequest httpServletRequest) {

        ResponseResult responseResult = bookingService.cancelBookingInfo(intBookingID, httpServletRequest);
        return responseResult;
    }

    @GetMapping("getCancelPolicy")
    @ResponseBody
    public void getCancelPolicy(String propertyId, String roomTypeId, String ratePlanId,
                                String strCheckInDate, String strCheckOutDate, HttpServletRequest httpServletRequest){
        bookingService.getCancelPolicy(propertyId, roomTypeId, ratePlanId, strCheckInDate, strCheckOutDate, httpServletRequest);
    }

}
