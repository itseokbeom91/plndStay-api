package com.example.stay.common.util;

import com.example.stay.accommodation.onda.service.BookingService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class LogWriter {

    private static final Logger logger = LoggerFactory.getLogger(LogWriter.class);

    private StringBuilder sb;

    private String apiUrl;
    private String method; // api 호출 방식
    private String logMessage;
    private long startTime; // 로직 프로세스 시작 시간
    private long endTime; //  로직 프로세스 완료 시간
    private String requestTime; // 걸린 시간

    public static final String logHead  = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    public static final String logBody  = "┃";
    public static final String logFoot  = "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";




    public LogWriter() {
        this.startTime = System.currentTimeMillis();
        this.requestTime = getRequestTime();
    }

    public LogWriter(String method, String apiUrl, long startTime){
        this.method = (method != null) ? method : "";
        this.apiUrl = (apiUrl != null) ? apiUrl : "";
        this.startTime = startTime;
//        this.endTime = System.currentTimeMillis();
    }

    public void add(String msg) {
        if (logMessage != null) {
            this.logMessage += logBody + "  ".concat(msg.concat("\r\n"));
        }
        else {
            this.logMessage = logBody + "  ".concat(msg.concat("\r\n"));
        }
    }

    public String makeLog(){
        this.endTime = System.currentTimeMillis();

        sb = new StringBuilder();

        String log = "";
        sb.append("\r\n");
        sb.append(logHead + "\r\n");
        sb.append(logBody + "  method : " + getMethod() + "\r\n");
        sb.append(logBody + "  apiUrl : " + getApiUrl() + "\r\n");
        sb.append(logBody + "  requestTime : " + requestTime + "\r\n\n");

        if(getLogMessage() != null){
            sb.append(getLogMessage());
        }

        sb.append(logFoot + "\r\n");

        log = sb.toString();

        return log;
    }


    String getRequestTime() {
        SimpleDateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");
        return dayTime.format(new Date(getStartTime()));
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
