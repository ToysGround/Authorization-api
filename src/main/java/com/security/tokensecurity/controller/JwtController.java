package com.security.tokensecurity.controller;

import com.security.tokensecurity.controller.dto.TokenDto;
import com.security.tokensecurity.service.jwtToken.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/jwt")
@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;

    @PostMapping("/signIn")
    public TokenDto signIn(@RequestParam Map map){
        //토큰발급하여 리턴
        TokenDto tokenDto = jwtService.signIn(map);
        System.out.println("****************************************");
        System.out.println("tokenDto :: " + tokenDto.getAccessToken());
        System.out.println("tokenDto :: " + tokenDto.getRefreshToken());
        System.out.println("tokenDto :: " + tokenDto.getRefreshTokenKey());
        System.out.println("tokenDto :: " + tokenDto.getGrantType());
        System.out.println("****************************************");
        return tokenDto;
    }

    @PostMapping("/vaild")
    public Boolean vaild(@RequestParam String token){
        //토큰 검증
        return jwtService.vaildToken(token);
    }

    @PostMapping("/refresh")
    public void refresh(@RequestParam String token){
        //REFRESH TOKEN 재발급하여 리턴
    }
}
