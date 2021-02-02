package com.t1dmlgus.security1.config;


import com.t1dmlgus.security1.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity                                                   // 스프링 시큐리티 필터가 스프링 필터체인에 등록
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)  //@secure 어노테이션 활성화, preAuthorize, postAuthorize 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {   // 스프링 시큐리티 필터

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;



    // 패스워드 암호화


    // 해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다.
    @Bean
    public BCryptPasswordEncoder encodePwd(){

        return new BCryptPasswordEncoder();
    }



    // 1. 코드 받고(인증),
    // 2. 코드 받은 걸로 엑세스 토큰을 받음(권한),
    // 3. 권한을 통해서 사용자 프로필 정보를 가져옴,
    // 4-1. 가져온 정보(이메일, 전화번호, 이름, 아이디)를 토대로 회원가입을 자동으로 진행시키기도 함
    // 4-2. 모자란 정보(집주소, vip등급,일반등급)등 -> 추가적인 정보 필요하면 추가적인 창이 나와서 회원가입을 진행

    // Tip : 구글 로그인이 완료가 되면 코드를 받는게 아니라 (엑세스 토큰 + 사용자 프로필 정보 ㅇ)


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
        http
                .authorizeRequests()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/manager/**")
                .access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해준다.
                .defaultSuccessUrl("/")         // 로그인이 완료가 되면 메인페이지로 가도록
                .and()
                .oauth2Login()
                .loginPage("/loginForm")       // 구글 로그인이 완료된 후 후처리가 필요!
                .userInfoEndpoint()
                .userService(principalOauth2UserService);       // 후처리 -> 회원가입진행



    }
}
