package baekgwa.backend.global.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {

    //User Domain
    SIGNUP_SUCCESS(HttpStatus.CREATED, Boolean.TRUE, 201, "회원가입 성공"),
    LOGIN_SUCCESS(HttpStatus.OK, Boolean.TRUE, 200, "로그인 성공"),

    //Board Domain
    FIND_BOARD_LIST_SUCCESS(HttpStatus.OK, Boolean.TRUE, 200, "게시판 조회 성공"),
    CREATE_NEW_QUESTION_SUCCESS(HttpStatus.CREATED, Boolean.TRUE, 201, "신규 질문 등록 성공"),
    FIND_QUESTION_DETAIL_SUCCESS(HttpStatus.OK, Boolean.TRUE, 200, "질문 상세 조회 성공"),
    CREATE_NEW_ANSWER_SUCCESS(HttpStatus.CREATED, Boolean.TRUE, 201, "신규 질문 등록 성공"),

    //공통
    SUCCESS(HttpStatus.OK, Boolean.TRUE, 200, "요청 응답 성공"),
    REISSUE_TOKEN_SUCCESS(HttpStatus.OK, Boolean.TRUE, 200, "Token 재발급 성공"),
    ;

    private final HttpStatus httpStatus;
    private final Boolean isSuccess;
    private final int code;
    private final String message;
}
