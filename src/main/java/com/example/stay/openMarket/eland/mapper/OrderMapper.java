package com.example.stay.openMarket.eland.mapper;

import com.example.stay.openMarket.common.dto.CondoDto;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface OrderMapper {

    // 주문번호 만들 idx가져오기
    String getIdxForOrderID();

    // strVendGoodsNo, strRoomTypeName, 주문정보의 입실일자로 시설 정보 가져오기
    CondoDto condoInfoForInsertOrder(String con_id, String strEnterIn, String strRoomTypeName);

    // tocode 정보 다시 가져오기
    String tocodeForRoomTypeNm(String con_id, String strEnterIn, String strRoomTypeName);
}
