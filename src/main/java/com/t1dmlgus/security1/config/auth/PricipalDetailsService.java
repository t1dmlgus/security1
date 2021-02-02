package com.t1dmlgus.security1.config.auth;

import com.t1dmlgus.security1.model.User;
import com.t1dmlgus.security1.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl("/login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC 되어있는 loadUserByUsername 함수가 실행

@Service
public class PricipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;




    // **       시큐리티 session(내부 Authentication(내부 UserDetails))      **



    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User userEntity = userRepository.findByUsername(username);

        if(userEntity!=null){
            return new PrincipalDetail(userEntity);
        }
                                        //리턴될 때 Authentication 객체 안에 PrincipalDetail 타입이 들어감

        return null;
    }
}
