package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class RatePlanDto {
    /**
     * DB test_rate_plan
     */

    private int intCondoID;
    private String strAccommId;
    private int intRoomID;
    private String strRoomTypeId;
    private String intRateID;
    private String strRatePlanName;
    private String bedTypeCode;
    private String strMealCode;
    private int intMinPersons;
    private int intMaxPersons;
    private int intPrice;
    private String strDelYn;

}
