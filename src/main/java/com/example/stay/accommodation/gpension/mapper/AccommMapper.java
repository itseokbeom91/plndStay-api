package com.example.stay.accommodation.gpension.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("gpension.AccommMapper")
public interface AccommMapper {
//    To-Do 예약테이블에서 GPID 뽑아오는 쿼리 필요
//    To-Do 시설 INSERT
//    To-Do 객실 INSERT

    String insertAccommTotal(String strPensionDatas, String strRoomDatas, String strStockDatas, String strType);
    String getDistrictCodeWithStr(String strDistrict1, String strDistrict2);

    void updateDelPension(String pensionID);
    void updateDelRoom(String pensionID, String roomID);

}
