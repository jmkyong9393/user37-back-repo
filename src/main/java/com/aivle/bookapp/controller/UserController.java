package com.aivle.bookapp.controller;

import com.aivle.bookapp.domain.User;
import com.aivle.bookapp.dto.request.RefreshTokenRequest;
import com.aivle.bookapp.dto.request.UserLoginRequest;
import com.aivle.bookapp.dto.request.UserProfileUpdateRequest;
import com.aivle.bookapp.dto.request.UserRegisterRequest;
import com.aivle.bookapp.dto.response.RefreshTokenResponse;
import com.aivle.bookapp.dto.response.UserLoginResponse;
import com.aivle.bookapp.dto.response.UserProfileResponse;
import com.aivle.bookapp.dto.response.UserRegisterResponse;
import com.aivle.bookapp.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.findAll();
    }

    @PostMapping("/register")
    public ResponseEntity<UserRegisterResponse> register(@Valid @RequestBody UserRegisterRequest request) {
        UserRegisterResponse res = userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse res = userService.login(request);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse res = userService.refresh(request);
        return ResponseEntity.ok(res);
    }

    /**
     * 마이페이지 - 현재 로그인한 사용자의 프로필 정보를 조회합니다.
     *
     * @param loginUserId Spring Security 인증 객체로부터 추출한 현재 로그인 중인 사용자의 ID
     * 
     * @return 200 OK와 함께 패스워드가 제외된 사용자 프로필 정보 응답 DTO (UserProfileRespons
     * )
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal String loginUserId) {
        UserProfileResponse res = userService.getProfile(loginUserId);
        return ResponseEntity.ok(res);
    }

    /**
     * 마이페이지 - 현재 로그인한 사용자의 프로필 정보를 수정합니다.
     *
     * @param loginUserId Spring Security 인증 객체로부터 추출한 현재 로그인 중인 사용자의 ID
     * 
     * @param request 수정할 정보를 담은 DTO (UserProfileUpdateRequest)
     * @return 200 OK와 함께 수정 완료 후 패스워드가 제외된 사용자 프로필 정보 응답 DTO
     *  (UserProfileResponse)
     */
    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal String loginUserId,
            @RequestBody UserProfileUpdateRequest request) {
        UserProfileResponse res = userService.updateProfile(loginUserId, request);
        return ResponseEntity.ok(res);
    }

    /**
     * 사용자의 로그아웃을 처리합니다. 데이터베이스에 저장된 사용자의 Refresh Token을 삭제합니다.
     * 
     *
     * @param loginUserId 토큰
     * @return 200 OK
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal String loginUserId) {
        userService.logout(loginUserId);
        SecurityContextHolder.clearContext(); // 현재 쓰레드의 시큐리티 인증정보(SecurityContext) 즉시 초기화, 동준s 의견
        return ResponseEntity.ok().build();
    }
}
