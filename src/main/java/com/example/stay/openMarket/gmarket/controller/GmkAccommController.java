package com.example.stay.openMarket.gmarket.controller;

import com.example.stay.openMarket.gmarket.GmkUtil.HmacGenerator;
import com.example.stay.openMarket.gmarket.service.GmkAccommService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

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




//    @GetMapping("/testHmac")
//    public void testHmac(){
//        HmacGenerator.generate("");
//    }
//
//    @GetMapping("/getPriceNStock")
//    public String getPriceNStock(HttpServletRequest httpServletRequest){
//        return gmkAccommService.getPriceNStock(httpServletRequest);
//    }


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

    // 상품번호 조회 - 마스터번호 기준으로 site번호 조회
    @GetMapping("/getOmkSiteCode")
    @ResponseBody
    public String getOmkSiteCode(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.getOmkSiteCode(dataType, intAID, httpServletRequest);
    }

    // 상품번호 조회 - site번호 기준으로 마스터번호 조회
    @GetMapping("/getPdtCode")
    @ResponseBody
    public String getPdtCode(String dataType, int intAID, HttpServletRequest httpServletRequest){
        return gmkAccommService.getPdtCode(dataType, intAID, httpServletRequest);
    }








    // 지마켓 카테고리 조회
    @GetMapping("/getGmkCategory")
    @ResponseBody
    public String getGmkCategory(){
        return gmkAccommService.getGmkCategory();
    }

    // 지마켓 카테고리 조회
    @ResponseBody
    @GetMapping("/getEsmCategory")
    public String getEsmCategory(){
        return gmkAccommService.getEsmCategory();
    }

    // Stie-ESM 카테고리 매칭조회
    @ResponseBody
    @GetMapping("/getSiteEsmCategory")
    public String getSiteEsmCategory(){
        return gmkAccommService.getSiteEsmCategory();
    }

    // 미니샵 카테고리 조회
    @ResponseBody
    @GetMapping("/getMiniShopCategory")
    public String getMiniShopCategory(){
        return gmkAccommService.getMiniShopCategory();
    }

    // 브랜드코드 조회 - 브랜드명으로 조회
    @ResponseBody
    @GetMapping("/getBrandCodeCategory")
    public String getBrandCodeCategory(String strBrandName){
        return gmkAccommService.getBrandCodeCategory(strBrandName);
    }

    // 판매자주소록 등록
    @ResponseBody
    @GetMapping("/sellerAddrRegist")
    public String sellerAddrRegist(){
        return gmkAccommService.sellerAddrRegist();
    }

    // 판매자주소록 수정
    @ResponseBody
    @GetMapping("/sellerAddrUpdate")
    public String sellerAddrUpdate(){
        return gmkAccommService.sellerAddrUpdate();
    }

    // 판매자주소록 조회
    @ResponseBody
    @GetMapping("/getSellerAddr")
    public String getSellerAddr(){
        return gmkAccommService.getSellerAddr();
    }

    // 판매자주소록 전체조회
    @ResponseBody
    @GetMapping("/getSellerAddrList")
    public String getSellerAddrList(){
        return gmkAccommService.getSellerAddrList();
    }

    // 카테고리별 추천옵션 조회
    @ResponseBody
    @GetMapping("/getRecOption")
    public String getRecOption(){
        return gmkAccommService.getRecOption();
    }

    // 추천옵션별 선택항목 조회
    @ResponseBody
    @GetMapping("/getSelectOption")
    public String getSelectOption(){
        return gmkAccommService.getSelectOption();
    }
}
