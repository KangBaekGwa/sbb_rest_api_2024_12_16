package baekgwa.backend.global.annotation.validation.board;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Board Domain 의 정렬 조건 검증
 * BoardSortType 에 등록된 것만 사용 가능
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = { SortTypeValidator.class })
public @interface OnlyBoardSort {
    String message() default "올바른 정렬 조건이 아닙니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
