package baekgwa.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    //User Domain
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),

    //공통
    SUCCESS(HttpStatus.OK, "요청 응답 성공"),
    ;

    private final HttpStatus httpStatus;
    private final Boolean isSuccess = true;
    private final int code = 200;
    private final String message;
}
