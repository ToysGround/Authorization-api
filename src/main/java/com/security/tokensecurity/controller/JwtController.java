package com.security.tokensecurity.controller;

import com.security.tokensecurity.controller.dto.TokenDto;

import com.security.tokensecurity.service.jwtToken.JwtService;
import lombok.RequiredArgsConstructor;
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
        return tokenDto;
    }

    @PostMapping("/vaild")
    public Boolean vaild(@RequestParam String token){
        //토큰 검증
        return jwtService.vaildToken(token);
    }

    @PostMapping("/refresh")
    public TokenDto refresh(@RequestBody Map map){
        //REFRESH TOKEN 재발급하여 리턴
        System.out.println("MAP :: " + map.toString());
        return jwtService.reissueAccessToken(map);
    }

    @PostMapping("/signOut")
    public boolean signOut(@RequestBody Map map){
        if(jwtService.deleteByhashKey(map.get("refreshTokenKey").toString()) == null){
            return true;
        }
        return false;
    }

    @PostMapping("/checkRefresh")
    public boolean checkRefreshToken(@RequestBody Map map){
        if(jwtService.findByHashKey(map.get("refreshTokenKey").toString()) == null){
            return true;
        }
        return false;
    }
}
