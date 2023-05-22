package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface AccomodationMapper {

    String accommPhotoContentsReg(ContentsPhotoDto contentsPhotoDto);

    String roomTypeRegist(ToconDto toconDto);

    int getRoomAdminCnt(String intCondoId);

    String ratePlanRegist(RatePlanDto ratePlanDto);

    StockDto getIdxsByRatePlanId(int strRatePlanId);

    String insertGoods(int intCondoID, int intRoomID, int intRateID,
                       int intStock, String strCheckInDate, int intBasicPrice, int intSalePrice, int intMinStay, int intMaxStay);

    String accommRegistTotal(String strAccommId, String API_FLAG, String strCondoName, String strConZip,
                             String strConAddr1, String strConAddr2, String strConTel, String strConFax, String strConGekNum, String strConFlag, String strLocation, String strHomepage,
                             String strTimeIn, String strTimeOut, String strConDisplay, String strMapX, String strMapY, String strMobileWarning, String strCity, String strNation,
                             String strConDesc, String strConAround, String strConSookbak, String strTagName, String strImgData, String strPenaltyData, String strRoomNRatePlanDatas);

    String accommUpdate(Map<String, Object> totalData);

}