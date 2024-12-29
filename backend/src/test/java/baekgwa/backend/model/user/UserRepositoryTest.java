package baekgwa.backend.model.user;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.integration.SpringBootTestSupporter;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class UserRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("로그인 아이디로, 회원 정보를 찾아옵니다.")
    @Test
    void findByLoginId() {
        // given
        String loginId = "test";
        String username = "test";
        String email = "test@test.com";
        String password = "password";
        User newUser = User.createNewUser(loginId, username, email, password);
        userRepository.save(newUser);
        em.flush();
        em.clear();

        // when
        Optional<User> findData = userRepository.findByLoginId(loginId);

        // then
        assertThat(findData).isPresent();
        assertThat(findData.get())
                .extracting("loginId", "username", "email", "password")
                .contains(loginId, username, email, password);
        assertThat(findData.get().getId()).isNotNull();
        assertThat(findData.get().getUuid()).isNotNull();
        assertThat(findData.get().getRole()).isEqualTo(UserRole.ROLE_USER);
    }

    @DisplayName("로그인 아이디로 찾다가, 없으면 Optional 이 반환 됩니다.")
    @Test
    void findByLoginId2() {
        // given
        String loginId = "test";
        em.flush();
        em.clear();

        // when
        Optional<User> findData = userRepository.findByLoginId(loginId);

        // then
        assertThat(findData).isEmpty();
    }
}