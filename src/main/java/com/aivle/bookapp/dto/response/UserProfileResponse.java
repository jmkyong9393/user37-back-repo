package com.aivle.bookapp.dto.response;

import com.aivle.bookapp.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 마이페이지 프로필 조회 및 수정 응답에 사용되는 DTO 클래스입니다.
 * 보안에 민감한 패스워드 등의 정보는 제외하고 제공합니다.
 */
@Getter
@AllArgsConstructor
@Builder
public class UserProfileResponse {

    private String userId;   // 사용자 ID
    private String name;     // 사용자 실명
    private String email;    // 사용자 이메일
    private String nickname; // 사용자 닉네임

    /**
     * User 엔티티 객체를 UserProfileResponse DTO로 변환하는 정적 팩토리 메서드입니다.
     *
     * @param user 변환할 User 엔티티 객체
     * @return UserProfileResponse 응답 DTO 객체
     */
    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
