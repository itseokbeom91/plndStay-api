package com.example.stay.accommodation.hanwha.controller;

import com.example.stay.accommodation.hanwha.service.HanwhaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/hanwha/*")
public class HanwhaController {

    @Autowired
    private HanwhaService hanwhaService;

    @GetMapping("/capa")
    public void getCapa(int intAID, int intRmIdx, String intPkgIdx, String strLocalCode, String startDate, String endDate){

        String result = hanwhaService.getCapa(intAID, intRmIdx, intPkgIdx, strLocalCode, startDate, endDate);
        System.out.println(result);

    }

    @GetMapping("/packageList")
    public void getPackageList(String localCode, String strDate, String resultType){

        String result = hanwhaService.getPackageList(localCode, strDate, resultType);
        System.out.println(result);

    }

    @GetMapping("/packageDetail")
    public void getPackageDetail(String packageCode){

        hanwhaService.getPackageDetail(packageCode);

    }

    @GetMapping("/booking")
    public void booking(String packNo, String locCd, String RMCd, String startDate, String roomCnt, String staycnt, String reserveName, String reservePhone, String stayName, String stayPhone){

        hanwhaService.booking(packNo, locCd, RMCd, startDate, roomCnt, staycnt, reserveName, reservePhone, stayName, stayPhone);

    }

    @GetMapping("/bookingCancel")
    public void bookingCancel(){

        hanwhaService.bookingCancel();

    }

    @GetMapping("/bookingUpdate")
    public void bookingModify(String strRsrvNo, String strDate, String strRoomCnt, String strStaycnt, String strReserveName, String strReservePhone, String strStayName, String strStayPhone){

        hanwhaService.bookingModify(strRsrvNo, strDate, strRoomCnt, strStaycnt, strReserveName, strReservePhone, strStayName, strStayPhone);

    }

    @GetMapping("/bookingInfo")
    public void bookingInfo(){

        hanwhaService.bookingInfo();

    }

}
