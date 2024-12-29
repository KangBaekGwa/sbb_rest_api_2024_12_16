package baekgwa.backend.domain.user;

import baekgwa.backend.global.exception.CustomException;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void signup(UserDto.Signup signup) {

        User newUser = User.createNewUser(
                signup.getLoginId(),
                signup.getUsername(),
                signup.getEmail(),
                passwordEncoder.encode(signup.getPassword()));

        try {
            userRepository.save(newUser);
        } catch (DataIntegrityViolationException ignored) {
            throw new CustomException(ErrorCode.DUPLICATED_SIGNUP_DATA);
        }
    }
}
