package com.rojoxpress.gurallery.utils;

import java.util.HashMap;
import java.util.Map;

public class GurHeaders {

    public static Map<String,String> getHeader(){

        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization", "Client-ID a3ae22da6d0c0e5");
        return headers;
    }
}
