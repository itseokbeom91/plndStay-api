package com.example.stay.accommodation.hanwha.controller;

import com.example.stay.accommodation.hanwha.service.HanwhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/hanwha/*")
public class HanwhaController {

    @Autowired
    private HanwhaService hanwhaService;

    @GetMapping("/getRoomStock")
    @ResponseBody
    public String getCapa(int intAID, int intRmIdx, String intPkgIdx, String strLocalCode, String startDate, String endDate, String dataType){

        String result = hanwhaService.getCapa(intAID, intRmIdx, intPkgIdx, strLocalCode, startDate, endDate, dataType);
        System.out.println(result);

        return result;
    }

    @GetMapping("/getPackageList")
    @ResponseBody
    public String getPackageList(@Nullable String localCode, String strDate, String dataType){

        String result = hanwhaService.getPackageList(localCode, strDate, dataType);
        System.out.println(result);

        return result;
    }

    @GetMapping("/getPackageInfo")
    public String getPackageDetail(String packageCode){

        String result = hanwhaService.getPackageDetail(packageCode);
        System.out.println(result);

        return result;
    }

    @GetMapping("/createBooking")
    public String booking(int intRsvID){

        String result = hanwhaService.booking(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/cancelBooking")
    public String bookingCancel(int intRsvID){

        String result = hanwhaService.bookingCancel(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/updateBooking")
    public String bookingModify(int intRsvID){

        String result = hanwhaService.bookingModify(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/getBookingInfo")
    public String bookingInfo(){

        String result = hanwhaService.bookingInfo();
        System.out.println(result);

        return result;
    }

}
