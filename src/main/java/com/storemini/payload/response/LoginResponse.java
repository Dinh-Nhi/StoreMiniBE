package com.storemini.payload.response;

import com.storemini.model.user.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private final UserEntity user;
    private String accessToken;
    private int code = 2000;
//    private String refreshToken;
    private String tokenType = "Bearer";
//    private String exchangeToken;
    private String role;
    public LoginResponse(String accessToken, String refreshToken, String exchangeToken, UserEntity user, String role) {
        this.accessToken = accessToken;
//        this.refreshToken = refreshToken;
        this.user = user;
//        this.exchangeToken = exchangeToken;
        this.role = role;
    }
}
