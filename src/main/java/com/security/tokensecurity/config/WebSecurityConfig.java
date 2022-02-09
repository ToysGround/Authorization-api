package com.security.tokensecurity.config;

import com.security.tokensecurity.jwt.JwtAccessDeniedHandler;
import com.security.tokensecurity.jwt.JwtAuthenticationEntryPoint;
import com.security.tokensecurity.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtProvider jwtProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CSRF 설정 DISABLE
        http.csrf().disable()

                // EXCEPTION HANDLING 할 때 내가 만든 클래스를 추가
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                //H2-console을 위한 설정을 추가
                /*
                .and()
                .headers()
                .frameOptions()
                .sameOrigin()
                */

                //시큐리티는 기본적으로 세션을 사용
                // 여기서는 세션을 사용하지 않기 떄문에 세션 설정을 Stateless로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //로그인, 회원가입 API는 토큰이 없는 상태에서 요청이 들어오기 떄문에 permitAll 설정
                .and()
                .authorizeRequests()
                .antMatchers("/token/**").permitAll()
                .antMatchers("/jwt/**").permitAll()
                .anyRequest().authenticated() // 나머지 API는 전부 인증 필요

                //JwtFilter를 addFilterBefore로 등록했던 JwtSecurityConfig클래스를 적용
                .and()
                .apply(new JwtSecurityConfig(jwtProvider));
    }
}
