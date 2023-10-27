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
    public String getPackageList(@RequestParam(required = false, defaultValue="jsonp") String dataType,HttpServletRequest httpServletRequest){
        return  bookingService.getPackageList(dataType, httpServletRequest);
    }

    @GetMapping("/getPackageInfo")
    @ResponseBody
    public String getPackageInfo(@RequestParam(required = false, defaultValue="jsonp") String dataType,String pkgNo, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageInfo(dataType, pkgNo, httpServletRequest);
    }

    @GetMapping("/getPackageStock")
    @ResponseBody
    public String getPackageStatus(@RequestParam(required = false, defaultValue="jsonp") String dataType,String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageStatus(dataType, pkgNo, storeCd, sDate, rmTypeCd, ciYmd, httpServletRequest);
    }

    @GetMapping("/getPackagePrice")
    @ResponseBody
    public String getPackageAmount(@RequestParam(required = false, defaultValue="jsonp") String dataType,String pkgNo, String storeCd, String sDate, String rmTypeCd, String ciYmd, String nights, String rmCnt, HttpServletRequest httpServletRequest){
        return  bookingService.getPackageAmount(dataType, pkgNo, storeCd, sDate, rmTypeCd, ciYmd, nights, rmCnt, httpServletRequest);
    }

    @GetMapping("/createBookingPackage")
    @ResponseBody
    public String createBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID, @RequestParam(required = false, defaultValue = "")String rsvDate ,HttpServletRequest httpServletRequest){
        return  bookingService.createBooking(dataType, intRsvID, rsvDate ,httpServletRequest);
    }

    @GetMapping("/cancelBookingPackage")
    @ResponseBody
    public String cancelBooking(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID ,HttpServletRequest httpServletRequest){
        return  bookingService.cancelBooking(dataType, intRsvID ,httpServletRequest);
    }
    @GetMapping("/createBookingRoom")
    @ResponseBody
    public String createBookingRoom(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID, @RequestParam(required = false, defaultValue = "")String rsvDate ,HttpServletRequest httpServletRequest){
        return  bookingService.createBookingRoom(dataType, intRsvID, rsvDate ,httpServletRequest);
    }

    @GetMapping("/cancelBookingRoom")
    @ResponseBody
    public String cancelBookingRoom(@RequestParam(required = false, defaultValue="jsonp") String dataType, String intRsvID ,HttpServletRequest httpServletRequest){
        return  bookingService.cancelBookingRoom(dataType, intRsvID ,httpServletRequest);
    }

    @GetMapping("/getRoomList")
    @ResponseBody
    public String getRoomList(@RequestParam(required = false, defaultValue="jsonp") String dataType,HttpServletRequest httpServletRequest){
        return  bookingService.getRoomList(dataType, httpServletRequest);
    }

    @GetMapping("/getRoomPrice")
    @ResponseBody
    @CrossOrigin
    public String getRoomAmount(@RequestParam(required = false, defaultValue="jsonp") String dataType,HttpServletRequest httpServletRequest, String storeCd,String rmTypeCd, String ciYmd, String nights){
        return  bookingService.getRoomAmount(dataType, httpServletRequest, storeCd, rmTypeCd, ciYmd, nights);
    }



    @GetMapping("/getRoomStock")
    @ResponseBody
    public String getRoomStatus(@RequestParam(required = false, defaultValue="jsonp") String dataType,HttpServletRequest httpServletRequest, String storeCd, String sDate){
        return  bookingService.getRoomStatus(dataType, httpServletRequest, storeCd, sDate);
    }

    @GetMapping("/insertAccommInfo")
    @ResponseBody
    public String insertSONO(HttpServletRequest httpServletRequest) {
        return bookingService.insertSONO(httpServletRequest);
    }

    @GetMapping("/updateStock")
    @ResponseBody
    public String getStockAndInsert(@RequestParam(required = false, defaultValue="jsonp") String dataType,HttpServletRequest httpServletRequest) {
        return bookingService.getStockAndInsert(dataType, httpServletRequest);
    }

    @GetMapping("/getSettlement")
    @ResponseBody
    public String getSettlement(@RequestParam(required = false, defaultValue="jsonp") String dataType,String stndDt) {
        return bookingService.getSettlement(dataType, stndDt);
    }


}
