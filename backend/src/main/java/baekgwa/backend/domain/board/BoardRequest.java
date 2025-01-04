package baekgwa.backend.domain.board;

import baekgwa.backend.global.annotation.validation.board.OnlyBoardSort;
import baekgwa.backend.global.annotation.validation.board.OnlyCategory;
import baekgwa.backend.model.category.CategoryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BoardRequest {

    @Getter
    @Setter
    public static class BoardList {
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

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NewQuestion {
        @Size(min = 1, max = 200, message = "제목은 1글자 이상, 200자 미만 입니다.")
        private String subject;

        @NotBlank(message = "질문 내용은 필수값 입니다.")
        private String content;
    }

    @Getter
    @Setter
    public static class AnswerList {
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        private int page = 1;

        @Min(value = 1, message = "페이지 사이즈는 1 이상이어야 합니다.")
        private int size = 10;
    }
}