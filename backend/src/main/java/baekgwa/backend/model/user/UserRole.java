package baekgwa.backend.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ROLE_ADMIN("어드민"),
    ROLE_USER("회원"),
    ;

    private final String describe;
}
