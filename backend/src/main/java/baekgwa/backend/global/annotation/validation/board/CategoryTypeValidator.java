package baekgwa.backend.global.annotation.validation.board;

import baekgwa.backend.model.category.CategoryType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryTypeValidator implements ConstraintValidator<OnlyCategory, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return false;
        }

        try {
            CategoryType categoryType = CategoryType.valueOf(value);
            return categoryType != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
