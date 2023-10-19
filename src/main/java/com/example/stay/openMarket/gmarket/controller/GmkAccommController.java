package com.example.stay.openMarket.gmarket.controller;

import com.example.stay.openMarket.gmarket.hmac.HmacGenerater;
import com.example.stay.openMarket.gmarket.service.GmkAccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Controller
@RequestMapping("/gmk/accomm/*")
public class GmkAccommController {

    @Autowired
    private GmkAccommService gmkAccommService;

    /**
     * 숙박상품 생성
     */
    @GetMapping("/createAccomm")
    @ResponseBody
    public String createAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.createAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 숙박상품 수정
     */
    @GetMapping("/updateAccomm")
    @ResponseBody
    public String updateAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updateAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 숙박상품 삭제
     */
    @GetMapping("/deleteAccomm")
    @ResponseBody
    public String deleteAccomm(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.deleteAccomm(dataType, intAID, httpServletRequest);
    }

    /**
     * 가격/재고/판매상태 수정
     */
    @GetMapping("/updatePriceStockStatus")
    @ResponseBody
    public String updatePriceStockStatus(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updatePriceStockStatus(dataType, intAID, httpServletRequest);
    }

    /**
     * 상품명 수정
     */
    @GetMapping("/updateProductName")
    @ResponseBody
    public String updateProductName(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updateProductName(dataType, intAID, httpServletRequest);
    }

    /**
     * 상품 이미지 수정
     */
    @GetMapping("/updateAccommImages")
    @ResponseBody
    public String updateAccommImages(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updateAccommImages(dataType, intAID, httpServletRequest);
    }

    /**
     * 상품 상세설명 수정
     */
    @GetMapping("/updateAccommDesc")
    @ResponseBody
    public String updateAccommDesc(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updateAccommDesc(dataType, intAID, httpServletRequest);
    }

    /**
     * 옵션 등록/수정
     */
    @GetMapping("/updateAccommOption")
    @ResponseBody
    public String updateAccommOption(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updateAccommOption(dataType, intAID, httpServletRequest);
    }










    @GetMapping("/testHmac")
    public void testHmac(){
        HmacGenerater.generate("", "");
    }

    @GetMapping("/getPriceNStock")
    public String getPriceNStock(HttpServletRequest httpServletRequest){
        return gmkAccommService.getPriceNStock(httpServletRequest);
    }

//    @GetMapping("/getRecommendOpts")
//    public void getRecommendOpts(){
//        gmkAccommService.getRecommendOpts();
//    }

    @GetMapping("/getCategory")
    public String getCategorty(){
        return gmkAccommService.getCategory();
    }






    // 가격/재고/판매상태 조회
    @GetMapping("/getPriceStockStatus")
    @ResponseBody
    public String getPriceStockStatus(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.getPriceStockStatus(dataType, intAID, httpServletRequest);
    }

    // 숙박상품 조회
    @GetMapping("/getAccommInfo")
    @ResponseBody
    public String getAccommInfo(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.getAccommInfo(dataType, intAID, httpServletRequest);
    }

    // 옵션 조회
    @GetMapping("/getAccommOption")
    @ResponseBody
    public String getAccommOption(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.getAccommOption(dataType, intAID, httpServletRequest);
    }

    // 판매자할인(사이트별 판매자부담 할인) 등록/수정
    @GetMapping("/updateSellerDiscount")
    @ResponseBody
    public String updateSellerDiscount(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.updateSellerDiscount(dataType, intAID, httpServletRequest);
    }

    // 판매자할인(사이트별 판매자부담 할인) 해제
    @GetMapping("/deleteSellerDiscount")
    @ResponseBody
    public String deleteSellerDiscount(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.deleteSellerDiscount(dataType, intAID, httpServletRequest);
    }

    // 판매자할인(사이트별 판매자부담 할인) 조회
    @GetMapping("/getSellerDiscount")
    @ResponseBody
    public String getSellerDiscount(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.getSellerDiscount(dataType, intAID, httpServletRequest);
    }



}
