package com.aivle.bookapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserLoginRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String password;
}
