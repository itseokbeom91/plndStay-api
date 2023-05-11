package com.example.stay.common.util;

import lombok.Data;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Data
public class UrlResourceDownloader {

    private String strDownloadPath = ""; // 저장할 경로
    private String strFileUrl = ""; // 다운받을 파일의 url 경로

    public UrlResourceDownloader(String strDownloadPath, String strFileUrl){
        this.strDownloadPath = strDownloadPath;
        this.strFileUrl = strFileUrl;
    }


    public void urlFileDownload(){
        try(InputStream in = new URL(strFileUrl).openStream()){
            Path imagePath = Paths.get(strDownloadPath);
            Files.copy(in, imagePath);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("file download fail");
        }
    }

}
