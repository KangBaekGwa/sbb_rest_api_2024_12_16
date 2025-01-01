package baekgwa.backend.domain.board;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.domain.board.BoardResponse.Content;
import baekgwa.backend.integration.SpringBootTestSupporter;
import baekgwa.backend.model.answer.Answer;
import baekgwa.backend.model.category.CategoryType;
import baekgwa.backend.model.question.Question;
import baekgwa.backend.model.user.User;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class BoardServiceImplTest extends SpringBootTestSupporter {

    @DisplayName("질문 게시판의 글을 검색합니다.")
    @Test
    void getList1() {
        // given
        String keyword = "1번 제목";
        User savedUser = userRepository.save(
                User.createNewUser("test", "테스터", "test@test.com", "1234"));
        for (int i = 1; i <= 100; i++) {
            Question question = createQuestion(i + "번 제목", i + "번 내용", savedUser.getId());
            questionRepository.save(question);
            int random = new Random().nextInt(2, 5);
            for (int j = 1; j <= random; j++) {
                answerRepository.save(
                        createAnswer(i + "번질문의 " + j + "번째 답변", question, savedUser.getId()));
            }
        }
        BoardRequest.List request = new BoardRequest.List();
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
                    assertThat(data.getAuthor()).isEqualTo("테스터");
                    assertThat(data.getAnswerCount()).isBetween(2L, 5L);
                    assertThat(data.getCreatedDate()).isNotNull();
                });
    }

    private Question createQuestion(String subject, String content, Long authorId) {
        return Question
                .builder()
                .subject(subject)
                .content(content)
                .authorId(authorId)
                .build();
    }

    private Answer createAnswer(String content, Question question, Long authorId) {
        return Answer
                .builder()
                .content(content)
                .question(question)
                .authorId(authorId)
                .build();
    }
}