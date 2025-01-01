package baekgwa.backend.model.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByLoginId(String username);

    Optional<User> findByUuid(String uuid);
}
