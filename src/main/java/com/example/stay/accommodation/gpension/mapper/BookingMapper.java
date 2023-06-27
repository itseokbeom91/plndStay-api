package com.example.stay.accommodation.gpension.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("gpension.BookingMapper")
public interface BookingMapper {
    Map<String , Object> getBookingInfoFromBookingIdx(int intBookingIdx);
}
