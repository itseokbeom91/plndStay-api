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

    @GetMapping("/getAccommInfo")
    public void getAccomm(String dataType){

        roomioService.getAccomm(dataType);

    }

    @GetMapping("/getRoomInfo")
    public void getRoom(String hotelId){

        roomioService.getRoom(hotelId);

    }

    @GetMapping("/getBookingState")
    public void bookingState(){}

    @GetMapping("/getRoomStock")
    public void getStock(int intAID, int intRmIdx, String startDate, String endDate, String dataType){

        roomioService.getStock(intAID, intRmIdx, startDate, endDate, dataType);

    }

    @GetMapping("/createBooking")
    public void booking(int intRsvID){

        roomioService.booking(intRsvID);
    }

    @GetMapping("/getBookingInfo")
    public void bookingInfo(){}

    @GetMapping("/getBookingList")
    public void bookingList(){}

    @GetMapping("/cancelBooking")
    public void bookingCancel(){}

}
