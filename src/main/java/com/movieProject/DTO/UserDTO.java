package com.movieProject.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

public class UserDTO {
    @Data
    public static class UserCreateRequest{
        @Schema(description = "이름", example = "test")
        private String userName;
        @Schema(description = "아이디", example = "test123")
        private String UserId;
        @Schema(description = "이메일", example = "test123@gmail.com")
        private String email;
        @Schema(description = "비밀번호", example = "test123!")
        private String password;
        @Schema(description = "휴대폰 번호", example = "010-2122-7619")
        private String phoneNumber;
        @Schema(description = "생년월일", example = "20020208")
        private String birthDate;
    }

    @Data
    public static class UserLoginRequest{
        private String userId;
        private String password;
    }

    @Data
    public static class loginResponse{
        private String token;
        private String userName;

        public loginResponse(String token, String userName){
            this.token=token;
            this.userName=userName;
        }
    }

    @Data
    @AllArgsConstructor
    public static class UserResponse{
        @Schema(description = "이름", example = "test")
        private String userName;
        @Schema(description = "아이디", example = "test123")
        private String id;
        @Schema(description = "이메일", example = "test123@gmail.com")
        private String email;
        @Schema(description = "휴대폰 번호", example = "010-2122-7619")
        private String phoneNumber;
        @Schema(description = "생년월일", example = "20020208")
        private String birthDate;
    }
}
