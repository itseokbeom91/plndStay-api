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

    @GetMapping("/capa")
    @ResponseBody
    public String getCapa(int intAID, int intRmIdx, String intPkgIdx, String strLocalCode, String startDate, String endDate, String dataType){

        String result = hanwhaService.getCapa(intAID, intRmIdx, intPkgIdx, strLocalCode, startDate, endDate, dataType);
        System.out.println(result);

        return result;

    }

    @GetMapping("/packageList")
    @ResponseBody
    public String getPackageList(@Nullable String localCode, String strDate, String dataType){

        String result = hanwhaService.getPackageList(localCode, strDate, dataType);
        System.out.println(result);

        return result;

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
