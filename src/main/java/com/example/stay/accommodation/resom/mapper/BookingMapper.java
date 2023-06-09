package com.example.stay.accommodation.resom.mapper;

import com.example.stay.common.util.ResponseResult;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;

@Repository("resom.BookingMapper")
public interface BookingMapper {

    String insertPackage(String pkgNo, String nights, String maxNights, String rmCnt, String maxRmCnt, String rmTypeCd);
}
