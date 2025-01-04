package baekgwa.backend.model.answer;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.model.question.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AnswerTest {

    @DisplayName("질문 정보와 답변 내용을 전달받아, 새로운 답변을 만듭니다.")
    @Test
    void createNewAnswer() {
        // given
        String content = "댓글 내용";
        Long authorId = 1L;
        Question question = Question.createNewQuestion("질문 제목", "질문 내용", authorId);

        // when
        Answer newAnswer = Answer.createNewAnswer(content, question, authorId);

        // then
        assertThat(newAnswer)
                .extracting("content", "question", "authorId")
                .containsExactly(content, question, authorId);
    }
}