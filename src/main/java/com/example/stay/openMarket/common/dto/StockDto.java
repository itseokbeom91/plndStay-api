package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class StockDto {

    /**
     * DB goods
     */

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
}
