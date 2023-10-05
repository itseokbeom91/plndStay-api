package com.example.stay.openMarket.ebay.service;

import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.example.stay.openMarket.common.dto.AccommDto;
import com.example.stay.openMarket.common.mapper.CommonMapper;
import com.example.stay.openMarket.ebay.mapper.EbayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class AccommService {

    @Autowired
    private EbayMapper ebayMapper;

    @Autowired
    private CommonMapper commonMapper;

    public String getAccommList(HttpServletRequest httpServletRequest){
        LogWriter logWriter = new LogWriter(httpServletRequest.getMethod(), httpServletRequest.getServletPath(),
                httpServletRequest.getQueryString(), System.currentTimeMillis());
        String strXml = "";
        try{
            int intAID = 0; // 상품 번호

            AccommDto accommDto = commonMapper.getAcmInfo(intAID, Constants.intGmkOmkIdx);

            // 상품명
            // 상품 영문명 - 해외호텔 필수
            // 상품 카탈로그명
            // 검색 키워드
            // 전시 사이트 구분(0 : 공통, 1 : 지마켓, 2 : 옥션)
            // 전시 영역 구분(A : 공통, P : PC, M : MOBILE)
            // 상품전시 시작일(YYYY-MM-DD)
            // 상품전시 종료일(YYYY-MM-DD)
            // 상품전시 최소가격 기준일자(YYYY-MM-DD)
            // 상품전시 최소가격 기준정보(객실명 또는 룸타입명 EX) 싱글룸 / 더블룸)
            // 상품전시 최소가격 기준정보 코드(객실코드 또는 룸타입코드)
            // 상품전시 최소 가격(10원 단위까지 가능)
            // 상품전시 최대 가격(10원 단위까지 가능)
            // 상품 대표 이미지 URL(최소 600 * 600, 2MB 미만)
            // 상품 상태(1 : 구매 가능, 2 : 구매 불가능)
            // 상품구분(국내호텔, 국내 리조트, 해외 호텔 ...)
            // 국내 국외(1 : 국내, 2 : 해외)
            // 국가코드
            // 국가명
            // 도시코드
            // 도시명
            // 상세 지역코드(국내숙박일 경우 필수)
            // 상세 지역명
            // 주소
            // 우편번호
            // X좌표
            // Y좌표
            // 호텔등급
            // 전화번호
            // FAX번호
            // 체크인 시간 (EX) 14:00)
            // 체크아웃 시간
            // 이미지 업데이트 여부(Y : 업데이트, N : 변경없음)
            // 옵션정보
            // 랜드마크 정보
            // 랜드마크 업데이트 여부(Y : 업데이트, N : 변경없음)
            // 상품 더보기 이미지 정보(최대 30개)
            // 더보기 이미지 업데이트 여부(Y : 업데이트, N : 변경없음)

        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }

        return strXml;
    }
}
