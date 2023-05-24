package com.example.stay.accommodation.onda.controller;

import com.example.stay.accommodation.onda.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/onda/booking/*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    /**
     * 예약
     */
    @GetMapping("bookingRegist")
    public void reservation(String orderID){
        // 우리 DB에서 예약정보를 가져와서 온다에 보내기 -> ORDER_ID로



    }

//    @GetMapping("test")
//    public void test(){
//        String propertyId = "130517";
//        String roomTypeId = "1459423";
//        String ratePlanId = "1592553";
//        String checkInDate = "2023-05-24";
//        String checkOutDate = "2023-06-01";
//
//        bookingService.checkAvailBooking(propertyId, roomTypeId, ratePlanId, checkInDate, checkOutDate);
//
//    }

    @GetMapping("createBooking")
    public void createBooking(int intBookingID){
        bookingService.createBookingInfo(intBookingID);

    }

}
