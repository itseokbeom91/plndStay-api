package com.example.stay.accommodation.wellihilli.controller;

import com.example.stay.accommodation.wellihilli.service.AccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller("wellihilli.AccommController")
@RequestMapping("/wellihilli/accomm/*")
public class AccommController {

    @Autowired
    private AccommService accommService;

    @GetMapping("insertRoomType")
    @ResponseBody
    public String insertRoomType(HttpServletRequest httpServletRequest){
        return accommService.insertRoomType(httpServletRequest);
    }

}
