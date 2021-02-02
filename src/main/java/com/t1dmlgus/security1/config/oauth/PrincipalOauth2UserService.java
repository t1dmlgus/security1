package com.t1dmlgus.security1.config.oauth;

import com.t1dmlgus.security1.config.auth.PrincipalDetail;
import com.t1dmlgus.security1.config.oauth.provider.FacebookUserInfo;
import com.t1dmlgus.security1.config.oauth.provider.GoogleUserInfo;
import com.t1dmlgus.security1.config.oauth.provider.NaverUserInfo;
import com.t1dmlgus.security1.config.oauth.provider.OAuth2UserInfo;
import com.t1dmlgus.security1.model.User;
import com.t1dmlgus.security1.model.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserRepository userRepository;



    // 구글로 받은 userRequest 데이터에 대한 후처리(회원가입 진행) 되는 함수
    // 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다.

    // 페이스북 | 구글 로그인 -> request가 어떻게 찍히는지 -----

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        System.out.println("getClientRegistration: "+userRequest.getClientRegistration());      // 우리 서버의 기본 정보(registionId로 어떤 OAuth로 로그인 했는지 확인 가능 ex)google, naver ..등)
        System.out.println("getAccessToken: "+userRequest.getAccessToken().getTokenValue());


        System.out.println(">>  getClientName: "+userRequest.getClientRegistration().getClientName());




        //구글 로그인 버튼 클릭 -> 구글 로그인 창 -> 로그인 완료 -> 코드를 리턴(OAuth -> Client라이브러리) -> 코드를 통해서 Access Token 요청
        //여기까지가 userRequest 정보 ->userRequest 정보로 loadUser를 이용해서 ->google로 부터 회원프로필 받을 수 있음
        // 받은 회원프로필로 -> 강제 회원가입 진행

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("> 어트리뷰트 getAttributes: "+oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")){
            System.out.println("구글 로그인 요청");
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")){
            System.out.println("페이스북 로그인 요청");
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        }else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            System.out.println("네이버 로그인 요청");
            oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
        }else {
            System.out.println("우리는 구글과 페이스북, 네이버만 지원합니01다.");
        }


        /**
         *  String provider = userRequest.getClientRegistration().getRegistrationId();
         *         String providerId = oAuth2User.getAttribute("sub");
         *         String username = provider+"_"+providerId;
         *         String password = bCryptPasswordEncoder.encode("겟인데어");
         *         String email = oAuth2User.getAttribute("email");
         *         String role = "ROLE_USER";
         */

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = oAuth2UserInfo.getName();
        String password = bCryptPasswordEncoder.encode("겟인데어");
        String email = oAuth2UserInfo.getEmail();
        String role = "ROLE_USER";


        User userEntity = userRepository.findByUsername(username);

        if (userEntity == null) {

            userEntity = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .role(role)
                    .provider(provider)
                    .providerId(providerId)
                    .build();

            userRepository.save(userEntity);

        }

        // Authentication 객체 안으로 리턴
        return new PrincipalDetail(userEntity, oAuth2User.getAttributes());
    }
}

/*
     >  엑세스 토큰 필요없음 -> 이미 userRequest에 있음
     >  System.out.println("getAttributes: "+super.loadUser(userRequest).getAttributes());

        getAttributes: {sub=116424168713481714110,
          name=이의현,
          given_name=의현,
          family_name=이,
          picture=https://lh6.googleusercontent.com/-fnIUvw8CvRk/AAAAAAAAAAI/AAAAAAAAAAA/AMZuucn9Jb1ZDrQ-Xq5a-wiMmIOzMlGvtw/s96-c/photo.jpg,
          email=dmlgusgngl@gmail.com,
          email_verified=true,
          locale=ko}


      > 강제로 회원가입 진행
        username = "google_116424168713481714110"
        password = "암호화(겟인데어)"
        email = "dmlgusgngl@gmail.com"
        role = "ROLE_USER"
        provider = "google"
        providerId = "116424168713481714110"
*/


