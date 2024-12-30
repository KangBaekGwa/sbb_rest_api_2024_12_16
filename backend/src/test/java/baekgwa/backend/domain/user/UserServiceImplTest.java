package baekgwa.backend.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import baekgwa.backend.domain.user.UserRequest.Signup;
import baekgwa.backend.global.exception.CustomException;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.integration.SpringBootTestSupporter;
import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRole;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserServiceImplTest extends SpringBootTestSupporter {

    @DisplayName("회원가입을 진행합니다.")
    @Test
    void signup1() {
        // given
        Signup dto = Signup
                .builder()
                .email("test@test.com")
                .loginId("test")
                .password("1234")
                .username("테스터")
                .build();

        // when
        userService.signup(dto);
        Optional<User> findData = userRepository.findByLoginId("test");

        // then
        assertThat(findData).isPresent();
        assertThat(findData.get().getId()).isNotNull();
        assertThat(findData.get().getRole()).isEqualTo(UserRole.ROLE_USER);
        assertThat(passwordEncoder.matches("1234", findData.get().getPassword())).isTrue();
    }

    @DisplayName("중복된 회원정보로 회원가입을 진행 시, 오류 메세지를 반환합니다.")
    @Test
    void signup2() {
        // given
        Signup dto = Signup
                .builder()
                .email("test@test.com")
                .loginId("test")
                .password("1234")
                .username("테스터")
                .build();
        userService.signup(dto);

        // when // then
        assertThatThrownBy(() -> userService.signup(dto))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.DUPLICATED_SIGNUP_DATA);
    }
}