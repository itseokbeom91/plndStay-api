package com.example.stay.accommodation.gpension.controller;

import com.example.stay.accommodation.gpension.service.AccommService;
import com.example.stay.common.util.CommonFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("gpension.AccommController")
@RequestMapping("/gp/accomm/*")
public class AccommController {

    @Autowired
    AccommService accommService = new AccommService();

    @GetMapping("/getPensionList")
    @ResponseBody
    public String getPensionList(){
        return accommService.getPensionList();
    }

    @GetMapping("/getPensionInfo")
    @ResponseBody
    public String getPensionInfo(String pensionId){
        return accommService.getPensionInfo(pensionId);
    }

    @GetMapping("/getPensionStatus")
    @ResponseBody
    public String getPensionStatus(String pensionId, String sDate, String eDate){
        return accommService.getPensionStatus(pensionId, sDate, eDate);
    }

    @GetMapping("/getPensionDailyInfo")
    @ResponseBody
    public String getPensionDailyInfo(String pensionId, String sDate, String eDate){
        return accommService.getPensionDailyInfo(pensionId, sDate, eDate);
    }

    @GetMapping("/getPensionMainList")
    @ResponseBody
    public String getPensionMainList(){
        return accommService.getPensionMainList();
    }

    @GetMapping("/getRoomInfo")
    @ResponseBody
    public String getRoomInfo(String pensionId, String roomId){
        return accommService.getRoomInfo(pensionId, roomId);
    }

    @GetMapping("/getRoomPriceInfo")
    @ResponseBody
    public String getRoomPriceInfo(String pensionId){
        return accommService.getRoomPriceInfo(pensionId);
    }

    @GetMapping("/getPensionModList")
    @ResponseBody
    public String getPensionModList(String lastDate){
        return accommService.getPensionModList(lastDate);
    }

    @GetMapping("/insertGP")
    @ResponseBody
    public String insertAccomm(){
        return accommService.insertGP();
    }

    @GetMapping("/testAdd")
    @ResponseBody
    public String testAdd(String address){
        CommonFunction commonFunction = new CommonFunction();
        return commonFunction.getNewAddressCodeByAddress(address);
    }
}
