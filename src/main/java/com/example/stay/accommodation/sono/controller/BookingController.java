package com.example.stay.accommodation.sono.controller;

import com.example.stay.accommodation.sono.service.BookingService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("sono.BookingController")
@RequestMapping("/sono/booking/*")
public class BookingController {

    @Autowired
    BookingService bookingService = new BookingService();


    @GetMapping("/getPackageList")
    @ResponseBody
    public String getPackageList(String dataType,HttpServletRequest httpServletRequest){
        return  bookingService.getPackageList(dataType, httpServletRequest);
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public String getPackageInfo(String dataType,String pkgNo, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageInfo(dataType, pkgNo, httpServletRequest);
    }

    @GetMapping("/getPackageStatus")
    @ResponseBody
    public String getPackageStatus(String dataType,String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate, rmTypeCd, ciYmd, httpServletRequest);
    }

    @GetMapping("/getPackageAmount")
    @ResponseBody
    public String getPackageAmount(String dataType,String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, String nights, String rmCnt, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd, ciYmd, nights, rmCnt, httpServletRequest);
    }

    @GetMapping("/reservation")
    @ResponseBody
    public String reservation(String dataType, String bookingIdx ,HttpServletRequest httpServletRequest){
        return  bookingService.reservation(dataType, bookingIdx ,httpServletRequest);
    }

    @GetMapping("/getRoomList")
    @ResponseBody
    public String getRoomList(String dataType,HttpServletRequest httpServletRequest){
        return  bookingService.getRoomList(dataType, httpServletRequest);
    }

    @GetMapping("/getRoomAmount")
    @ResponseBody
    @CrossOrigin
    public String getRoomAmount(String dataType,HttpServletRequest httpServletRequest, String storeCd, String sMonth){
        return  bookingService.getRoomAmount(dataType, httpServletRequest, storeCd, sMonth);
    }



    @GetMapping("/getRoomStatus")
    @ResponseBody
    public String getRoomStatus(String dataType,HttpServletRequest httpServletRequest, String storeCd, String sDate){
        return  bookingService.getRoomStatus(dataType, httpServletRequest, storeCd, sDate);
    }

    @GetMapping("/insertSONO")
    @ResponseBody
    public String insertSONO(HttpServletRequest httpServletRequest) {
        return bookingService.insertSONO(httpServletRequest);
    }

    @GetMapping("/stockResult")
    @ResponseBody
    public String getStockAndInsert(String dataType,HttpServletRequest httpServletRequest) {
        return bookingService.getStockAndInsert(dataType, httpServletRequest);
    }

    @GetMapping("/settlement")
    @ResponseBody
    public String getSettlement(String dataType,String stndDt) {
        return bookingService.getSettlement(dataType, stndDt);
    }

}
