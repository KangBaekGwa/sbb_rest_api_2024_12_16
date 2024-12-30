package baekgwa.backend.global.exception;

import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.ErrorCode;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 사용자 설정 Exception 발생
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<BaseResponse<Void>> customException(CustomException e) {
        BaseResponse<Void> response = BaseResponse.fail(e.getCode());
        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * Request Dto 필드 유효성 검증 실패
     * @param e
     * @return
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<BaseResponse<Void>> validationException(BindException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        StringBuilder message = new StringBuilder();
        for (ObjectError allError : allErrors) {
            if(!message.isEmpty()){
                message.append("\n");
            }
            message.append(allError.getDefaultMessage());
        }

        BaseResponse<Void> response = new BaseResponse<>(
                ErrorCode.VALIDATION_FAIL_ERROR.getHttpStatus(),
                ErrorCode.VALIDATION_FAIL_ERROR.getIsSuccess(),
                message.toString(),
                ErrorCode.VALIDATION_FAIL_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * Http Method 가 지원되지 않는 Method
     * EX) user/signup (회원가입) 은, `post` 이나, `get` 으로 진행할 경우.
     * @param e
     * @return
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> httpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        BaseResponse<Void> response = new BaseResponse<>(
                ErrorCode.NOT_SUPPORTED_METHOD.getHttpStatus(),
                ErrorCode.NOT_SUPPORTED_METHOD.getIsSuccess(),
                e.getMessage(),
                ErrorCode.NOT_SUPPORTED_METHOD.getCode(),
                null);
        return new ResponseEntity<>(response, ErrorCode.NOT_SUPPORTED_METHOD.getHttpStatus());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<BaseResponse<Void>> noResourceFoundException(NoResourceFoundException e) {
        BaseResponse<Void> response = new BaseResponse<>(
                ErrorCode.NOT_FOUND_URL.getHttpStatus(),
                ErrorCode.NOT_FOUND_URL.getIsSuccess(),
                ErrorCode.NOT_FOUND_URL.getMessage(),
                ErrorCode.NOT_FOUND_URL.getCode(),
                null);
        return new ResponseEntity<>(response, ErrorCode.NOT_FOUND_URL.getHttpStatus());
    }

    /**
     * 핸들링 되지 않은 Exception 발생
     * 로깅 처리 및 오류 발생 응답
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> notHandledException(Exception e) {
        BaseResponse<Void> response = new BaseResponse<>(
                ErrorCode.FAIL.getHttpStatus(),
                ErrorCode.FAIL.getIsSuccess(),
                ErrorCode.FAIL.getMessage(),
                ErrorCode.FAIL.getCode(),
                null);

        log.error(Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>(response, ErrorCode.FAIL.getHttpStatus());
    }
}

