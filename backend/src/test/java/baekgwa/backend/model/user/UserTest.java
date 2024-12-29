package baekgwa.backend.model.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("필수 입렵값으로, 새로운 회원을 생성합니다.")
    @Test
    void test() {
        // given
        String loginId = "test";
        String username = "1234";
        String email = "test@test.com";
        String password = "1234";

        // when
        User newUser = User.createNewUser(loginId, username, email, password);

        // then
        assertThat(newUser.getUuid()).isNotNull();
        assertThat(newUser.getRole()).isEqualTo(UserRole.ROLE_USER);
    }
}