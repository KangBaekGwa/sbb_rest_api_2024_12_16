package baekgwa.backend.domain.board;

import baekgwa.backend.global.annotation.validation.board.OnlyBoardSort;
import baekgwa.backend.global.annotation.validation.board.OnlyCategory;
import baekgwa.backend.model.category.CategoryType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

public class BoardRequest {

    @Getter
    @Setter
    public static class List {
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        private int page = 1;

        @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
        private int size = 10;

        @OnlyBoardSort
        private String sort = "createDate";

        private String keyword;

        @OnlyCategory
        private String category = CategoryType.QUESTION.name();
    }
}