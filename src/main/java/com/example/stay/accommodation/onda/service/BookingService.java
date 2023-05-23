package com.example.stay.accommodation.onda.service;

import com.example.stay.accommodation.onda.mapper.BookingMapper;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    @Autowired
    private BookingMapper bookingMapper;

    private Logger logger = LoggerFactory.getLogger(BookingService.class);

    // 예약 전 예약 가능 여부 조회
    public boolean checkAvailBooking(String propertyId, String roomTypeId, String ratePlanId, String checkInDate, String checkOutDate){
        boolean availability = false;

        try{
            long startTime = System.currentTimeMillis();

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://dapi.tport.dev/gds/diglett/properties/" + propertyId + "/roomtypes/" + roomTypeId + "/rateplans/" + ratePlanId + "/checkavail?checkin=" + checkInDate + "&checkout=" + checkOutDate)
                    .get()
                    .addHeader("accept", "application/json")
                    .addHeader("Authorization", Constants.ondaAuth)
                    .build();

            Response response = client.newCall(request).execute();

            if(response.isSuccessful()){
                // response 파싱
                String responseBody = response.body().string();
//                System.out.println("responseBody : " + responseBody);

                JSONParser jsonParser = new JSONParser();
                JSONObject responseJson = (JSONObject) jsonParser.parse(responseBody);

                availability = (boolean) responseJson.get("availability");
                JSONArray datesArr = (JSONArray) responseJson.get("dates");
                String dates = "";
                for(int i=0; i<datesArr.size(); i++){
                    JSONObject datesJson = (JSONObject) datesArr.get(i);
                    String date = datesJson.get("date").toString();
                    int vacancy = Integer.parseInt(datesJson.get("vacancy").toString());

                    dates += "availability : " + availability + "예약 가능 재고 : " + vacancy + "개";
                }

                LogWriter logWriter = new LogWriter("", request.url().toString(), startTime);
                logWriter.add("asdlkjfa;lkdjf;alsdjf;lakjfdl;");
                logWriter.add("asdlkjfa;lkdjf;alsdjf;lakjfdl;");
                logWriter.add("asdlkjfa;lkdjf;alsdjf;lakjfdl;");
                logWriter.add(dates);
                logWriter.log(0);


            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("예약 가능 여부 조회 실패");

        }
        return availability;
    }
}
