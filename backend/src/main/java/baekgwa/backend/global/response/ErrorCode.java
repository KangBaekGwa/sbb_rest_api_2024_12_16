package baekgwa.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    //1000 ~ 2000
    //오류 종류 : 인증/인가 서비스 오류
    AUTHENTICATION_LOGIN_FAIL(HttpStatus.BAD_REQUEST, Boolean.FALSE, 1000, "잘못된 회원 정보 입니다."),
    DUPLICATED_SIGNUP_DATA(HttpStatus.BAD_REQUEST, Boolean.FALSE, 1001, "중복된 회원가입 정보 입니다."),

    //9000 ~ 9999
    //오류 종류 : 공통 에러
    VALIDATION_FAIL_ERROR(HttpStatus.BAD_REQUEST, Boolean.FALSE, 9000, "(exception error 메세지에 따름)"),
    NOT_SUPPORTED_METHOD(HttpStatus.METHOD_NOT_ALLOWED, Boolean.FALSE, 9001, "(exception error 메세지에 따름"),
    FAIL(HttpStatus.BAD_REQUEST, Boolean.FALSE, 400, "요청 응답 실패, 관리자에게 문의해주세요."),
    ;

    private final HttpStatus httpStatus;
    private final Boolean isSuccess;
    private final int code;
    private final String message;
}
