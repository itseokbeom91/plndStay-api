package com.example.stay.accommodation.roomio.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface RoomioMapper {

    String insertAccomm(String strHotelId, String strHotelName, String strRoomDatas);
}
