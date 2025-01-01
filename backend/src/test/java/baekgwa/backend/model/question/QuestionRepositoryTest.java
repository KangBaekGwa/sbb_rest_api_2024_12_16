package baekgwa.backend.model.question;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.integration.SpringBootTestSupporter;
import baekgwa.backend.model.answer.Answer;
import baekgwa.backend.model.question.projection.QuestionWithAnswerCountProjection;
import baekgwa.backend.model.user.User;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class QuestionRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("키워드로 질문 목록을 검색합니다. Pageable 이 적용 되어야 합니다.")
    @Test
    void findByKeywordWithQueryDsl() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createDate")));
        String keyword = "1번 제목";
        User savedUser = userRepository.save(User.createNewUser("test", "테스터", "test@test.com", "1234"));
        for(int i=1; i<=100; i++) {
            Question question = createQuestion(i + "번 제목", i + "번 내용", savedUser.getId());
            questionRepository.save(question);
            int random = new Random().nextInt(2, 5);
            for(int j=1; j<=random; j++) {
                answerRepository.save(createAnswer(i+"번질문의 " + j + "번째 답변", question, savedUser.getId()));
            }
        }

        // when
        Page<QuestionWithAnswerCountProjection> findData = questionRepository.findByKeywordWithQueryDsl(keyword, pageable);

        // then
        assertThat(findData).hasSize(5);
        assertThat(findData.getTotalElements()).isEqualTo(10L);
        assertThat(findData.isLast()).isFalse();
        assertThat(findData.isFirst()).isTrue();
        assertThat(findData.hasNext()).isTrue();
        assertThat(findData).allSatisfy(data -> {
            assertThat(data.getId()).isNotNull();
            assertThat(data.getSubject()).contains(keyword);
            assertThat(data.getContent()).isNotNull();
            assertThat(data.getUsername()).isEqualTo("테스터");
            assertThat(data.getAnswerCount()).isBetween(2L, 5L);
            assertThat(data.getCreateDate()).isNotNull();
            assertThat(data.getModifyDate()).isNotNull();
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