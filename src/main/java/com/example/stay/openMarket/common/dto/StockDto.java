package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class StockDto {

    private String dateSales;
    private String strRmtypeName;
    private int intRmIdx;
    private int moneySales;
    private int intStock;
    private int intSsgSeq;
    private int intGsshopSeq;
    private int intNextStock;

    /**
     * DB goods


    private String strGoodDate;
    private String strTocode;
    private int intOMKPrice;
    private int intOMKStock;
    private String strGoodId;
    private int intGoodSeq;
    private int intOMKSeq;
    private String strOMKFlag;
    private int intOMKNextStock;
    private int intPId;

    /**
     * DB test_goods


    private int intGoodsID;
    private int intCondoID;
    private int intRoomID;
    private int intRateID;
    private int intStock;
    private String checkInDate;
    private int intBasicPrice;
    private int intSalePrice;
    private int intMinStay;
    private int intMaxStay;

     */
}
