package com.example.stay.accommodation.gpension.controller;

import com.example.stay.accommodation.gpension.service.AccommService;
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

    @GetMapping("/gpList")
    @ResponseBody
    public String getPensionList(){
        return accommService.getPensionList();
    }

    @GetMapping("/gpInfo")
    @ResponseBody
    public String getPensionInfo(String pensionId){
        return accommService.getPensionInfo(pensionId);
    }

    @GetMapping("/gpStatus")
    @ResponseBody
    public String getPensionStatus(String pensionId, String sDate, String eDate){
        return accommService.getPensionStatus(pensionId, sDate, eDate);
    }
}
