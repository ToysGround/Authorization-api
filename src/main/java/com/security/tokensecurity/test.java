package com.security.tokensecurity;

import org.springframework.boot.SpringApplication;

import java.util.Calendar;
import java.util.Date;

public class test {

    public static void main(String[] args) {
        long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L; // 30분
        long REFRESH_TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L; // 24시간.
        long da = 60 * 60 * 1000L;

        Date now = new Date();
        Date expiration1 = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME);
        Date expiration2 = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);
        long accessDt = expiration1.getTime()/1000;
        long refreshDt = expiration2.getTime()/1000;
        long compareDt = da/1000;



        System.out.println("***************************************************************");
        System.out.println("Date :: " + now.getTime());
        System.out.println("Date2 :: " + (now.getTime() + ACCESS_TOKEN_VALID_TIME)/1000);
        System.out.println("Date3 :: " + (now.getTime() + REFRESH_TOKEN_VALID_TIME)/1000);
        System.out.println("da :: " + da/1000);

        System.out.println("accessDt :: " + accessDt);
        System.out.println("refreshDt :: " + refreshDt);
        System.out.println("compareDt :: " + compareDt);

        System.out.println("compare :: " + (refreshDt - accessDt)/60/60);

        System.out.println("expiration1 :: " + expiration1);
        System.out.println("expiration2 :: " + expiration2);

        Date a = new Date(2022-1900, Calendar.FEBRUARY,9,20,8);
        long b = a.getTime()/1000;
        Date c = new Date(a.getTime());
        float compareTime = ((float)refreshDt - (float)b)/60/60;
        System.out.println("b :: " + b);
        System.out.println("c :: " + c);
        if(compareTime<1){
            System.out.println("1시간 이하임 :: " + String.format("%.2f",compareTime));
        }else{
            System.out.println("아직임 :: " + String.format("%.2f",compareTime));
        }

    }
}
