package com.example.stay.openMarket.eland.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/eland/goods/*")
public class GoodsController {

    @GetMapping("updateGoodsDtl")
    public void updateGoodsDtl(){

    }
}
