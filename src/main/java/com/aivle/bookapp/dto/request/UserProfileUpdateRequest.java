package com.aivle.bookapp.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 마이페이지 사용자 프로필 정보 수정을 위한 요청 DTO 클래스입니다.
 * 변경하고자 하는 필드만 선택하여 입력할 수 있도록 모든 필드는 필수값이 아닙니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserProfileUpdateRequest {

    private String nickname; // 수정할 사용자 닉네임 (선택)
    private String email;    // 수정할 사용자 이메일 (선택)
    private String password; // 수정할 사용자 비밀번호 (선택)
}
