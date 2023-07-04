package com.example.stay.accommodation.hotelStory.mapper;

import com.example.stay.accommodation.hotelStory.dto.BookingDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface HotelStoryMapper {

    String insertAccommtotal(String strPropertyId, String strPropertyName, String strAddress, String strPhone, String strNumRooms, String strLocation, String strHomePageUrl, String strCheckInTime, String strCheckOutTime
            , String strLongitude, String strLatitude, String strCity, String strPropertyDescription, String strTrafficInformation, String strRoomInformation, String imgData, String cancelData, String roomTypeData);

    String insertProperty(String strPropertyId, String strLocation, String strCity, String strPropertyName, String strLatitude, String strLongitude, String strStarRating, String strNumRooms, String strCheckInTime, String strCheckOutTime, String strPhone, String strAddress, String strHomePageUrl
            , String strPropertyDescription, String strTrafficInformation, String strRsvGuide, String imgData, String cancelData, String roomTypeData, String addData);

    Map<String, String> getAcmRmIdx(String strPropertyId, String strRoomTypeId, String strRatePlanId);

    String insertStock(int intAID, int intRmIdx, String strStockDatas);

    BookingDto getbooking(int intBookingID);

    String updateBooking(int intBookingID, String strBookingProcess, String strSpBookingId, int intRoomCount);

    String insertRefund(int intBookingID, String strRefundData);
}
