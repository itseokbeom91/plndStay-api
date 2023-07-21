package com.example.stay.accommodation.hotelPass.controller;

import com.example.stay.accommodation.hotelPass.service.AccommService;
import com.example.stay.common.util.CommonFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("hotelPass.AccommController")
@RequestMapping("/hotelPass/accomm/*")
public class AccomController {
    @Autowired
    AccommService accommService;


    @GetMapping("/getHotelList")
    @ResponseBody
    public String getHotelList(){
        return accommService.getHotelList();

    }

    @GetMapping("/getFacilityList")
    @ResponseBody
    public String getFacilityList(){
        return accommService.getFacilityList();
    }

}
