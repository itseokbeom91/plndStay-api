package com.example.stay.accommodation.gpension.service;

import com.example.stay.accommodation.gpension.mapper.BookingMapper;
import com.example.stay.common.util.Base64Encoder;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Service("gpension.BookingService")
public class BookingService {

    @Autowired
    BookingMapper bookingMapper;

    CommonFunction commonFunction = new CommonFunction();

    public String createBooking(String dataType, int intRsvID) {
        // request parameter 중 예약자명, 고객요청사항은 base64 encoding하여 전송
        // 펜션ID, 객실ID, 결제여부 (O, X), 입실일(yyyy-mm-dd), 숙박일 수, 예약자명, hp1, hp2, hp3, 이메일, 생년월일(yyyy-mm-dd), 성인수, 아동수, 픽업신청여부(O, X), 도착시간구분(AM, PM), 도착시간, 객실요금, 총 요금(추가인원 금액을 포함한), 요청사항, 캐릭터셋
        // pension_id, room_id, charge_flag, startdate, daytype, name, hp1, hp2, hp3, email, birthday, adult_num, child_num, pickup, ampm, ar_time, room_price, total_price, memo, char
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intRsvID);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        //아래는 임시DATA임 예약테이블 생성시 추가작업 필요
        String pensionID =(String) bookingMap.get("pensionId");
        String roomId = (String) bookingMap.get("strRmtypeID");
        String chargeFlag = "X";//(String) bookingMap.get("charge_flag");
        String startDate = bookingMap.get("dateCheckIn").toString();//"2023-09-27";//
        String daytype = "1";//(String) bookingMap.get("nights");//"1";// TO-DO 퇴실일자 비교해서 투숙일 계산 필요
        String userName = (String) bookingMap.get("strOrdName");//"테스트예약";//
        String hp = (String) bookingMap.get("strOrdPhone");
        hp = hp.replaceAll("-", "");
        String hp1 = hp.substring(0, 3);
        String hp2 = hp.substring(3, 7);
        String hp3 = hp.substring(7, hp.length());
        String adult_num = bookingMap.get("intQuantityA").toString();
        String child_num = bookingMap.get("intQuantityC").toString();
        String room_price = bookingMapper.getMoneyByintRsvID(bookingMap.get("intRmIdx").toString(), bookingMap.get("dateCheckin").toString());;//(String) bookingMap.get("room_price");
        String total_price = "0";//(String) bookingMap.get("total_price");
        String intAID = bookingMap.get("intAID").toString();
//        String email = (String) bookingMap.get("email");                //필수 X
//        String birthday = "";//(String) bookingMap.get("birthday");     //필수 X
//        String pickup = (String) bookingMap.get("pickup");              //필수 X
//        String ampm = (String) bookingMap.get("ampm");                  //필수 X
//        String ar_time = (String) bookingMap.get("ar_time");            //필수 X
//        String memo = (String) bookingMap.get("memo");                  //필수 X
//        String charSet = (String) bookingMap.get("char");               //필수 X
        userName = Base64Encoder.encode(userName.getBytes());
//        memo = Base64Encoder.encode(memo.getBytes());

        Integer adult_numInt = Integer.parseInt(adult_num);
        Integer child_numInt = Integer.parseInt(child_num);

        if ((adult_numInt + child_numInt) > bookingMapper.getMaxpeopleByroomId(intAID, roomId)) {
            return commonFunction.makeReturn(dataType,"500", "투숙인원 초과!");
        }

//        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionID + "&room_id=" + roomId + "&charge_flag=" + chargeFlag + "&startdate=" + startDate +
//                "&daytype=" + daytype + "&name=" + userName + "&hp1=" + hp1 + "&hp2=" + hp2 + "&hp3=" + hp3 + "&email=" + email +
//                "&birthday=" + birthday + "&adult_num=" + adult_num + "&child_num=" + child_num + "&pickup=" + pickup + "&ampm=" + ampm + "&ar_time=" + ar_time +
//                "&room_price=" + room_price + "&total_price=" + total_price + "&memo=" + memo + "&charSet=" + charSet;
        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionID + "&room_id=" + roomId + "&charge_flag=" + chargeFlag + "&startdate=" + startDate +
                "&daytype=" + daytype + "&name=" + userName + "&hp1=" + hp1 + "&hp2=" + hp2 + "&hp3=" + hp3  + "&adult_num=" + adult_num + "&child_num=" + child_num +
                "&room_price=" + room_price + "&total_price=" + total_price;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "join_room.php" + requestURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
//        예약전 예약 가능 여부 확인

