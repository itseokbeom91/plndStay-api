package com.example.stay.accommodation.kumho.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import org.springframework.stereotype.Repository;

@Repository("kumho.BookingMapper")
public interface BookingMapper {

    BookingDto getBookingByIntBookingID(int intBookingID);

    String updateBooking(int intBookingID, String strBookingProcess, String strSpBookingId, int intRoomCount);

    int updateBookingStatus(int intBookingID, String strBookingProcess);

    // 금호는 strPropertyID를 따로 주지 않아서 area = strPropertyID 값으로 insert했음 
    String updateGoods(String strRmtypeID, String strPropertyID, String dateSales, int intStock, int intOmkStock);
}
