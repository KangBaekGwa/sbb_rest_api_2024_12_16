package baekgwa.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String loginId;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column(unique = true)
    private String uuid;

    @Builder
    private User(Long id, String loginId, String username, String email, String password,
            UserRole role,
            String uuid) {
        this.id = id;
        this.loginId = loginId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.uuid = uuid;
    }

    public static User createNewUser(String loginId, String username, String email, String password) {
        return User
                .builder()
                .loginId(loginId)
                .username(username)
                .email(email)
                .password(password)
                .role(UserRole.ROLE_USER)
                .uuid(UUID.randomUUID().toString())
                .build();
    }
}
