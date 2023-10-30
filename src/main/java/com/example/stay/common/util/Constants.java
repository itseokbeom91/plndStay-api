package com.example.stay.common.util;

import java.util.Base64;

public class Constants {

    public static final String jsonpCallback = "cd24";
    public static final String vWorldApiSecretKey = "76EF76B2-5EB8-3181-A583-0D02CA042C30";
    public static final String faxId = "condo2424";
    public static final String faxPwd = "red01341234";

    public static final String rsv_history_rsv = "예약 -> 완료";
    public static final String rsv_history_rsv_to_cancel = "예약상태 변경 : ";

    // 예약 상태값
    public static final String rsvStatus_rsv = "0"; // 예약
    public static final String rsvStatus_rsv_complete = "4"; // 완료
    public static final String rsvStatus_cancel_wait = "14"; // 취소 대기
    public static final String rsvStatus_cancel_complete = "5"; // 취소 완료



    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ 쿠팡
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String cpUrl = "/v2/providers/travel_connect_api/apis/v1/marketplace/";
    public static final String cpHost = "api-gateway.coupang.com";
    public static final int cpPort = 443;
    public static final String cpAccessKey = "d2e7aa3e-dd1f-4863-b518-b550f5489cf8";
    public static final String cpSecretKey = "8dbe1471b2a086b8eacd22b2e3fafacb84d7c3bf";
    public static final String cpVendorId = "A00919983";
    public static final String HMAC_SHA_256 = "HmacSHA256";
    public static final String HMAC_SHA_1 = "HmacSHA1";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ 호텔스토리
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String hotelStoryID = "condo2424";
    public static final String hotelStoryAuthKey = "hotelstory!@#";
    public static final String hotelStoryFileDir = "D:\\dev\\4.photo\\condo_images\\hotelStory\\";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ SSG
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String SsgAuthorization = "d02850a6-ec01-45ad-9916-0985af25a61b";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ Eland
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String elandClientId = "L200708078"; // 상용? : L220510198
    public static final String elandClientSecret = "p1586262E!"; // 상용? : p33846938!
    // client_id:client_secret Base64인코딩
    public static final String base64EncodedAuth = "Basic " + Base64.getEncoder().encodeToString((elandClientId + ":" + elandClientSecret).getBytes());
//    public static final String elandPath = "https://int-api.elandmall.co.kr"; // 테스트 서버
    public static final String elandPath = "https://api.elandmall.com"; // 상용 서버

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ onda
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String ondaAuth = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJjbGllbnRfa2V5IjoiM2YzNDY3MTIzNTc1NTc3OWEyMDliOTczZDlkM2NhODJmYzgyY2IwMzk5OTkzOTNlYzk3ZmJhMWExN2I2NjUzYyIsInRpbWVzdGFtcCI6MTY4MzA4MDU1ODUzOCwic2VydmljZV9pZCI6MSwidGFyZ2V0IjoiY2hhbm5lbCIsInRhcmdldF9pZCI6MTcyLCJpYXQiOjE2ODMwODA1NTgsImV4cCI6MTc0NjE1MjU1OH0.E8Tjjpl4C3wNxLlHvIFk3o6MACCVtI36_MYEoricsKM";
    /**
     * TODO : 테스트용 url 변경
     */
    public static final String ondaPath = "https://dapi.tport.dev/gds/diglett/"; // 테스트용
    public static final String ondaFileDir = "C:\\Users\\DEV1\\condo_images\\onda\\";
    public static final String webhookAuthKey = "b/8waV7zIhA5w8O1BnpHhmikDvaCTnDFpJwmJVdkCbo=";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ kumho
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
//    public static final String interfaceId = "yhsc009";
    public static final String groupId = "100211";
    public static final String [] groupIdArr = {"100211", "100009", "160349", "209971", "209972", "209973"};
    public static final String kumhoUrl = "https://www.kumhoresort.co.kr/interface/";

