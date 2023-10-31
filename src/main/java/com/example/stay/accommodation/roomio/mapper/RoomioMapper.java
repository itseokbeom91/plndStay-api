package com.example.stay.accommodation.roomio.mapper;

import com.example.stay.openMarket.common.dto.RsvStayDto;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomioMapper {

    String insertAccomm(String strHotelId, String strHotelName, String strRoomDatas);

    RsvStayDto getRsvInfo(int intRsvID);

    String getHotelId(int intAID);

    String getRoomId(int intRmIdx);

    String insertStock(int intAID, int intRmIdx, String strStockDatas);

    List<Integer> getRMOIntAID();
    List<Integer> getRmIdx(int intAID);
}
