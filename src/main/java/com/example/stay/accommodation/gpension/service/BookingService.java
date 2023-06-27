package com.example.stay.accommodation.gpension.service;

import com.example.stay.accommodation.gpension.mapper.BookingMapper;
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
        return commonFunction.makeReturn("", "");
    }

    public String confirmBooking(int intBookingIdx) {
        //order_no (예약번호) 만 태움
        return commonFunction.makeReturn("", "");
    }

    public String cancelBooking(int intBookingIdx) {
        //order_no (예약번호) 만 태움
        return commonFunction.makeReturn("", "");
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
            return commonFunction.makeReturn("", "", "");
        }
    }

    public String searchOrder(int intBookingIdx) {
        //order_no (예약번호) 만 태움
        return commonFunction.makeReturn("", "");
    }
}