    public static final String spavisUrl = "http://www.spavis.co.kr/";
    public static final String cpCustomerID = "411180"; // 선납권 관련 api 호출 시 사용하는 ID
    public static final String tkCustomerID = "411538"; // 티켓 관련 api 호출 시 사용하는 ID
    public static final String goodsCode = "P10200"; // 티켓구매시 이용하는 상품코드


    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ hanwha
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String hanwhaCustNo = "0000000008";
    public static final String hanwhaContNo = "11605542";
    public static final String hanwhaUrl = "https://exgate.hanwharesort.co.kr:443/iGate/LCB/json.jdo";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ elysian
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String elysUrl = "https://www.elysian.co.kr/wesp/cjt/datarecv.asp?";
    public static final String elys_DH_CODE1 = "1006";
    public static final String elys_DH_CODE2 = "1030";
    public static final String ely_PASS = "1234";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ 용평 / 비체
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String ypUrl = "https://api.iyes.biz:17004/yobiss-api/api/cr/rmsrv/RmsrvAgencyApi/";
    public static final String ypTokenKey = "VHKeffdhb+rSUWzqsLO7ivkwKnsXhw39AYzw/yTtChA=";
    public static final String yongpyongCode = "1199719";
    public static final String beacheCode = "1178413";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ wellihilli
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String whpUrl = "https://vapi.wellihillipark.com";
    public static final String whpFileDir = "C:\\Users\\DEV1\\condo_images\\wellihilli\\";
    public static final String whpImgUrl = "https://www.wellihillipark.com/data/common/DataFiles/upload/image/facilities/condo_house/condo/";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ resom
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public static final String resomAuth = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb25kbzI0X2FwaV8xIiwiaWF0IjoxNjc4ODUyMjMyLCJleHAiOjI1MzQwMjI2ODM5OX0.fQdCnmV4jM6Yv7UoDYgjad2kJuiZzp9zS75c1bmaYxA";
    public static final String resomId = "condo24_api_1";
    public static final String resomLanguage = "kor";
    //    public static final String resomPath = "https://api.resom.co.kr/api/cms/ota/booking/v1/test/pack";
    public static final String resomPath = "https://api.resom.co.kr/api/cms/ota/booking/v1/real/pack"; //운영URL

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ sono
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public static final String sonoPackAuth = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb25kbzI0MDBfYXBpXzEiLCJpYXQiOjE1ODgwMjcxNDYsImV4cCI6MjUzNDAyMjY4Mzk5fQ.VJ6JDErJ-3Fhuwj3b2YxiQApBSyTmQ4acEXtmmqE2hw";
    public static final String sonoPackId = "condo2400_api_1";
    public static final String sonoTicketAuth = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb25kbzI0MDBfYXBpXzIiLCJpYXQiOjE2NTE1NDA2OTUsImV4cCI6MjUzNDAyMjY4Mzk5fQ.URxjwn0MKOxKtsI-0ay-HNKq_AcqfIiRfp8Lv38eepY";
    public static final String sonoTicketId = "condo2400_api_2";
    public static final String sonoRoomAuth = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjb25kbzI0MDBfYXBpXzMiLCJpYXQiOjE2MDI1NjMxMDEsImV4cCI6MjUzNDAyMjY4Mzk5fQ.aSEfOsgMF4cey3NcY-rlTxcxsMQYVD86jCn6s2dJyFE";
    public static final String sonoRoomId = "condo2400_api_3";
    //    public static final String sonoPackPath = "https://sonoapi.traveland.co.kr/api/ota/v1/test/pack";       // 패키지URL(테스트)
//    public static final String sonoRoomPath = "https://sonoadmin.traveland.co.kr/api/ota/v1/test/roomonly"; // 룸온리URL(테스트)
    public static final String sonoPackPath = "https://sonoapi.traveland.co.kr/api/ota/v1/real/pack";       // 패키지URL(운영)
    public static final String sonoRoomPath = "https://sonoadmin.traveland.co.kr/api/ota/v1/real/roomonly"; // 룸온리URL(운영)
    public static final String sonoLanguage = "kor";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ GPension
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

    public static final String gpAuth = "CD02050011720";
    public static final String gpPath = "http://gpapi.gpension.co.kr/_API/_LINK/";
    public static final String gpensionFileDir = "D:\\dev\\4.photo\\condo_images\\gpension\\";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ HotelPass
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String hpComCode = "2-16710";
    public static final String hpID = "dongmu";
    public static final String hpPW = "dongmu2022";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ 11st
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String elevenApiKey = "c6c8040fd368a4df4a6bb13bc3ba3e65";
    public static final String elevenUrl = "https://api.11st.co.kr";

    // ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ┃ 지마켓 & 옥션
    // ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    public static final String gmk_secret_key = "BtqV+nlQ2EisJBB0BGNULA=="; // 일반배송상품 지마켓, 옥션 시크릿키
    public static final String gmkEsmMasterID = "condo24";
    public static final String gmkUrl = "https://sa2.esmplus.com/"; // 일반배송상품 지마켓, 옥션 시크릿키
    public static final int intGmkOmkIdx = 5;
    public static final int intGmkConnID = 34;
    public static final int gmk_delivery_compnay_code = 10070; // 택배사 코드 - 기타
    public static final int gmk_dispatch_policy_no = 85888; // 발송정책번호 - 미정
    public static final int gmk_official_notice = 27; // 상품정보고시 상품군코드 - 호텔/펜션 예약
    public static final int gmk_shipping_place = 716363; // 출하지번호 - 본점
    public static final int gmk_seller_AddrNo = 1113012; // 출하지번호 - 본점

    public static final int intAucOmkIdx = 6;
    public static final int intAucConnID = 35;
}
