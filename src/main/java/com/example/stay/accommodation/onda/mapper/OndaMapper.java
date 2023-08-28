package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.BookingDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface OndaMapper {

    String insertAccommTotal(String strPropertyID, String strDeleteYn, String strViewYn, String strType,
                             String strDistrict1, String strDistrict2, String strSubject, String strLat, String strLon,
                             String strCheckIn, String strCheckOut, String strPhone, String strFax, String strEmail, String strZipCode,
                             String strAddr1, String strAddr2, String strDescription, String strRsvGuide, String strAcmNotice,String strImgDatas,
                             String strPenaltyDatas, String strKeyWordDatas, String strAttractionDatas, String strFacilityDatas, String strRmtypeDatas);

    String updateRmtype(String strPropertyID, String strRmtypeDatas);

    String updateGoods(int intAID, int intRmIdx, String strStockDatas);

    String webhookUpdateGoods(String strStockDatas);

    String getPropertyID(int intAID);

    Map<String, Object> getStrRateplanIDNIntAID(int intRmIdx);

    String updateStatus(String target, String strDeleteYn, String strViewYn, String propertyId, String roomTypeId, String ratePlanId);

    Map<String, String> getDistrictCode(String strRegion, String strCity);

    String getStrCodeByStrName(String strCateCode, String strName);

    BookingDto getBookingByIntBookingID(int intOrderID);

    String updateBooking(int intBookingID, int intCondoID, int intRoomID, int intRateID, String strSpBookingId,
                         String strRefundPolicies, long stayDays);

    int updateBookingStatus(String strBookingProcess, int intBookingID);
}