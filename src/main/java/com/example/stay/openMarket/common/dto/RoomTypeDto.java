package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class RoomTypeDto {

    private int intIdx;
    private int intAID;
    private String strRmtypeName;
    private String strShortDesc;
    private String strRmPhotos;
    private int intQuanStd;
    private int intQuanMax;
    private String strRefundYn;
    private int intExtraA;
    private int intExtraC;

    private String strCpItemCode;
    private String strCprateCode;

    private String strPdtCode;
}
