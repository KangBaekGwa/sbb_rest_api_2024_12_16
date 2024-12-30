package baekgwa.backend.domain.user;

import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public BaseResponse<Void> signup(
            @Valid @RequestBody UserRequest.Signup signup) {
        userService.signup(signup);
        return BaseResponse.ok(SuccessCode.SIGNUP_SUCCESS);
    }
}
