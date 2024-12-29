package baekgwa.backend.global.annotation.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Constraint(validatedBy = { })
@Retention(RetentionPolicy.RUNTIME)
@Size(min = 5, max = 20, message = "로그인 아이디는 5자리 ~ 20자리 사이입니다.")
@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "로그인 아이디는 영문(대소문자 구분)과 숫자만 허용합니다.")
public @interface UserLoginId {
    String message() default "Invalid user login ID";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}