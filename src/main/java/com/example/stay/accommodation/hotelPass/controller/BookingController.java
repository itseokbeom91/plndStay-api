package com.example.stay.accommodation.hotelPass.controller;

import com.example.stay.accommodation.hotelPass.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("hotelPass.BookingController")
@RequestMapping("/hotelPass/booking/*")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @GetMapping("/getCityRate")
    @ResponseBody
    public String getCityRate(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getCityRate(requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }

    @GetMapping("/getMultiRate")
    @ResponseBody
    public String getMultiRate(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getMultiRate(requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
}
