package com.example.stay.openMarket.common.dto;

import lombok.Data;

@Data
public class ContentsPhotoDto {

    /**
     * DB CONTENTS_PHOTO
     */

    private int intPhotoID;
    private String strSubject;
    private String strFilePath;
    private String strFileName;
    private int intCreatedSID;
//    private String strDateCreated; // dateCreated
    private int intModifiedSID;
//    private String strDateModified; // dateModified

    /**
     * DB CONDO_PHOTO
     */
    private int intStep;

}
