package com.example.stay.common.util;

import com.example.stay.accommodation.onda.service.BookingService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class LogWriter {

    private static final Logger logger = LoggerFactory.getLogger(LogWriter.class);

    private StringBuilder sb;
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String apiUrl;
    private String apiQuery;
    private String method; // api 호출 방식
    private String logMessage;
    private long startTime; // 로직 프로세스 시작 시간
    private long endTime; //  로직 프로세스 완료 시간
    private double processTime; // 걸린 시간
    private String requestData; // request data
    private String responseData; // response data

    public static final String logHead  = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    public static final String logBody  = "┃";
    public static final String logBlock = "┡━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    public static final String logFoot  = "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    public LogWriter(long startTime){
        this.startTime = startTime;
    }

    public LogWriter(String method, String apiUrl, long startTime){
        this.startTime = startTime;
        this.method = (method != null) ? method : "";
        this.apiUrl = (apiUrl != null) ? apiUrl : "";
    }

    public LogWriter(String method, String apiUrl, String apiQuery, long startTime){
        this.startTime = startTime;
        this.method = (method != null) ? method : "";
        this.apiUrl = (apiUrl != null) ? apiUrl : "";
        this.apiQuery = (apiQuery != null) ? apiQuery : "";
    }

    public void add(String msg) {
        if (logMessage != null) {
            this.logMessage += msg.concat("\r\n");
        }
        else {
            this.logMessage = msg.concat("\r\n");
        }
    }

    public void addRequest(String requestData) {
        if (requestData != null) {
            this.requestData += requestData.concat("\r\n");
        }
        else {
            this.requestData = requestData.concat("\r\n");
        }
    }

    public String makeLog(){
        this.processTime = (System.currentTimeMillis()-this.startTime)/1000.0;
        String log = "";
        try{
            sb = new StringBuilder();

            sb.append("\r\n");
            sb.append(logHead + "\r\n");
            sb.append(logBody + "  method : " + getMethod() + "\r\n");
            sb.append(logBody + "  apiUrl : " + getApiUrl() + "\r\n");
            sb.append(logBody + "  apiQuery : " + getApiQuery() + "\r\n");
            sb.append(logBody + "  startTime : " + df.format(new Date(this.startTime)) + "\r\n");
            sb.append(logBody + "  processTime : " + this.processTime + "\r\n" + logBlock + "\r\n");

            String requestLine = getRequestData();
            if(getRequestData() != null){
                sb.append(logBody + "  [Request] \r\n");
                sb.append(logBody + "\r\n");
                BufferedReader br = new BufferedReader(new StringReader(getRequestData()));
                while ((requestLine = br.readLine()) != null) {
                    sb.append(logBody + "  " + requestLine);
                    sb.append("\n\r");
                }
                sb.append(logBlock + "\r\n");
            }

            String messageLine = getLogMessage();
            if(getLogMessage() != null){
                sb.append(logBody + "  [Response] \r\n");
                sb.append(logBody + "\r\n");
                BufferedReader br = new BufferedReader(new StringReader(getLogMessage()));
                while ((messageLine = br.readLine()) != null) {
                    sb.append(logBody + "  " + messageLine);
                    sb.append("\n\r");
                }
            }
            sb.append(logFoot + "\r\n");

            log = sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }

        return log;
    }


    public void log(int level) {
        if (makeLog() != null) {
            switch (level) {
                case 0:
                    logger.info(makeLog());
                    break;
                case 1:
                    logger.debug(makeLog());
                    break;
                case 2:
                    logger.warn(makeLog());
                    break;
                case 3:
                    logger.error(makeLog());
                    break;
            }
        }
    }
}
