package com.example.stay.accommodation.onda.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/onda/booking/*")
public class BookingController {

    /**
     * 예약
     */
    @GetMapping("bookingRegist")
    public void reservation(String orderID){
        // 우리 DB에서 예약정보를 가져와서 온다에 보내기 -> ORDER_ID로
        


    }
}