        String orderYn = searchRoom("jsonp", roomId, startDate, daytype);
        orderYn = orderYn.substring(5,orderYn.length()-1);
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(orderYn);
            String result = (String) responseJson.get("result");
            String[] resultArr = result.split("::");
            if("S".equals(resultArr[0])) {
                //TO-DO 예약 가능하므로 예약로직 진행
                LogWriter logWriter = new LogWriter("GET", requestURI, System.currentTimeMillis());
                Response response = client.newCall(request).execute();

                String bookResult = response.body().string();
                JSONObject resultJson = new JSONObject();
                resultJson.put("result_cd", bookResult.split("::")[0]);
                resultJson.put("result_msg", bookResult.split("::")[1]);
                if ("S".equals(resultJson.get("result_cd"))) {
                    //예약 성공 재고UPDATE
                    resultJson.put("order_no", bookResult.split("::")[2]);
//                    예약테이블에 예약번호 꼭 집어넣기!
                    bookingMapper.updateBooking((String) resultJson.get("order_no"), intRsvID);

                } else {
                    //예약 실패
                /*
                예약 응답은 result_cd::result_msg::order_no
                분기처리 확인해서 할 것
                처리결과 코드 (S : 정상 처리 / P : 필수 파라미터 누락 / E : 파라미터 형식 오류 / D : 이미 예약된 객실
                 */
                    return commonFunction.makeReturn(dataType, "500", (responseJson.get("result_cd").toString()+responseJson.get("result_msg").toString()));
                }

                return commonFunction.makeReturn(dataType,"200", (responseJson.get("result_cd").toString()+responseJson.get("result_msg").toString()), resultJson);
            } else {
                if ("1".equals(resultArr[1])) {
                    //입실일에 예약이 이미 되어 있음
                    return commonFunction.makeReturn(dataType,"500", "입실일에 예약이 이미 되어 있음");
                } else {
                    //입실일로부터 나오는숫자의 박째에 예약이 이미되어있음
                    return commonFunction.makeReturn(dataType,"500", "입실일로부터 나오는숫자의 "+ resultArr[1].replaceAll("/", ",") +"박째에 예약이 이미되어 있음");
                }
            }
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType,"500", e.getMessage());
        }

    }

    public String confirmBooking(String dataType, int intRsvID) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intRsvID);
        String orderNo = (String) bookingMap.get("strRsvCode");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&order_no=" + orderNo;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "confirm_room.php" + requestURI)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
            String result = (String) responseJson.get("result");
            JSONObject resultJson = new JSONObject();
            resultJson.put("result_cd", result.split("::")[0]);
            resultJson.put("result_msg", result.split("::")[1]);
            if (resultJson.get("result_cd").equals("S")) {
                //예약 확정 -- 예약상태를 변경하여야하나?
                return commonFunction.makeReturn(dataType,"200", "OK", resultJson);
            }
            return commonFunction.makeReturn(dataType,"200", "OK", resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType,"500", "");
        }
    }

    public String cancelBooking(String dataType, int intRsvID) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intRsvID);
        String orderNo = (String) bookingMap.get("strRsvCode");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&order_no=" + orderNo;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "cancel_room.php" + requestURI)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject resultJson = new JSONObject();
            resultJson.put("result_cd", responseBody.split("::")[0]);
            resultJson.put("result_msg", responseBody.split("::")[1]);
            if (resultJson.get("result_cd").equals("S")) {
                //취소 성공
                resultJson.put("order_no", responseBody.split("::")[2]);
            }
            return commonFunction.makeReturn(dataType,"200", "OK", resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType,"500", e.getMessage());
        }
    }
    @Async
    public String searchRoom(String dataType, String roomId, String startDate, String daytype) {
        //room_id, startdate, daytype(박수)
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&room_id=" + roomId + "&startdate=" + startDate + "&daytype=" + daytype;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "search_room.php" + requestURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                //response 파싱
                String responseBody = response.body().string();
                return commonFunction.makeReturn(dataType,"200", "OK", responseBody);
            } else {
                return commonFunction.makeReturn(dataType,"500", response.message(), "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType,"500", e.getMessage());
        }
    }

    public String searchOrder(String dataType, int intRsvID) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intRsvID);
        String orderNo = (String) bookingMap.get("strRsvCode");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&order_no=" + orderNo;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "search_order.php" + requestURI)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
            String result = (String) responseJson.get("result");
            JSONObject resultJson = new JSONObject();
            resultJson.put("result_cd", result.split("::")[0]);
            resultJson.put("result_msg", result.split("::")[1]);
            return commonFunction.makeReturn(dataType,String.valueOf(response.code()), response.message(), resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType,"500", e.getMessage());
        }
    }

    private String getCancelFee(String dataType, int intRsvID) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intRsvID);
        String orderNo = (String) bookingMap.get("strRsvCode");
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        requestURI += "auth_key=" + Constants.gpAuth + "&order_no=" + orderNo;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "get_cancel_fee.php" + requestURI)
                .get()
                .build();
        try {
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
            String result = (String) responseJson.get("result");
            JSONObject resultJson = new JSONObject();
            resultJson.put("result_cd", result.split("::")[0]);
            resultJson.put("result_msg", result.split("::")[1]);
            return commonFunction.makeReturn(dataType,String.valueOf(response.code()), response.message(), resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn(dataType,"500", e.getMessage());
        }
    }
}
