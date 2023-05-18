package com.example.stay.accommodation.onda.mapper;

import com.example.stay.openMarket.common.dto.*;
import org.springframework.stereotype.Repository;

@Repository
public interface AccomodationMapper {
//    CondoDto selectCondoByConId(String con_id);

//    String selectCode(String con_flag);

    String accommRegist(CondoDto accommDetail);

    String accommPhotoContentsReg(ContentsPhotoDto contentsPhotoDto);

    String cancelInfoReg(CancelInfoDto cancelInfoDto);

    String roomTypeRegist(ToconDto toconDto);

    int getRoomAdminCnt(String conId);

    String ratePlanRegist(RatePlanDto ratePlanDto);
}
