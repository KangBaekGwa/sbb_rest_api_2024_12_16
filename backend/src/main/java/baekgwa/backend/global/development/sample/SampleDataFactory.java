package baekgwa.backend.global.development.sample;

import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRepository;
import baekgwa.backend.model.user.UserRole;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class SampleDataFactory {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    protected void registerSampleData() {
        addTestUser();
    }

    public void addTestUser() {
        User user = User.builder()
                .loginId("test")
                .username("테스터")
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .role(UserRole.ROLE_USER)
                .uuid(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);
    }
}
