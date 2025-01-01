package baekgwa.backend.global.annotation.validation.board;

import baekgwa.backend.domain.board.BoardSortType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SortTypeValidator implements ConstraintValidator<OnlyBoardSort, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if(value == null) {
            return false;
        }

        try {
            BoardSortType boardSortType = BoardSortType.valueOf(value);
            return boardSortType != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
