package com.example.stay.openMarket.eland.mapper;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import org.springframework.stereotype.Repository;

@Repository
public interface ElandMapper {

    // 발급받은 AccessToken DB에 INSERT
    int insertAccessToken (String token);

    // 주문번호 만들 idx가져오기
    String getIdxForOrderID();

    // strVendGoodsNo, strRoomTypeName, 주문정보의 입실일자로 시설 정보 가져오기
    CondoDto condoInfoForInsertOrder(String con_id, String strEnterIn, String strRoomTypeName);

    // tocode 정보 다시 가져오기
    String tocodeForRoomTypeNm(String con_id, String strEnterIn, String strRoomTypeName);





    // 시설 정보 가져오기
    AccommDto getAccommInfo(int intAID, int intOMKIdx);

    // 표준상품코드 가져오기
    String getCateCode(String strAcmType, String strRegion);

    // 카테고리코드 가져오기
    String getCategoryCode(String strRegion);

    // 이랜드 상품 채번 최대값 가져오기
    int getMaxElandSeq(int intAID);

}
