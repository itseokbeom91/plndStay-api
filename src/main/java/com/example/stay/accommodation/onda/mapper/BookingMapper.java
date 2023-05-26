package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingMapper {
    BookingDto getBookingByIntBookingID(int intOrderID);

    String updateBooking(int intBookingID, int intCondoID, int intRoomID, int intRateID, String strSpBookingId,
                         String strRefundPolicies, long stayDays);

    int updateBookingStatus(String strBookingProcess, int intBookingID);

}
