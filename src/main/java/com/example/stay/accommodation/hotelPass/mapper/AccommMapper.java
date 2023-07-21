package com.example.stay.accommodation.hotelPass.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("hotePass.AccommMapper")
public interface AccommMapper {
    String insertCityList (String cityCode, String cityName);

    String getDistrictCode(String cityName, String strRegion);

    String insertHotelList(String hotelCode, String hotelName, String strDistrict1,
                           String strDistrict2, String latitude, String longitude,
                           String address, String tel, String fax, String zipNo, String grade, String roomCnt);

    String insertHotel(String strHotelDatas);
}
