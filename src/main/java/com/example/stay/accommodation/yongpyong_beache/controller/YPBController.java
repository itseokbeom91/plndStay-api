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
    public String getStock(int intAID, int intRmIdx, int intPkgIdx, String startDate, String endDate){

        String result = ypbService.getStock(intAID, intRmIdx, intPkgIdx, startDate, endDate);
        System.out.println(result);

        return result;
    }

    @GetMapping("/createBooking")
    public String booking(int intRsvID){

        String result = ypbService.booking(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/getBookingInfo")
    public String getBookingInfo(int intRsvID){

        String result =ypbService.getBookingInfo(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/cancelBooking")
    public String bookingCancel(int intRsvID){

        String result =ypbService.bookingCancel(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/getBookingList")
    public String bookingList(){

        String result = ypbService.getBookingList();
        System.out.println(result);

        return result;
    }
}
