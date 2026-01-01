package com.example.reservation_system.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import com.example.reservation_system.dto.request.UserSignUpRequest;
import com.example.reservation_system.dto.response.UserResponse;
import com.example.reservation_system.entity.User;
import com.example.reservation_system.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //이 서비스 안의 모든 메서드는 기본적으로 읽기만 할것을 선언. 성능최적화를 해준다(dirty checkong, 데이터변겅을 감시하는건데, 어차피 안 바뀌니 감시를 안 한다는 뜻) 
public class UserService {


    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    
    /**
     * 회원가입
     * @param userDto
     * @return User
     */ 
    @Transactional  //DB에 쓰는 작업이므로, 중간에 오류가 났을 때 모든 과정을 취소하기 위해 꼭 필요하다.  (readOnly = false로 바뀌는 것!그래서 얘만 체크할듯)
    public UserResponse createUser(UserSignUpRequest signUpDto){ //controller에서 api로 날아온 정보를 dto로 매핑하여 service로 전달한다.  controller는 외부와의 소통(http, json,인증)을, service는 핵심로직(데이터 가공, db저장, 로직) 을 담당한다.

        String nickname = signUpDto.getNickname();
        if (nickname == null || nickname.trim().isEmpty()) {
            nickname = signUpDto.getLoginId();
        }

        validateDuplicate(signUpDto.getLoginId(), signUpDto.getEmail(), nickname);

        User user = User.builder()
                        .loginId(signUpDto.getLoginId())
                        .password(passwordEncoder.encode(signUpDto.getPassword()))
                        .nickname(nickname)
                        .email(signUpDto.getEmail())
                        .phoneNumber(signUpDto.getPhoneNumber())
                        .build();

       try {
            // 3. [동시성/성능 관점] 최종 저장 및 예외 처리
            User savedUser = userRepository.save(user);
            return UserResponse.from(savedUser);//보안 유지를 위해 Entity -> Response DTO로 변환 
        } catch (DataIntegrityViolationException e) {
            // exists 체크를 통과했어도 찰나의 순간에 중복이 발생했을 경우 방어
            throw new IllegalArgumentException("이미 사용 중인 아이디, 이메일 또는 닉네임입니다.");
        }

    } // UserRequestDTO -> createUser() -> UserResponseDTO 구조 
    
    private void validateDuplicate(String loginId, String email, String nickname) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }
    //회원가입 하는데 DB통신이 4번 -> 대규모 트래픽시 구체적인 메시지는 생략하는 것이 좋다. 

    //로그인 

}
