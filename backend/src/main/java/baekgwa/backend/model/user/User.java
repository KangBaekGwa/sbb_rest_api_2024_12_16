package baekgwa.backend.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    private String role;

    @Column(unique = true)
    private String uuid;

    @Builder
    private User(Long id, String loginId, String username, String email, String password,
            String role,
            String uuid) {
        this.id = id;
        this.loginId = loginId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.uuid = uuid;
    }
}
