package com.itime.team.itime.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by mac on 16/1/14.
 */
public class URLConnectionUtil {
    public static String encode(String str){
        String encode = "";
        try {
            encode = URLEncoder.encode(str,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }
}
