package com.security.tokensecurity.controller;

import com.security.tokensecurity.common.Com;
import com.security.tokensecurity.controller.dto.TokenDto;
import com.security.tokensecurity.jwt.JwtProvider;
import com.security.tokensecurity.service.jwtToken.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/jwt")
@RestController
@RequiredArgsConstructor
public class JwtController {

    private final JwtService jwtService;
    private final JwtProvider jwtProvider;

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
    public ResponseEntity<?> refresh(@RequestBody Map map, HttpServletRequest request){
        //REFRESH TOKEN 재발급하여 리턴
        Map<String, String> data = new HashMap<>();
        data.putAll(map);
        data.put("accessToken", jwtProvider.resolveJwtToken(request));
        TokenDto tokenDto = jwtService.reissueAccessToken(data);
        return new ResponseEntity<>(Com.inputMap(true,"TOKEN 발급 성공.",tokenDto.getAccessToken()), HttpStatus.OK); //200
    }
}
