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
@Size(min = 2, max = 15, message = "이름은 2글자 ~ 15글자 사이입니다.")
@Pattern(regexp = "^[가-힣a-zA-Z]+$", message = "이름은 한글과 영문만 허용됩니다.")
public @interface Username {
    String message() default "Invalid username";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
