package com.security.tokensecurity.controller;

import com.security.tokensecurity.controller.dto.TokenDto;
import com.security.tokensecurity.service.jwtToken.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequestMapping("/jwt")
@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/signIn")
    public TokenDto signIn(@RequestParam Map map){
        //토큰발급하여 리턴
        return jwtService.signIn(map);
    }

    @PostMapping("/vaild")
    public String vaild(){
        //토큰 검증
        return null;
    }

    @PostMapping("/refresh")
    public void refresh(){
        //REFRESH TOKEN 재발급하여 리턴
    }
}
