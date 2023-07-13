package com.example.stay.accommodation.hotelPass.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("hotePass.AccommMapper")
public interface AccommMapper {
    String insertCityList (String cityCode, String cityName);
}
