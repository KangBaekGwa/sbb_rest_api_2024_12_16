package baekgwa.backend.domain.user;

import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.SuccessCode;
import jakarta.servlet.http.HttpServletResponse;
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
            @Valid @RequestBody UserRequest.Signup signup,
            HttpServletResponse response) {
        userService.signup(signup);
        return BaseResponse.ok(response, SuccessCode.SIGNUP_SUCCESS);
    }
}
