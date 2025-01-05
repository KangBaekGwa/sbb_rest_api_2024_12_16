package baekgwa.backend.domain.board;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import baekgwa.backend.domain.board.BoardRequest.BoardList;
import baekgwa.backend.domain.board.BoardResponse.Content;
import baekgwa.backend.domain.board.BoardResponse.NewQuestion;
import baekgwa.backend.domain.board.BoardResponse.QuestionDetails;
import baekgwa.backend.global.exception.CustomException;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.integration.SpringBootTestSupporter;
import baekgwa.backend.model.answer.Answer;
import baekgwa.backend.model.category.CategoryType;
import baekgwa.backend.model.question.Question;
import baekgwa.backend.model.user.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class BoardServiceImplTest extends SpringBootTestSupporter {

    @DisplayName("질문 게시판의 글을 검색합니다.")
    @Test
    void getList1() {
        // given
        testDataFactory.createUserAndQuestionAndAnswer(1, 100, 2, 5);

        String keyword = "1번 제목";
        BoardList request = new BoardList();
        request.setCategory(CategoryType.QUESTION.name());
        request.setPage(1);
        request.setSort("createDate");
        request.setKeyword(keyword);
        request.setSize(5);

        // when
        Content findData = boardService.getList(request);

        // then
        assertThat(findData.isLast()).isFalse();
        assertThat(findData.isHasNext()).isTrue();
        assertThat(findData.isHasPrevious()).isFalse();
        assertThat(findData.getCurrentPage()).isEqualTo(1);
        assertThat(findData.getTotalPages()).isEqualTo(2);
        assertThat(findData.getTotalElements()).isEqualTo(10);
        assertThat(findData.getPageSize()).isEqualTo(5);
        assertThat(findData.getContentDetails())
                .isNotNull()
                .hasSize(5)
                .allSatisfy(data -> {
                    assertThat(data.getSubject()).contains(keyword);
                    assertThat(data.getAuthor()).contains("테스터");
                    assertThat(data.getAnswerCount()).isBetween(2L, 5L);
                    assertThat(data.getCreatedDate()).isNotNull();
                });
    }

    @DisplayName("새로운 질문을 등록합니다.")
    @Test
    void createNewQuestion() {
        // given
        User savedUser = userRepository.save(
                User.createNewUser("test", "test", "test@test.com", "1234"));

        String subject = "제목";
        String content = "내용";
        BoardRequest.NewQuestion newQuestion = BoardRequest.NewQuestion
                .builder()
                .subject(subject)
                .content(content)
                .build();
        em.flush();
        em.clear();

        // when
        NewQuestion data = boardService.createNewQuestion(newQuestion, savedUser.getUuid());

        // then
        assertThat(data).isNotNull();
        Optional<Question> findData = questionRepository.findById(data.getQuestionId());
        assertThat(findData).isPresent();
        assertThat(findData.get())
                .extracting("subject", "content", "authorId")
                .containsExactly(subject, content, savedUser.getId());
    }

    @DisplayName("상제 질문 내용과, 그에 따른 답변 목록을 조회합니다.")
    @Test
    void getQuestion1() {
        // given
        Object[] savedData = testDataFactory.createUserAndQuestionAndAnswer(1, 1, 11, 20);
        Question savedQuestion = (Question) savedData[1];
        Answer savedAnswer = (Answer) savedData[2];
        BoardRequest.AnswerList request = new BoardRequest.AnswerList();
        request.setPage(1);
        request.setSize(10);

        // when
        QuestionDetails findData = boardService.getQuestion(request, savedQuestion.getId());

        // then
        // 질문 내용 검증
        assertThat(findData.getSubject()).contains("제목");
        assertThat(findData.getContent()).contains("내용");
        assertThat(findData.getAuthor()).contains("테스터");
        assertThat(findData.getCreateDate()).isEqualToIgnoringNanos(savedQuestion.getCreateDate());
        assertThat(findData.getModifyDate()).isEqualToIgnoringNanos(savedQuestion.getModifyDate());

        // 답변 페이징 검증
        assertThat(findData.getAnswerDetails())
                .extracting("currentPage", "totalPages", "pageSize", "hasNext", "hasPrevious", "isLast")
                .containsExactly(1, 2, 10, true, false, false);
        assertThat(findData.getAnswerDetails().getTotalElements()).isBetween(11L, 20L);

        // 답변 상세 내용 검증
        assertThat(findData.getAnswerDetails().getAnswerInfos())
                .allSatisfy(data -> {
                    assertThat(data.getContent()).contains("답변");
                    assertThat(data.getModifyDate()).isEqualToIgnoringNanos(savedAnswer.getModifyDate());
                    assertThat(data.getCreatedDate()).isEqualToIgnoringNanos(savedAnswer.getCreateDate());
                    assertThat(data.getAuthor()).contains("테스터");
                });
    }

    @DisplayName("질문을 찾을 수 없다면 오류가 발생합니다.")
    @Test
    void getQuestion2() {
        // given
        BoardRequest.AnswerList request = new BoardRequest.AnswerList();
        request.setPage(1);
        request.setSize(10);

        // when // then
        assertThatThrownBy(() -> boardService.getQuestion(request, 1L))
                .isInstanceOf(CustomException.class)
                .extracting("code")
                .isEqualTo(ErrorCode.NOT_FOUND_QUESTION);
    }

    @DisplayName("질문 상세 조회를 할때, 댓글이 없어도 질문은 응답해야 합니다.")
    @Test
    void getQuestion3() {
        // given
        Object[] savedData = testDataFactory.createUserAndQuestionAndAnswer(1, 1, 0, 0);
        Question savedQuestion = (Question) savedData[1];
        BoardRequest.AnswerList request = new BoardRequest.AnswerList();
        request.setPage(1);
        request.setSize(10);

        // when
        QuestionDetails findData = boardService.getQuestion(request, savedQuestion.getId());

        // then
        // 답변 페이징 검증
        assertThat(findData.getAnswerDetails())
                .extracting("currentPage", "totalPages", "pageSize", "hasNext", "hasPrevious", "isLast")
                .containsExactly(1, 0, 10, false, false, true);
        assertThat(findData.getAnswerDetails().getTotalElements()).isEqualTo(0L);

        // 답변 상세 내용 검증
        assertThat(findData.getAnswerDetails().getAnswerInfos()).isEmpty();
    }

    @DisplayName("질문에 대한 새로운 답변을 등록합니다.")
    @Test
    void createNewAnswer() {
        // given
        Object[] savedData = testDataFactory.createUserAndQuestionAndAnswer(1, 1, 0, 0);
        User savedUser = (User) savedData[0];
        Question savedQuestion = (Question) savedData[1];
        BoardRequest.NewAnswer request = BoardRequest.NewAnswer.builder().content("답변").build();

        // when
        BoardResponse.NewAnswer newAnswer =
                boardService.createNewAnswer(request, savedQuestion.getId(), savedUser.getUuid());

        // then
        Optional<Answer> findData = answerRepository.findById(newAnswer.getAnswerId());
        assertThat(findData).isPresent();
        assertThat(findData.get())
                .extracting("id", "content", "authorId")
                .containsExactly(newAnswer.getAnswerId(), "답변", savedUser.getId());
    }
}