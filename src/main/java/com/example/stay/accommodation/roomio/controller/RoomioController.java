package com.example.stay.accommodation.roomio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roomio/*")
public class RoomioController {

    @GetMapping("/getAccomm")
    public void getAccomm(){}

    @GetMapping("/getRoom")
    public void getRoom(){}

    @GetMapping("/bookingState")
    public void bookingState(){}

    @GetMapping("/getPrice")
    public void getPrice(){}

    @GetMapping("/booking")
    public void booking(){}

    @GetMapping("/bookingInfo")
    public void bookingInfo(){}

    @GetMapping("/bookingList")
    public void bookingList(){}

    @GetMapping("/cancel")
    public void bookingCancel(){}

}
