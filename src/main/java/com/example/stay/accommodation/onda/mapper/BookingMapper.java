package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingMapper {
    BookingDto getBookingByIntBookingID(int intOrderID);

    int insertRefundPolicy(String untilDate, int intPercent, int intRefundPrice, int intRefundFee);
}
