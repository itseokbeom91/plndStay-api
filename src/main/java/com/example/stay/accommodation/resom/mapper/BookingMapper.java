package com.example.stay.accommodation.resom.mapper;

import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Repository("resom.BookingMapper")
public interface BookingMapper {
    String localInsert(String data);

    String updateBooking(String intRsvID, String strStatusCode, String strRsvRmNum);

    Map<String , Object> getBookingInfoFromBookingIdx(String intRsvID);
    String insertRoom(String strPackageDatas, String strRoomDatas, String strStockDatas, String strAccommDatas, String strType);

    Map<String, Object> getPackageCodeAndStoreCode(String type);
}
