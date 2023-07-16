package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("kumho.BookingMapper")
public interface BookingMapper {

    BookingDto getBookingByIntBookingID(int intBookingID);

    String updateBooking(int intBookingID, String strBookingProcess, String strSpBookingId, int intRoomCount);

    int updateBookingStatus(int intBookingID, String strBookingProcess);

    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    Map<String, Object> getRmtypeIDNIntAID(int intRmIdx);

    String getStrLocalCode(int intAID, String strRmtypeID);
}
