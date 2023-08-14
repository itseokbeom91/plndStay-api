package com.example.stay.openMarket.coupang.hmac;

import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.example.stay.common.util.Constants;
import org.apache.commons.codec.binary.Hex;

public class HmacGenerater {
    public HmacGenerater() {
    }

    public static String generate(String method, String uri, String secretKey, String accessKey) {
        String[] parts = uri.split("\\?");
        if (parts.length > 2) {
            throw new RuntimeException("incorrect uri format");
        } else {
            String path = parts[0];
            String query = "";
            if (parts.length == 2) {
                query = parts[1];
            }

            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyMMdd'T'HHmmss'Z'");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datetime = dateFormatGmt.format(new Date());
            String message = datetime + method + path + query;

            String signature;
            try {
                SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(Charset.forName("UTF-8")), Constants.HMAC_SHA_256);
                Mac mac = Mac.getInstance(Constants.HMAC_SHA_256);
                mac.init(signingKey);
                byte[] rawHmac = mac.doFinal(message.getBytes(Charset.forName("UTF-8")));
                signature = Hex.encodeHexString(rawHmac);
            } catch (GeneralSecurityException var14) {
                throw new IllegalArgumentException("Unexpected error while creating hash: " + var14.getMessage(), var14);
            }

            String result = String.format("CEA algorithm=%s, access-key=%s, signed-date=%s, signature=%s", Constants.HMAC_SHA_256, accessKey, datetime, signature);
            return result;
        }
    }

}

