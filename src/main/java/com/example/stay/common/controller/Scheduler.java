package com.example.stay.common.controller;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class Scheduler {

    //@Scheduled(cron = "5 * * * * *")
    public void cron(){

        try {
            System.out.println("another test");

            URL url = new URL("http://localhost:8080/SSG/info?intAID=11471");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
