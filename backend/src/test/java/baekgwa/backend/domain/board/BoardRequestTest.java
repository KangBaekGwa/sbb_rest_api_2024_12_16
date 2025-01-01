package baekgwa.backend.domain.board;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.domain.board.BoardRequest.List;
import baekgwa.backend.domain.user.UserRequest.Signup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setupValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("게시판 리스트 조회 Dto 검증")
    @Test
    void list1() {
        // given
        BoardRequest.List request = new BoardRequest.List();

        // when
        Set<ConstraintViolation<List>> validate = validator.validate(request);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("올바르지 않은 Paging 검색 조건은 오류메세지를 발생합니다.")
    @Test
    void list2() {
        // given
        BoardRequest.List request = new BoardRequest.List();
        request.setSize(0);
        request.setPage(0);
        request.setSort("이상한 정렬조건");
        request.setCategory("이상한 카테고리");

        // when
        Set<ConstraintViolation<List>> validate = validator.validate(request);

        // then
        assertThat(validate).hasSize(4);
    }
}