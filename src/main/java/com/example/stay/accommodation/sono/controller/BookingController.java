package com.example.stay.accommodation.sono.controller;

import com.example.stay.accommodation.sono.service.BookingService;
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
    public String getPackageList(HttpServletRequest httpServletRequest){
        return  bookingService.getPackageList(httpServletRequest);
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public String getPackageInfo(String pkgNo, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageInfo(pkgNo, httpServletRequest);
    }

    @GetMapping("/getPackageStatus")
    @ResponseBody
    public String getPackageStatus(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageStatus(pkgNo, storeCd, sDate, rmTypeCd, ciYmd, httpServletRequest);
    }

    @GetMapping("/getPackageAmount")
    @ResponseBody
    public String getPackageAmount(String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, String nights, String rmCnt, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageAmount(pkgNo, storeCd, sDate, rmTypeCd, ciYmd, nights, rmCnt, httpServletRequest);
    }

    @GetMapping("/reservation")
    @ResponseBody
    public String reservation(String pkgNo, String storeCd, String ciYmd, String rmTypeCd, String comRsvNo, String userName, String userTel, String payAmt, String adultCnt, String childCnt ,HttpServletRequest httpServletRequest){
        return  bookingService.reservation(pkgNo, storeCd, ciYmd, rmTypeCd, comRsvNo, userName, userTel, payAmt, adultCnt, childCnt ,httpServletRequest);
    }

    @GetMapping("/getRoomList")
    @ResponseBody
    public String getRoomList(HttpServletRequest httpServletRequest){
        return  bookingService.getRoomList(httpServletRequest);
    }

    @GetMapping("/getRoomAmount")
    @ResponseBody
    @CrossOrigin
    public String getRoomAmount(HttpServletRequest httpServletRequest, String storeCd, String sMonth){
        return  bookingService.getRoomAmount(httpServletRequest, storeCd, sMonth);
    }



    @GetMapping("/getRoomStatus")
    @ResponseBody
    public String getRoomStatus(HttpServletRequest httpServletRequest, String storeCd, String sDate){
        return  bookingService.getRoomStatus(httpServletRequest, storeCd, sDate);
    }

    @GetMapping("/insertSONO")
    @ResponseBody
    public String insertSONO(HttpServletRequest httpServletRequest) {
        return bookingService.insertSONO(httpServletRequest);
    }

    @GetMapping("/stockResult")
    @ResponseBody
    public String getStockAndInsert(HttpServletRequest httpServletRequest) {
        return bookingService.getStockAndInsert(httpServletRequest);
    }

    @GetMapping("/settlement")
    @ResponseBody
    public String getSettlement(String stndDt) {
        return bookingService.getSettlement(stndDt);
    }

}
