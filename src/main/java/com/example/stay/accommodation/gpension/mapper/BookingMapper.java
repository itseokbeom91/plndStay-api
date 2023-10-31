package com.example.stay.accommodation.gpension.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("gpension.BookingMapper")
public interface BookingMapper {
    Map<String , Object> getBookingInfoFromBookingIdx(String intRsvID);
    /**
     * 해당하는 객실의 최대인원을 반환합니다
     *
     * @param  pensionID  펜션아이디(지펜션)
     * @param  roomID     객실아이디
     * @return            최대인원
     */
    int getMaxpeopleByroomId(String pensionID, String roomID);
    String updateStock(String pensionID, String roomID);
    String updateBooking(String orderID, String intRsvID);
}
