package com.example.stay.openMarket.gmarket.mapper;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface GmkMapper {
//    List<Map<String, String>> getBrandCodeList();
    Map<String, String> getCategories(int intAID);

    String getOmkSiteCode(int intAID, int intOmkIdx);

//    String insertBooking(int intSeller, String strRsvCode, int intAID, int intRmIdx, int intRmCnt, String strCheckIn, String strCheckOut
//            , String strRmtypeName, String strRmOptName, String strOrdName, String strOrdPhone, String strRcvName, String strRcvPhone
//            , String strIP, String strRemark, String strOrderCode, int intOrderSeq, String strOrderStatus, String strProductID, String strOrderPackage);

    String insertBooking(String strRsvDatas);
}
