package com.aivle.bookapp.dto.response;

import com.aivle.bookapp.domain.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInBookResponse {
    private String userId;
    private String nickName;

    public static UserInBookResponse from(User user){
        return UserInBookResponse.builder()
                .userId(user.getUserId())
                .nickName(user.getNickname())
                .build();
    }
}
