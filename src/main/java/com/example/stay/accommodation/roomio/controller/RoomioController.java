package com.example.stay.accommodation.roomio.controller;

import com.example.stay.accommodation.roomio.service.RoomioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/roomio/*")
public class RoomioController {

    @Autowired
    RoomioService roomioService;

    @GetMapping("/getAccomm")
    public void getAccomm(String dataType){

        roomioService.getAccomm(dataType);

    }

    @GetMapping("/getRoom")
    public void getRoom(String hotelId){

        roomioService.getRoom(hotelId);

    }

    @GetMapping("/bookingState")
    public void bookingState(){}

    @GetMapping("/getPrice")
    public void getPrice(String strHotelId, String strRoomId, String startDate, String endDate, String dataType){

        roomioService.getPrice(strHotelId, strRoomId, startDate, endDate, dataType);

    }

    @GetMapping("/booking")
    public void booking(int intRsvID){

        roomioService.booking(intRsvID);
    }

    @GetMapping("/bookingInfo")
    public void bookingInfo(){}

    @GetMapping("/bookingList")
    public void bookingList(){}

    @GetMapping("/cancel")
    public void bookingCancel(){}

}
