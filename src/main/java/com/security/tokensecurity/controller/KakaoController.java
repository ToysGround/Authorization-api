package com.security.tokensecurity.controller;

import com.security.tokensecurity.service.kakaoToken.KakaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

public class KakaoController {
    @Autowired
    private KakaoService kakaoService;

    @RequestMapping("/login")
    public String home(@RequestParam(value = "code", required = false) String code) throws Exception{
        System.out.println("#########" + code);
        String access_Token = kakaoService.getAccessToken(code);
        System.out.println("###access_Token#### : " + access_Token);
        return "testPage";
    }
}
