package baekgwa.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    //User Domain
    SIGNUP_SUCCESS(HttpStatus.CREATED, "회원가입 성공"),
    LOGIN_SUCCESS(HttpStatus.OK, "로그인 성공"),

    //Board Domain
    FIND_BOARD_LIST_SUCCESS(HttpStatus.OK, "게시판 조회 성공"),
    CREATE_NEW_QUESTION_SUCCESS(HttpStatus.CREATED, "신규 질문 등록 성공"),

    //공통
    SUCCESS(HttpStatus.OK, "요청 응답 성공"),
    REISSUE_TOKEN_SUCCESS(HttpStatus.OK, "Token 재발급 성공"),
    ;

    private final HttpStatus httpStatus;
    private final Boolean isSuccess = true;
    private final int code = 200;
    private final String message;
}
