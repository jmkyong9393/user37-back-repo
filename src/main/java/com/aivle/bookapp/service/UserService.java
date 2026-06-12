package com.aivle.bookapp.service;

import com.aivle.bookapp.domain.User;
import com.aivle.bookapp.dto.request.RefreshTokenRequest;
import com.aivle.bookapp.dto.request.UserLoginRequest;
import com.aivle.bookapp.dto.request.UserProfileUpdateRequest;
import com.aivle.bookapp.dto.request.UserRegisterRequest;
import com.aivle.bookapp.dto.response.RefreshTokenResponse;
import com.aivle.bookapp.dto.response.UserLoginResponse;
import com.aivle.bookapp.dto.response.UserProfileResponse;
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

        String accessToken = jwtUtil.createAccessToken(user);
        String refreshToken = jwtUtil.createRefreshToken(user);

        return UserLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .build();
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse refresh(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 유효하지 않습니다.");
        }

        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("리프레시 토큰이 아닙니다.");
        }

        String userId = jwtUtil.getUserId(refreshToken);

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String newAccessToken = jwtUtil.createAccessToken(user);

        return new RefreshTokenResponse(newAccessToken);
    }

    /**
     * 사용자의 프로필 정보를 조회합니다.
     *
     * @param userId 조회하고자 하는 사용자의 ID
     * @return 패스워드가 제외된 사용자 프로필 정보 응답 DTO (UserProfileResponse)
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        return UserProfileResponse.from(user);
    }

    /**
     * 사용자의 프로필 정보(닉네임, 이메일, 패스워드)를 수정합니다.
     * 수정 요청 필드가 null이거나 비어있지 않은 경우에만 수정을 진행합니다. (부분 변경 지원)
     *
     * @param userId 수정을 요청하는 사용자의 ID
     * @param request 수정할 정보를 담은 DTO (UserProfileUpdateRequest)
     * @return 수정 완료 후 패스워드가 제외된 사용자 프로필 정보 응답 DTO (UserProfileResponse)
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Transactional
    public UserProfileResponse updateProfile(String userId, UserProfileUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 닉네임 변경 요청이 존재하고 유효한 경우 업데이트
        if (request.getNickname() != null && !request.getNickname().isBlank()) {
            user.setNickname(request.getNickname());
        }
        // 이메일 변경 요청이 존재하고 유효한 경우 업데이트
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail());
        }
        // 비밀번호 변경 요청이 존재하고 유효한 경우 업데이트
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }

        return UserProfileResponse.from(user);
    }
}
