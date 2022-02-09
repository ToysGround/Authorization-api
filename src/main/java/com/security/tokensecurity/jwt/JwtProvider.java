package com.security.tokensecurity.jwt;

import com.security.tokensecurity.controller.dto.TokenDto;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    final String URL_LOCAL = "http://localhost:8080/user/searchId";
    private String secretKey = "wlsghks"; //시크릿키 다른방법으로 설정하는거 생각해야함
    private final long ACCESS_TOKEN_VALID_TIME = 30 * 60 * 1000L; // 30분
    private final long REFRESH_TOKEN_VALID_TIME = 24 * 60 * 60 * 1000L; // 24시간
    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_TYPE = "bearer";


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public TokenDto generateTokenDto(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        String refreshTokenValue = UUID.randomUUID().toString().replace("-", "");

        String accToken = createJwtAccessToken(authentication.getName(),authorities);
        String reToken = createJwtRefreshToken(refreshTokenValue);

        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accToken)
                .refreshToken(reToken)
                .build();
    }

    public TokenDto generateTokenDto(String accessToken, String refreshToken){
        return TokenDto.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public String createJwtAccessToken(String userId, String roles){
        Claims claims  = Jwts.claims().setSubject(userId);
        claims.put("roles", roles);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_VALID_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createJwtRefreshToken(String value){
        Claims claims = Jwts.claims();
        claims.put("value",value);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + REFRESH_TOKEN_VALID_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String resolveJwtToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    public Authentication getAuthentication(String token){
        Jws<Claims> claims = getClaimsFromJwtToken(token);
        if(claims.getBody().get(secretKey) == null){
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.getBody().get(secretKey).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails userDetails = new User(claims.getBody().getSubject(),"",authorities);

        return new UsernamePasswordAuthenticationToken(userDetails,"",authorities);
    }

    public String getUserId(String token){
        return getClaimsFromJwtToken(token).getBody().getSubject();
    }

    public boolean isTokenValid(String jwtToken){
        try {
            Jws<Claims> claims = getClaimsFromJwtToken(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        }catch (SecurityException | MalformedJwtException e){
            log.info("잘못된 JWT 서명입니다.");
        }catch (ExpiredJwtException e){
            log.info("만료된 JWT 토큰입니다.");
        }catch (UnsupportedJwtException e){
            log.info("지원되지 않는 JWT 토큰입니다.");
        }catch (IllegalArgumentException e){
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    public Jws<Claims> getClaimsFromJwtToken(String jwtToken){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
    }

    public String getUserEntity(){
        List<HttpMessageConverter<?>> converts = new ArrayList<HttpMessageConverter<?>>();
        converts.add(new FormHttpMessageConverter());
        converts.add(new StringHttpMessageConverter());

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(converts);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String,String>();
        map.add("id", "admin");

        String result = restTemplate.postForObject(URL_LOCAL, map, String.class) ;
        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.println(result.toString());
        System.out.println("------------------------------------------------------------------------------------------------------");
        return result;
    }

}
