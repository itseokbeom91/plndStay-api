package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class RatePlanDto {
    private int intConId;
    private String strAccommId;
    private int intToconIdx;
    private String strRoomTypeId;
    private String strRatePlanId;
    private String strRatePlanName;
    private String bedTypeCode;
    private String strMealCode;
    private int intMinPersons;
    private int intMaxPersons;
    private int intPrice;
    private String strDelYn;
//    private int intMinStay;
//    private int intMaxStay;


}
