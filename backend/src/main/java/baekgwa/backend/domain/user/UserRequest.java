package baekgwa.backend.domain.user;

import baekgwa.backend.global.annotation.validation.user.UserEmail;
import baekgwa.backend.global.annotation.validation.user.UserLoginId;
import baekgwa.backend.global.annotation.validation.user.Username;
import baekgwa.backend.global.annotation.validation.user.UserPassword;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Signup {
        @UserLoginId private String loginId;
        @UserPassword private String password;
        @UserEmail private String email;
        @Username private String username;
    }
}
