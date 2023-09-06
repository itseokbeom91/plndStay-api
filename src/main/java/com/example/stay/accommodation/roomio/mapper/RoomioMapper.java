package com.example.stay.accommodation.roomio.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomioMapper {

    String insertAccomm(String strHotelId, String strHotelName, String strRoomDatas);

    RsvStayDto getRsvInfo(int intRsvID);
}
