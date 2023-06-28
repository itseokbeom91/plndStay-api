package com.example.stay.accommodation.yongpyong_beache.controller;

import com.example.stay.accommodation.yongpyong_beache.service.YPBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/YPB/*")
public class YPBController {

    @Autowired
    private YPBService ypbService;

    @GetMapping("/stock")
    public void getStock(String proertyId, String lcdCode, String roomTypeCode, String startDate, String endDate, String packageCode){

        ypbService.getStock(proertyId, lcdCode, roomTypeCode, startDate, endDate, packageCode);

    }

    @GetMapping("/booking")
    public void booking(){

        ypbService.booking();

    }

    @GetMapping("/bookingInfo")
    public void getBookingInfo(){

        ypbService.getBookingInfo();

    }

    @GetMapping("/cancel")
    public void bookingCancel(){

        ypbService.bookingCancel();
    }

    @GetMapping("/bookingList")
    public void bookingList(){

        ypbService.getBookingList();

    }
}
