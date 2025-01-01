package baekgwa.backend.global.annotation.validation.board;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Board Domain 의 카테고리 검색 조건 검증
 * CategoryType 에 등록된 것만 사용 가능
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { CategoryTypeValidator.class })
public @interface OnlyCategory {
    String message() default "올바른 카테고리가 아닙니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
