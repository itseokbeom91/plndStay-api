package com.example.stay.openMarket.eland.mapper;

import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.dto.CondoDto;
import org.springframework.stereotype.Repository;

import java.util.Map;

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

    // 채번
    String setNumbering(int intAID, String stockDatas);

    // 오픈마켓 상품코드로 intAID 가져오기
    int getIntAID(String strPdtCode);

    // itme_no로 intRmIdx 가져오기
    int getIntRmIdx(int intAID, int intItemNo);

    // intRmIdx, 체크인날짜 가져오기
    Map<String, String> getRmIdxNChechIn(int intAID, int intItemNo);

    // 예약 등록하기
    String createBooking(int intSeller, String strRsvCode, int intAID, int  intRmIdx, int  intRmCnt, String strCheckIn, String strCheckOut, String strRmtypeName, String strOrdName, String strOrdPhone, String strRcvName, String strRcvPhone, String strRemark, String strOrderCode, int intOrderSeq, String strProductID, String strOrderPackage);

    // 배송번호, 배송순번 가져오기
    Map<String, String> getDeliInfo(int intRsvID);

    // intRsvID 가져오기
    int getIntRsvID(String strOrderCode, int intOrderSeq);

    // rsv_stay 취소대기
    void updateRsvStay(int intRsvID);

    // rsv_stay_omk 취소대기
    void updateRsvStayOmk(int intRsvID);

}
