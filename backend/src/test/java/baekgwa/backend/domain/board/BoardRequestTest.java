package baekgwa.backend.domain.board;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.domain.board.BoardRequest.AnswerList;
import baekgwa.backend.domain.board.BoardRequest.BoardList;
import baekgwa.backend.domain.board.BoardRequest.NewQuestion;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BoardRequestTest {

    private static Validator validator;
    private static ValidatorFactory validatorFactory;

    @BeforeAll
    static void setupValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void cleanUpValidator() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @DisplayName("게시판 리스트 조회 request dto 검증")
    @Test
    void list1() {
        // given
        BoardList request = new BoardList();

        // when
        Set<ConstraintViolation<BoardList>> validate = validator.validate(request);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("올바르지 않은 Paging 검색 조건은 오류메세지를 발생합니다.")
    @Test
    void list2() {
        // given
        BoardList request = new BoardList();
        request.setSize(0);
        request.setPage(0);
        request.setSort("이상한 정렬조건");
        request.setCategory("이상한 카테고리");

        // when
        Set<ConstraintViolation<BoardList>> validate = validator.validate(request);

        // then
        assertThat(validate).hasSize(4);
    }

    @DisplayName("신규 질문 등록 request dto 검증")
    @Test
    void NewQuestion1() {
        // given
        BoardRequest.NewQuestion request = BoardRequest.NewQuestion
                .builder().subject("제목").content("내용").build();

        // when
        Set<ConstraintViolation<NewQuestion>> validate = validator.validate(request);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("신규 질문 등록 시, 제목의 길이는 1<=제목<=200, 공백 질문 내용은 허용되지 않습니다.")
    @Test
    void NewQuestion2() {
        // given
        BoardRequest.NewQuestion request = BoardRequest.NewQuestion
                .builder().subject("o".repeat(201)).content("").build();

        // when
        Set<ConstraintViolation<NewQuestion>> validate = validator.validate(request);

        // then
        assertThat(validate).hasSize(2);
    }

    @DisplayName("상세 질문 조회 request default 검증")
    @Test
    void AnswerList1() {
        // given
        BoardRequest.AnswerList answerList = new BoardRequest.AnswerList();

        // when
        Set<ConstraintViolation<AnswerList>> validate = validator.validate(answerList);

        // then
        assertThat(validate).hasSize(0);
    }

    @DisplayName("상세 질문 조회 시, 답변 페이징 번호는 1번부터, 사이즈는 1 이상이어야 합니다.")
    @Test
    void AnswerList2() {
        // given
        BoardRequest.AnswerList answerList = new BoardRequest.AnswerList();
        answerList.setPage(0);
        answerList.setSize(0);

        // when
        Set<ConstraintViolation<AnswerList>> validate = validator.validate(answerList);

        // then
        assertThat(validate).hasSize(2);
    }
}