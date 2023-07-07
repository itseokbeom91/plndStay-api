package com.example.stay.accommodation.wellihilli.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("wellihilli.AccommMapper")
public interface AccommMapper {
    String updateRmtype(String strRoomDatas);
}
