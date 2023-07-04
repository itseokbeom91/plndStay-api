package com.example.stay.accommodation.wellihilli.service;

import com.example.stay.common.util.CommonFunction;
import com.example.stay.common.util.Constants;
import com.example.stay.common.util.LogWriter;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;

@Service("wellihilli.BookingService")
public class BookingService {

    CommonFunction commonFunction = new CommonFunction();

    public boolean checkAvailBooking(String pyung, String sDate, String sleep, String roomCount, String roomType){
        LogWriter logWriter = new LogWriter(System.currentTimeMillis());
        String message = "";
        boolean avail = false;

        try{
            String strUrl = Constants.whpUrl + ":8070/api/vapi/reservation/room_resv_possible_check?" +
                    "s_memType=4&s_pyung=" + pyung + "&s_arrday=" + sDate + "&s_nightsu=" + sleep +
                    "&s_roomsu=" + roomCount + "&s_roomType=" + roomType;

            String method = "GET";

            JsonNode jsonNode = commonFunction.callJsonApi("", "", new JSONObject(), strUrl, method);
            String code = jsonNode.get("status").toString();

            if(code.equals("200")){
                JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonNode.get("data").toString());
                String strAvail = jsonObject.get("rtn").toString();
                if(strAvail.equals("Y")){
                    avail = true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            logWriter.add("error : " + e.getMessage());
            logWriter.log(0);
        }
        return avail;
    }
}
