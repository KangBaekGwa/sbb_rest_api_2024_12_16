package baekgwa.backend.global.annotation.validation.user;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Constraint(validatedBy = { })
@Retention(RetentionPolicy.RUNTIME)
@Size(min = 3, max = 25, message = "이메일은 최소 3자리 부터 최대 25자리 까지 입니다.")
@Email(message = "이메일 형식이 아닙니다.")
public @interface UserEmail {
    String message() default "Invalid user email";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
