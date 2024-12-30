package baekgwa.backend.domain.user;

import baekgwa.backend.global.annotation.validation.user.UserEmail;
import baekgwa.backend.global.annotation.validation.user.UserLoginId;
import baekgwa.backend.global.annotation.validation.user.Username;
import baekgwa.backend.global.annotation.validation.user.UserPassword;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserRequest {

    @Getter
    @NoArgsConstructor
    public static class Signup {
        @UserLoginId private String loginId;
        @UserPassword private String password;
        @UserEmail private String email;
        @Username private String username;

        @Builder
        private Signup(String loginId, String password, String email, String username) {
            this.loginId = loginId;
            this.password = password;
            this.email = email;
            this.username = username;
        }
    }
}
