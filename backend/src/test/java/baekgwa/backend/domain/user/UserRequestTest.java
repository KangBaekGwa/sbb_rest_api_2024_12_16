package baekgwa.backend.domain.user;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.domain.user.UserRequest.Signup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("회원가입 Dto 검증")
    @Test
    void signup1() {
        // given
        Signup signup = Signup
                .builder()
                .loginId("test1")
                .password("!asdf1234")
                .email("test@test.com")
                .username("tester")
                .build();

        // when
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("올바르지 않은 회원가입 정도는 오류가 발생됩니다.")
    @Test
    void signup2() {
        // given
        Signup signup = Signup
                .builder()
                .loginId("")
                .password("")
                .email("1")
                .username("")
                .build();

        // when
        Set<ConstraintViolation<Signup>> validate = validator.validate(signup);

        // then
        assertThat(validate).hasSize(8);
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("로그인 아이디는 5자리 ~ 20자리 사이입니다.")))
                .isTrue();
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("로그인 아이디는 영문(대소문자 구분)과 숫자만 허용합니다.")))
                .isTrue();

        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("비밀번호는 8자리 ~ 20자리 사이 입니다.")))
                .isTrue();
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("비밀번호는 특수문자를 반드시 포함하여야 합니다.")))
                .isTrue();

        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("이메일은 최소 3자리 부터 최대 25자리 까지 입니다.")))
                .isTrue();
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("이메일 형식이 아닙니다.")))
                .isTrue();

        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("이름은 2글자 ~ 15글자 사이입니다.")))
                .isTrue();
        assertThat(validate.stream()
                .anyMatch(v -> v.getMessage().equals("이름은 한글과 영문만 허용됩니다.")))
                .isTrue();
    }
}