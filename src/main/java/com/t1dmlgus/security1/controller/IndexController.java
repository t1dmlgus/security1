package com.t1dmlgus.security1.controller;


import com.t1dmlgus.security1.model.User;
import com.t1dmlgus.security1.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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



    @GetMapping("/")
    public String index(){

        return "index";
    }

    @GetMapping("/user")
    public @ResponseBody  String user() {

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

}
