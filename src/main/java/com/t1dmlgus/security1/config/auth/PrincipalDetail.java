package com.t1dmlgus.security1.config.auth;

// 시큐리티 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 시큐리티 session을 만들어줍니다. (Security ContextHolder)
// 오브젝트 -> Authentication 타입 객체
// Authentication 안에 User 정보가 있어야 됨.

// User 오브젝트타입 -> UserDetail 타입 객체

// Security Session => Authentication -> UserDetails(PrincipalDetail)

import com.t1dmlgus.security1.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.yaml.snakeyaml.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Collection;

public class PrincipalDetail implements UserDetails {

    private User user;  // 컴포지션


    public PrincipalDetail(User user) {
        this.user = user;
    }


    // 해당 User의 권한 을 리턴하는 곳!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });

        return collect;
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        return user.getUsername();
    }

    
    // 계정 만료 확인
    @Override
    public boolean isAccountNonExpired() {

        return true;
    }

    // 계정 잠겼니?
    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    // 계정이 기간이 지났니?
    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }

    // 계정이 활성화 되있니?
    @Override
    public boolean isEnabled() {

        // 우리 사이트~ 1년동안 회원이 로그인을 안하면! 휴먼계정으로 돌리기로 함
        // 현재시간 - 로그인시간 = 1시간 초과하면 -> return false;

        return true;
    }
}
