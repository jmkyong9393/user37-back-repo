package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.User;
import com.aivle.bookapp.dto.request.UserLoginRequest;
import com.aivle.bookapp.dto.request.UserRegisterRequest;
import com.aivle.bookapp.dto.response.UserLoginResponse;
import com.aivle.bookapp.dto.response.UserRegisterResponse;
import com.aivle.bookapp.exception.UserNotFoundException;
import com.aivle.bookapp.repository.UserRepository;
import com.aivle.bookapp.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public UserRegisterResponse register(UserRegisterRequest request) {
        Optional<User> findUser = userRepository.findByUserId(request.getUserId());

        if (findUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .userId(request.getUserId())
                .password(request.getPassword())
                .name(request.getName())
                .email(request.getEmail())
                .nickname(request.getNickname())
                .build();

        User saved = userRepository.save(user);

        return UserRegisterResponse.builder()
                .userId(saved.getUserId())
                .name(saved.getName())
                .email(saved.getEmail())
                .nickname(saved.getNickname())
                .build();
    }

    @Transactional(readOnly = true)
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        if(!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(user);

        return UserLoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

}
