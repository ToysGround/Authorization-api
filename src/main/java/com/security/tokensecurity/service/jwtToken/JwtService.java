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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtProvider jwtProvider;
    private final TokenTbRepository tokenTbRepository;

    //@Transactional
    public TokenDto signIn(Map<String,String> map){
        String auth ="";
        TokenDto tokenDto = null;
        switch (map.get("gourpNo")){
            case "1" : auth = "ROLE_USER";
                break;
            case "2" : auth = "ROLE_ADMIN";
                break;
            default: break;
        }
        try {
            GrantedAuthority authorities = new SimpleGrantedAuthority(Authority.valueOf(auth).name());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(map.get("userId"),map.get("userPwd"), Collections.singleton(authorities)) ;
//            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                    userDetails,
//                    userDetails.getPassword(),
//                    userDetails.getAuthorities()
//            ) ;
//            System.out.println("***************************************************************************************");
//            System.out.println("authenticationToken.getName() :: " + authenticationToken.getName());
//            System.out.println("authenticationToken.getCredentials() :: " + authenticationToken.getCredentials());
//            System.out.println("authenticationToken.getPrincipal() :: " + authenticationToken.getPrincipal());
//            System.out.println("authenticationToken.getAuthorities() :: " + authenticationToken.getAuthorities());
//            System.out.println("authenticationToken.getDetails() :: " + authenticationToken.getDetails());
//            System.out.println("***************************************************************************************");
//            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            Authentication authentication = authenticationToken;
            tokenDto = jwtProvider.generateTokenDto(authentication);

            TokenTb tokenEntity = new TokenTb();

            // tokenEntity.setHashKey(Com.changeHashMd5(authenticationToken.getName())+map.get("gourpNo"));
            tokenEntity.setHashKey(Com.changeHashMd5(authenticationToken.getName()+map.get("gourpNo")));
            tokenEntity.setRefreshToken(tokenDto.getRefreshToken());
            tokenEntity.setGourpNo(map.get("gourpNo"));

            tokenTbRepository.save(tokenEntity);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


        return tokenDto;
    }

    @Transactional(readOnly = true)
    public boolean vaildToken(){
        return false;
    }

    @Transactional(readOnly = true)
    public TokenDto reissueAccessToken(Map<String,String> map){
        TokenTb tokenEntity = tokenTbRepository.findByHashKey(map.get("refreshKey"));
        String refreshToken = "";

        if (tokenEntity == null){
            throw new RuntimeException("조회된 토큰이 업습니다.");
        }

        if(!jwtProvider.isTokenValid(tokenEntity.getRefreshToken())){
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
        if(!tokenEntity.getRefreshToken().equals(map.get("userId"))){
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }

        Authentication authentication = jwtProvider.getAuthentication(map.get("accessToken"));

        if(checkExpired(map)){
            refreshToken = reissueRefreshToken(map.get("userId"),tokenEntity).getRefreshToken();
        }else{
            refreshToken = tokenEntity.getRefreshToken();
        }

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        String accessToken = jwtProvider.createJwtAccessToken(map.get("userId"),authorities);

        TokenDto tokenDto =jwtProvider.generateTokenDto(accessToken, refreshToken);

        return tokenDto;
    }

    @Transactional
    public TokenDto reissueRefreshToken(String userId, TokenTb tokenEntity){
        String refreshTokenValue = UUID.randomUUID().toString().replace("-", "");
        String refreshToken = jwtProvider.createJwtRefreshToken(refreshTokenValue);

        TokenDto tokenDto =jwtProvider.generateTokenDto("",refreshToken);

        tokenEntity.setRefreshToken(refreshToken);
        tokenTbRepository.save(tokenEntity);

        return tokenDto;
    }

    public boolean checkExpired(Map<String, String> map){
        Jws<Claims> claimsAcc = jwtProvider.getClaimsFromJwtToken(map.get("accessToken"));
        Jws<Claims> claimsRef = jwtProvider.getClaimsFromJwtToken(map.get("refreshToken"));
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

}
