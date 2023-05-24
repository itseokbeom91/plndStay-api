package com.example.stay.accommodation.hotelStory.dto;

import lombok.Data;

@Data
public class BookingDto {

    private String strPropertyId;
    private String strRatePlanName;
    private String strRoomTypeId;
    private String strRatePlanId;
    private int intBookingID;
    private String checkInDate;
    private String checkOutDate;
    private int intPaymentPrice;
    private int intPersonCount;
    private String strOrdName;
    private String strOrdEmail;
    private String strOrdPhone;
    private String strRecvName;
    private String strRecvEmail;
    private String strRecvPhone;
    private int intRoomCount;

}
