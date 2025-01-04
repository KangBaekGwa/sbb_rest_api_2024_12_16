package baekgwa.backend.model.question;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.integration.SpringBootTestSupporter;
import baekgwa.backend.model.question.projection.QuestionProjection;
import baekgwa.backend.model.question.projection.QuestionProjection.Details;
import baekgwa.backend.model.user.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class QuestionRepositoryTest extends SpringBootTestSupporter {

    @DisplayName("키워드로 질문 목록을 검색합니다. Pageable 이 적용 되어야 합니다.")
    @Test
    void findByKeywordWithQueryDsl1() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Order.desc("createDate")));
        String keyword = "1번 제목";

        Object[] savedData = testDataFactory.createUserAndQuestionAndAnswer(1, 100, 2, 5);
        User savedUser = (User) savedData[0];

        // when
        Page<QuestionProjection.QuestionList> findData = questionRepository.findQuestionByKeyword(keyword, pageable);

        // then
        assertThat(findData.getTotalElements()).isEqualTo(10L);
        assertThat(findData.isLast()).isFalse();
        assertThat(findData.isFirst()).isTrue();
        assertThat(findData.hasNext()).isTrue();
        assertThat(findData.getContent()).hasSize(5);
        assertThat(findData).allSatisfy(data -> {
            assertThat(data.subject()).contains(keyword);
            assertThat(data.content()).isNotNull();
            assertThat(data.username()).isEqualTo(savedUser.getUsername());
            assertThat(data.answerCount()).isBetween(2L, 5L);
            assertThat(data.createDate()).isNotNull();
            assertThat(data.modifyDate()).isNotNull();
        });
    }

    @DisplayName("찾을 데이터가 없으면 content 에 null 이 반환됩니다.")
    @Test
    void findByKeywordWithQueryDsl2() {
        // given
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Order.desc("createDate")));
        String keyword = "1번 제목";

        // when
        Page<QuestionProjection.QuestionList> findData = questionRepository.findQuestionByKeyword(keyword, pageable);

        // then
        assertThat(findData.getTotalElements()).isEqualTo(0L);
        assertThat(findData.isLast()).isTrue();
        assertThat(findData.isFirst()).isTrue();
        assertThat(findData.hasNext()).isFalse();
        assertThat(findData.getContent()).hasSize(0);
    }

    @DisplayName("질문 ID로 연관된 답변 목록을 검색합니다.")
    @Test
    void findQuestionDetailById1() {
        // given
        Object[] savedData = testDataFactory.createUserAndQuestionAndAnswer(1, 1, 0, 1);
        User savedUser = (User) savedData[0];
        Question savedQuestion = (Question) savedData[1];

        // when
        Optional<QuestionProjection.Details> findData = questionRepository.findQuestionDetailById(savedQuestion.getId());

        // then
        assertThat(findData).isPresent();
        assertThat(findData.get())
                .extracting("subject", "content", "username")
                .containsExactly(savedQuestion.getSubject(), savedQuestion.getContent(), savedUser.getUsername());
        assertThat(findData.get().createDate())
                .isEqualToIgnoringNanos(savedQuestion.getCreateDate());
        assertThat(findData.get().modifyDate())
                .isEqualToIgnoringNanos(savedQuestion.getModifyDate());
    }

    @DisplayName("질문 상세 내용이 없다면 Optional null 이 반환 됩니다.")
    @Test
    void findQuestionDetailById2() {
        // given

        // when
        Optional<Details> findData = questionRepository.findQuestionDetailById(1L);

        // then
        assertThat(findData).isEmpty();
    }
}