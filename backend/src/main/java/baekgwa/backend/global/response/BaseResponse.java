package baekgwa.backend.global.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

public record BaseResponse<T>(
        @JsonIgnore HttpStatus httpStatus,
        Boolean isSuccess,
        String message,
        int code,
        T data
) {

    public static <T> BaseResponse<T> ok (HttpServletResponse response, SuccessCode code, T data) {
        response.setStatus(code.getHttpStatus().value());
        return new BaseResponse<>(
                code.getHttpStatus(),
                code.getIsSuccess(),
                code.getMessage(),
                code.getCode(),
                data
        );
    }

    public static BaseResponse<Void> ok() {
        return new BaseResponse<>(
                SuccessCode.SUCCESS.getHttpStatus(),
                SuccessCode.SUCCESS.getIsSuccess(),
                SuccessCode.SUCCESS.getMessage(),
                SuccessCode.SUCCESS.getCode(),
                null
        );
    }

    public static BaseResponse<Void> ok(HttpServletResponse response, SuccessCode code) {
        response.setStatus(code.getHttpStatus().value());
        return new BaseResponse<>(
                code.getHttpStatus(),
                code.getIsSuccess(),
                code.getMessage(),
                code.getCode(),
                null
        );
    }

    // 실패 응답 생성 팩토리 메서드
    public static BaseResponse<Void> fail(ErrorCode code) {
        return new BaseResponse<>(
                code.getHttpStatus(),
                code.getIsSuccess(),
                code.getMessage(),
                code.getCode(),
                null
        );
    }
}
