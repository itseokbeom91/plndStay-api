package com.example.stay.openMarket.ssg.mapper;

import org.springframework.stereotype.Repository;

@Repository
public interface SsgMapper {

    String getBrnadId(int intAID);

    String getItemId(int intAID);

    int getMaxSsgSeq(int intAID);

    int getCntTempStock(int intAID, String strDate);
}
