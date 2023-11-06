package com.example.stay.accommodation.hotelPass.controller;

import com.example.stay.accommodation.hotelPass.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("hotelPass.BookingController")
@RequestMapping("/hotelPass/booking/*")
public class BookingController {
    @Autowired
    BookingService bookingService;

    @GetMapping("/getCityRate")
    @ResponseBody
    public String getCityRate(@RequestParam(required = false, defaultValue="jsonp") String dataType, String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getCityRate(dataType, requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }

    @GetMapping("/getMultiRate")
    @ResponseBody
    public String getMultiRate(@RequestParam(required = false, defaultValue="jsonp") String dataType, String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getMultiRate(dataType, requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getServiceRate")
    @ResponseBody
    public String getServiceRate(@RequestParam(required = false, defaultValue="jsonp") String dataType, String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getServiceRate(dataType, requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getCancelPolicy")
    @ResponseBody
    public String getCancelPolicy(@RequestParam(required = false, defaultValue="jsonp") String dataType, String requestCode, String sDate, String eDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.getCancelPolicy(dataType, requestCode, sDate, eDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getBookingList")
    @ResponseBody
    public String getBookingList(@RequestParam(required = false, defaultValue="jsonp") String dataType, String agentID, String sDate, String eDate){
        return bookingService.getBookingList(dataType, agentID, sDate, eDate);
    }
    @GetMapping("/createBooking")
    @ResponseBody
    public String createBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, String requestCode, String startDate, String endDate, String roomCnt, String adultCnt, String childCnt){
        return bookingService.createBooking(dataType, requestCode, startDate, endDate, roomCnt, adultCnt, childCnt);
    }
    @GetMapping("/getBookingInfo")
    @ResponseBody
    public String getBookingDetail(@RequestParam(required = false, defaultValue="jsonp") String dataType, int intRsvID){
        return bookingService.getBookingDetail(dataType, intRsvID);
    }
    @GetMapping("/cancelBooking")
    @ResponseBody
    public String cancelBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, int intRsvID){
        return bookingService.cancelBooking(dataType, intRsvID);
    }
}
