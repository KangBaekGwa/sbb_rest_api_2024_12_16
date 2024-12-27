package baekgwa.backend.global.security.user;

import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 정보 로드 서비스
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userData = userRepository.findByLoginId(username)
                .orElse(null);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}
