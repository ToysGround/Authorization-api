package com.security.tokensecurity.common;

import java.security.MessageDigest;
import java.util.HashMap;

public class Com {
    public static HashMap<String,Object> inputMap(boolean success, String message, Object data){
        HashMap<String, Object> map = new HashMap<>();
        map.put("success", success);
        map.put("message", message);
        map.put("data", data);
        return map;
    }
    public static String changeHashMd5(String value){
        String result = "";
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte by[] = md.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < by.length;i++){
                sb.append(Integer.toString((by[i] & 0xff)+0x100,16).substring(1));
            }
            result = sb.toString();

        }catch (Exception e){
            e.printStackTrace();
            result = null;
        }

        System.out.println("HashCode :: " + result);

        return result;
    }
}
