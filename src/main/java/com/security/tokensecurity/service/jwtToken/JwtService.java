package com.security.tokensecurity.service.jwtToken;

import com.security.tokensecurity.common.Com;
import com.security.tokensecurity.controller.dto.TokenDto;
import com.security.tokensecurity.domain.entity.Authority;
import com.security.tokensecurity.domain.entity.TokenTb;
import com.security.tokensecurity.domain.repository.TokenTbRepository;
import com.security.tokensecurity.jwt.JwtProvider;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JwtService {
    private final JwtProvider jwtProvider;
    private final TokenTbRepository tokenTbRepository;

    public TokenDto signIn(Map<String,String> map){
        String auth ="";
        TokenDto tokenDto = null;
        switch (map.get("serviceNo")){
            case "1" : auth = "ROLE_USER";
                break;
            case "2" : auth = "ROLE_ADMIN";
                break;
            default: break;
        }
        try {
            GrantedAuthority authorities = new SimpleGrantedAuthority(Authority.valueOf(auth).name());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(map.get("userId"),map.get("userPwd"), Collections.singleton(authorities));

            Authentication authentication = authenticationToken;

            String hashKey = Com.changeHashMd5(authenticationToken.getName()+map.get("serviceNo")+((Math.random()*10000)+1));
            tokenDto = jwtProvider.generateTokenDto(authentication, hashKey);

            TokenTb tokenEntity = new TokenTb();

            tokenEntity.setHashKey(hashKey);
            tokenEntity.setRefreshToken(tokenDto.getRefreshToken());
            tokenEntity.setGourpNo(map.get("serviceNo"));

            tokenTbRepository.save(tokenEntity);

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

        return tokenDto;
    }

    public boolean vaildToken(String token) {
        return jwtProvider.isTokenValid(token);
    }

    @Transactional(readOnly = true)
    public TokenDto reissueAccessToken(Map<String,String> map){
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        System.out.println("map :: "+ map.toString());
        String mapRefreshTokenKey = String.valueOf(map.get("refreshTokenKey")).replaceAll(match," ").trim();
        String mapAccessToken = String.valueOf(map.get("accessToken")).replaceAll("\\[","").replaceAll("\\]","");
        System.out.println("refreshTokenKey ::" +mapRefreshTokenKey);
        System.out.println("mapAccessToken ::" +mapAccessToken);

        TokenTb tokenEntity = tokenTbRepository.findByHashKey(mapRefreshTokenKey);
        String refreshToken = tokenEntity.getRefreshToken();;
        String userId = jwtProvider.getUserId(refreshToken);


        if (tokenEntity == null){
            throw new RuntimeException("조회된 토큰이 업습니다.");
        }

        if(!jwtProvider.isTokenValid(refreshToken)){
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
       /* 토큰 테이블에 유저정보는 관리안함
        if(!tokenEntity.getRefreshToken().equals(userId)){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }*/

        Authentication authentication = jwtProvider.getAuthentication(refreshToken);
        map.put("refreshToken",refreshToken);

        /* 해당부분 사용할 시 리프레쉬 토큰 복호화 생각해보고 결정해야함
        if(checkExpired(map)){
            refreshToken = reissueRefreshToken(userId,tokenEntity).getRefreshToken();
        }else{
            refreshToken = tokenEntity.getRefreshToken();
        }*/
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = jwtProvider.createJwtAccessToken(userId,authorities);

        TokenDto tokenDto =jwtProvider.generateTokenDto(accessToken, refreshToken, mapRefreshTokenKey);

        return tokenDto;
    }

    @Transactional
    public TokenDto reissueRefreshToken(String userId, TokenTb tokenEntity){
        String refreshTokenValue = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = jwtProvider.createJwtRefreshToken(userId,refreshTokenValue);

        TokenDto tokenDto =jwtProvider.generateTokenDto("",refreshToken,tokenEntity.getHashKey());

        tokenEntity.setRefreshToken(refreshToken);
        tokenTbRepository.save(tokenEntity);

        return tokenDto;
    }

    public boolean checkExpired(Map<String, String> map){
        System.out.println("map :: " + String.valueOf(map.get("accessToken")));
        String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";
        //System.out.println("map :: "+ map.toString());
        String mapAccessToken = String.valueOf(map.get("accessToken")).replaceAll("\\[","").replaceAll("\\]","");
        String mapRefreshToken = String.valueOf(map.get("refreshToken")).replaceAll(match," ").trim();


        Jws<Claims> claimsAcc = jwtProvider.getClaimsFromJwtToken(mapAccessToken);
        System.out.println("*************************************************************");
        Jws<Claims> claimsRef = jwtProvider.getClaimsFromJwtToken(mapRefreshToken);
        System.out.println("*************************************************************");
        Date accDt = claimsAcc.getBody().getExpiration();
        Date reDt = claimsRef.getBody().getExpiration();
        long accessDt = accDt.getTime();
        long refreshDt = reDt.getTime();
        if(((float)refreshDt-(float)accessDt)/60/60 < 1 ){
            return true;
        }else{
            return false;
        }
    }

    public TokenTb deleteByhashKey(String hashKey){
       return tokenTbRepository.deleteByHashKey(hashKey);
    }

    public TokenTb findByHashKey(String hashKey){
        return tokenTbRepository.findByHashKey(hashKey);
    }

}
