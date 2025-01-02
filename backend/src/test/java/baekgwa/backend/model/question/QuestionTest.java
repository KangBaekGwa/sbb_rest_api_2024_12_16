package baekgwa.backend.model.question;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuestionTest {

    @DisplayName("제목과 내용, 회원 식별 id를 받아, 새로운 질문을 생성합니다.")
    @Test
    void createNewQuestion() {
        // given
        String subject = "제목";
        String content = "내용";
        Long authorId = 1L;

        // when
        Question newQuestion = Question.createNewQuestion(subject, content, authorId);

        // then
        assertThat(newQuestion)
                .extracting("subject", "content", "authorId")
                .containsExactly(subject, content, authorId);
    }
}