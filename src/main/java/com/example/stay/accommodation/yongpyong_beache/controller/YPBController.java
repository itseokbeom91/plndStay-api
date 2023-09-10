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

    @GetMapping("/getRoomStock")
    public void getStock(int intAID, int intRmIdx, int intPkgIdx, String startDate, String endDate){

        ypbService.getStock(intAID, intRmIdx, intPkgIdx, startDate, endDate);

    }

    @GetMapping("/createBooking")
    public void booking(){

        ypbService.booking();

    }

    @GetMapping("/getBookingInfo")
    public void getBookingInfo(){

        ypbService.getBookingInfo();

    }

    @GetMapping("/cancelBooking")
    public void bookingCancel(){

        ypbService.bookingCancel();
    }

    @GetMapping("/getBookingList")
    public void bookingList(){

        ypbService.getBookingList();

    }
}
