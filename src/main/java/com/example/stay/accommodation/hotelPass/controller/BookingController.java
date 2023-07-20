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
    @GetMapping("/getServiceRate")
    @ResponseBody
    public String getServiceRate(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getServiceRate(requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getCancelPolicy")
    @ResponseBody
    public String getCancelPolicy(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getCancelPolicy(requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getBookingList")
    @ResponseBody
    public String getBookingList(String agentID, String sDate, String eDate){
        return bookingService.getBookingList(agentID, sDate, eDate);
    }
    @GetMapping("/createBooking")
    @ResponseBody
    public String createBooking(String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.createBooking(requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getBookingDetail")
    @ResponseBody
    public String getBookingDetail(String bookingNo){
        return bookingService.getBookingDetail(bookingNo);
    }
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(String bookingNo){
        return bookingService.cancelBooking(bookingNo);
    }
}
