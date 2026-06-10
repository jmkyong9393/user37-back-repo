package com.aivle.bookapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserRegisterResponse {

    private String userId;
    private String name;
    private String email;
    private String nickname;
}
