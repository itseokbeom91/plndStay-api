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
    public void getPackageDetail(String packageCode){

        hanwhaService.getPackageDetail(packageCode);

    }

    @GetMapping("/createBooking")
    public void booking(int intRsvIdx){

        hanwhaService.booking(intRsvIdx);

    }

    @GetMapping("/cancelBooking")
    public void bookingCancel(){

        hanwhaService.bookingCancel();

    }

    @GetMapping("/updateBooking")
    public void bookingModify(String strRsrvNo, String strDate, String strRoomCnt, String strStaycnt, String strReserveName, String strReservePhone, String strStayName, String strStayPhone){

        hanwhaService.bookingModify(strRsrvNo, strDate, strRoomCnt, strStaycnt, strReserveName, strReservePhone, strStayName, strStayPhone);

    }

    @GetMapping("/getBookingInfo")
    public void bookingInfo(){

        hanwhaService.bookingInfo();

    }

}
