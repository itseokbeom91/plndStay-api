package com.example.stay.accommodation.gpension.service;

import com.example.stay.accommodation.gpension.mapper.BookingMapper;
import com.example.stay.common.util.Base64Encoder;
import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
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

    public String createBooking(int intBookingIdx) {
        // request parameter 중 예약자명, 고객요청사항은 base64 encoding하여 전송
        // 펜션ID, 객실ID, 결제여부 (O, X), 입실일(yyyy-mm-dd), 숙박일 수, 예약자명, hp1, hp2, hp3, 이메일, 생년월일(yyyy-mm-dd), 성인수, 아동수, 픽업신청여부(O, X), 도착시간구분(AM, PM), 도착시간, 객실요금, 총 요금(추가인원 금액을 포함한), 요청사항, 캐릭터셋
        // pension_id, room_id, charge_flag, startdate, daytype, name, hp1, hp2, hp3, email, birthday, adult_num, child_num, pickup, ampm, ar_time, room_price, total_price, memo, char
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intBookingIdx);
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        String requestURI = "?";
        //아래는 임시DATA임 예약테이블 생성시 추가작업 필요
        String pensionID = (String) bookingMap.get("pension_id");
        String roomId = (String) bookingMap.get("roomID");
        String chargeFlag = (String) bookingMap.get("charge_flag");
        String startDate = (String) bookingMap.get("startDate");
        String daytype = (String) bookingMap.get("nights");
        String userName = (String) bookingMap.get("name");
        String hp = (String) bookingMap.get("phone");
        String hp1 = hp.split("-")[0];
        String hp2 = hp.split("-")[1];
        String hp3 = hp.split("-")[2];
        String email = (String) bookingMap.get("email");                //필수 X
        String birthday = (String) bookingMap.get("birthday");
        String adult_num = (String) bookingMap.get("adult_num");
        String child_num = (String) bookingMap.get("child_num");
        String pickup = (String) bookingMap.get("pickup");              //필수 X
        String ampm = (String) bookingMap.get("ampm");                  //필수 X
        String ar_time = (String) bookingMap.get("ar_time");            //필수 X
        String room_price = (String) bookingMap.get("room_price");
        String total_price = (String) bookingMap.get("total_price");
        String memo = (String) bookingMap.get("memo");                  //필수 X
        String charSet = (String) bookingMap.get("char");               //필수 X
        userName = Base64Encoder.encode(userName.getBytes());
        memo = Base64Encoder.encode(memo.getBytes());

        requestURI += "auth_key=" + Constants.gpAuth + "&pension_id=" + pensionID + "&room_id=" + roomId + "&charge_flag=" + chargeFlag + "&startdate=" + startDate +
                "&daytype=" + daytype + "&name=" + userName + "&hp1=" + hp1 + "&hp2=" + hp2 + "&hp3=" + hp3 + "&email=" + email +
                "&birthday=" + birthday + "&adult_num=" + adult_num + "&child_num=" + child_num + "&pickup=" + pickup + "&ampm=" + ampm + "&ar_time=" + ar_time +
                "&room_price=" + room_price + "&total_price=" + total_price + "&memo=" + memo + "&charSet=" + charSet;
        Request request = new Request.Builder()
                .url(Constants.gpPath + "join_room.php" + requestURI)
                .get()
                .addHeader("Content-Type", "application/json")
                .build();
//        예약전 예약 가능 여부 확인

        String orderYn = searchRoom(roomId, startDate, daytype);
        orderYn = orderYn.substring(5,orderYn.length()-1);
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(orderYn);        
            String result = (String) responseJson.get("result");
            if("S".equals(result.substring(0, 1))) {
                //TO-DO 예약 가능하므로 예약로직 진행
                Response response = client.newCall(request).execute();
                responseJson = (JSONObject) jsonParser.parse(response.body().toString());
                /*
                예약 응답은 result_cd::result_msg::order_no
                분기처리 확인해서 할 것
                처리결과 코드 (S : 정상 처리 / P : 필수 파라미터 누락 / E : 파라미터 형식 오류 / D : 이미 예약된 객실
                 */
                if (response.isSuccessful()){
                    //예약완료되었으니 재고 update 쳐야지?!!!
                } else {

                }
            } else {
                String[] resultArr = result.split("::");
                if ("1".equals(resultArr[1])) {
                    //입실일에 예약이 이미 되어 있음
                } else {
                    //입실일로부터 나오는숫자의 박째에 예약이 이미되어있음
                }
            }
        } catch (Exception e) {
            return commonFunction.makeReturn(e.toString(), e.getMessage());
        }

        return commonFunction.makeReturn("", "");
    }

    public String confirmBooking(int intBookingIdx) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intBookingIdx);
        String orderNo = (String) bookingMap.get("order_no");
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
            return commonFunction.makeReturn("", "", resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn("", "");
        }
    }

    public String cancelBooking(int intBookingIdx) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intBookingIdx);
        String orderNo = (String) bookingMap.get("order_no");
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
            JSONParser jsonParser = new JSONParser();
            JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);
            String result = (String) responseJson.get("result");
            JSONObject resultJson = new JSONObject();
            resultJson.put("result_cd", result.split("::")[0]);
            resultJson.put("result_msg", result.split("::")[1]);
            return commonFunction.makeReturn("", "", resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn("", "");
        }
    }
    @Async
    public String searchRoom(String roomId, String startDate, String daytype) {
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
                return commonFunction.makeReturn("", "", responseBody);
            } else {
                return commonFunction.makeReturn("", "", "");
            }
        } catch (Exception e) {
            return commonFunction.makeReturn(e.toString(), e.getMessage());
        }
    }

    public String searchOrder(int intBookingIdx) {
        //order_no (예약번호) 만 태움
        Map<String, Object> bookingMap = bookingMapper.getBookingInfoFromBookingIdx(intBookingIdx);
        String orderNo = (String) bookingMap.get("order_no");
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
            return commonFunction.makeReturn(String.valueOf(response.code()), response.message(), resultJson);
        } catch (Exception e) {
            return commonFunction.makeReturn(e.toString(), e.getMessage());
        }
    }
}
