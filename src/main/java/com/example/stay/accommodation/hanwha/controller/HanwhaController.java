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
    public void getCapa(String accommId, String roomTypeId, String startDate, String endDate){
        hanwhaService.getCapa(accommId, roomTypeId, startDate, endDate);
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
    public void booking(){
        hanwhaService.booking();
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
