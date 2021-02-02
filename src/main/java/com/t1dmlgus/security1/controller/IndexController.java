package com.t1dmlgus.security1.controller;


import com.t1dmlgus.security1.config.auth.PrincipalDetail;
import com.t1dmlgus.security1.model.User;
import com.t1dmlgus.security1.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

/*
    > 일반 로그인 후 -> /test/login 로 로그인(인증)된 객체가 넘어와서

    1. PrincipalDetail(userDetails) 타입이 ->(cast 연산자) -> Authentication 객체로 들어감 -> getUser();

    2. @AuthenticationPricipal 어노테이션 사용해서 PrincipalDetail(userDetails) 타입 클래스.getUser();
    
 */

 
    @GetMapping("/test/login")
    public @ResponseBody String loginTest(Authentication authentication,
                                          @AuthenticationPrincipal PrincipalDetail userDetails){
        System.out.println("/test/login ---------------------  ");
        PrincipalDetail principalDetail = (PrincipalDetail) authentication.getPrincipal();
        System.out.println("authentication:" + principalDetail.getUser());


        System.out.println("userDetails = " + userDetails.getUser());

        return "세션 정보 확인하기";
    }
    
/*
    > 구글 로그인 후 -> /test/oauth/login 로 로그인(인증)된 객체가 넘어와서

    1. PrincipalDetail(userDetails) 타입이 ->(cast 연산자) -> Authentication 객체로 들어감 -> getAttributes();

    2. @AuthenticationPricipal 어노테이션 사용해서 PrincipalDetail(userDetails) 타입 클래스.getAttributes();

 */

    @GetMapping("/test/oauth/login")
    public @ResponseBody String loginTest(Authentication authentication,
                                          @AuthenticationPrincipal OAuth2User oauth){

        System.out.println("/test/oauth/login ---------------------  ");

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication:" + oauth2User.getAttributes());


        System.out.println("oauth2User = " + oauth.getAttributes());


        return "OAuth2 세션 정보 확인하기";
    }



    @GetMapping("/")
    public String index(){

        return "index";
    }

    // OAuth 로그인을 해도 PrincipalDetails
    // 일반 로그인을 해도 PrincipalDetails

    @GetMapping("/user")
    public @ResponseBody
    String user(@AuthenticationPrincipal PrincipalDetail principalDetail) {
        System.out.println("principalDetail.getUser() = " + principalDetail.getUser());
        return "user";
    }

    @GetMapping("/admin")
    public @ResponseBody  String admin() {

        return "admin";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {

        return "manager";
    }

    // 스프링시큐리티가 해당주소를 낚아챔    - > SecurityConfig 파일 생성 후 작동 안함
    @GetMapping("/loginForm")
    public String loginForm() {

        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {

        return "joinForm";
    }

    @PostMapping("/join")
    public String join(User user) {
        System.out.println(user);
        user.setRole("ROLE_USER");
        String rawPassword = user.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        user.setPassword(encPassword);

        userRepository.save(user);  // 회원가입은 되지만, 비밀번호: 1234 -> 시큐리티로 로그인 할 수 가 없음(암호화가 안됬음)
        return "redirect:/loginForm";
    }


    @Secured("ROLE_ADMIN")
    @GetMapping("/info")
    public @ResponseBody String info(){

        return "개인정보";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/data")
    public @ResponseBody String data(){

        return "데이터정보";
    }




}
