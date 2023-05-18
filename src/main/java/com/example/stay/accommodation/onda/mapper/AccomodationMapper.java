package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

@Repository
public interface AccomodationMapper {
//    CondoDto selectCondoByConId(String con_id);

//    String selectCode(String con_flag);

    String accommRegist(String strAccommDetail);

    String accommPhotoContentsReg(ContentsPhotoDto contentsPhotoDto);

    String cancelInfoReg(CancelInfoDto cancelInfoDto);

    String roomTypeRegist(ToconDto toconDto);

    int getRoomAdminCnt(String conId);

    String ratePlanRegist(RatePlanDto ratePlanDto);

    StockDto getIdxsByRatePlanId(String strRatePlanId);

    String goodsRegist(StockDto stockDto);

    String accommRegistTotal(String strAccommId, String strApiFlag,String strAccommName, String strZipcode
            , String strAddress, String strConAddrNew, String strConTel, String strConFax, String location
            , String strTimeIn, String strTimeOut, String strConDisplay, String strDecLng, String strDecLat
            , String strUsageNotice, String strCity, String strNation, String strSummary, String strTags
            , String strImgData, String strPenaltyData);
}
