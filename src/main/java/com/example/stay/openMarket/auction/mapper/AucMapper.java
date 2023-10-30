package com.example.stay.openMarket.auction.mapper;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository("auction.mapper.AucMapper")
public interface AucMapper {
    Map<String, String> getCategories(int intAID);

    String getOmkSiteCode(int intAID, int intOmkIdx);

    String insertBooking(String strRsvDatas);
}
