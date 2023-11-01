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


    // 스케줄러 재고 가져오기
    @GetMapping("/getStock")
    @ResponseBody
    public String mappingCapa(){

        String result = hanwhaService.mappingCapa();
        System.out.println(result);

        return result;
    }

    @GetMapping("/getRoomStock")
    @ResponseBody
    public String getCapa(int intAID, int intRmIdx, String strPkgIdx, String strLocalCode, String startDate, String endDate, String dataType){

        String result = hanwhaService.getCapa(intAID, intRmIdx, strPkgIdx, strLocalCode, startDate, endDate, dataType);
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
    @ResponseBody
    public String getPackageDetail(String packageCode){

        String result = hanwhaService.getPackageDetail(packageCode);
        System.out.println(result);

        return result;
    }

    @GetMapping("/createBooking")
    @ResponseBody
    public String booking(int intRsvID, String dataType){

        String result = hanwhaService.booking(intRsvID, dataType);
        System.out.println(result);

        return result;
    }

    @GetMapping("/createBookingDate")
    @ResponseBody
    public String bookingByDate(int intRsvID, String strDate, String dataType){

        String result = hanwhaService.bookingByDate(intRsvID, strDate, dataType);
        System.out.println(result);

        return result;
    }

    @GetMapping("/cancelBooking")
    @ResponseBody
    public String bookingCancel(int intRsvID, String dataType){

        String result = hanwhaService.bookingCancel(intRsvID, dataType);
        System.out.println(result);

        return result;
    }

    @GetMapping("/updateBooking")
    @ResponseBody
    public String bookingModify(int intRsvID){

        String result = hanwhaService.bookingModify(intRsvID);
        System.out.println(result);

        return result;
    }

    @GetMapping("/getBookingInfo")
    @ResponseBody
    public String bookingInfo(){

        String result = hanwhaService.bookingInfo();
        System.out.println(result);

        return result;
    }

}
