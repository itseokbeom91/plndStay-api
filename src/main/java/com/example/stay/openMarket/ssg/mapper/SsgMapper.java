package com.example.stay.openMarket.ssg.mapper;

import com.example.stay.openMarket.common.dto.StockDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SsgMapper {

    String getBrnadId(int intAID);

    String getItemId(int intAID);

    int getMaxSsgSeq(int intAID);

    int getCntTempStock(int intAID, String strDate);

    // 오픈마켓 상품코드로 intAID 가져오기
    int getIntAID(String strPdtCode);

    // itme_no로 intRmIdx 가져오기
    int getIntRmIdx(int intStockIdx);

    // 체크인 날짜 가져오기
    String getCheckIn(int intStockIdx);

    // intRmIdx, 체크인 날짜 가져오기
    Map<String, String> getRmIdxNChechIn(int intStockIdx);

    // 예약 등록하기
    String createBooking(int intSeller, String strRsvCode, int intAID, int  intRmIdx, int  intRmCnt, String strCheckIn, String strCheckOut, String strRmtypeName, String strOrdName, String strOrdPhone, String strRcvName, String strRcvPhone, String strRemark, String strOrderCode, int intOrderSeq, String strProductID, String strOrderPackage);

    // 배송번호, 배송순번 가져오기
    Map<String, String> getShppNoInfo(int intRsvID);

    // intRsvID 가져오기
    int getIntRsvID(String strOrderPackage);

    // rsv_stay 취소대기
    void updateRsvStay(int intRsvID);

    // rsv_stay_omk 취소대기
    void updateRsvStayOmk(int intRsvID);

    // 판매 중지 재개 업데이트
    void updateStatus(String strStatus, int intAID);
    List<StockDto> getTestStockList();
}
