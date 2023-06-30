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
    public void getCapa(int intAID, int intRmIdx, int intPkgIdx, String startDate, String endDate){

        hanwhaService.getCapa(intAID, intRmIdx, intPkgIdx, startDate, endDate);

    }

    @GetMapping("/packageList")
    public void getPackageList(String accommId, String startDate){

        hanwhaService.getPackageList(accommId, startDate);

    }

    @GetMapping("/packageDetail")
    public void getPackageDetail(String packageCode){

        hanwhaService.getPackageDetail(packageCode);

    }

    @GetMapping("/booking")
    public void booking(String packNo, String locCd, String RMCd, String startDate, String roomCnt, String staycnt, String name, String phone){

        hanwhaService.booking(packNo, locCd, RMCd, startDate, roomCnt, staycnt, name, phone);

    }

    @GetMapping("/cancel")
    public void bookingCancel(){

        hanwhaService.bookingCancel();

    }

    @GetMapping("/bookingInfo")
    public void bookingInfo(){

        hanwhaService.bookingInfo();

    }

}
