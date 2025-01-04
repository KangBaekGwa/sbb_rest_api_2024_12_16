package baekgwa.backend.model.answer;

import static org.assertj.core.api.Assertions.assertThat;

import baekgwa.backend.integration.SpringBootTestSupporter;
import baekgwa.backend.model.answer.projection.AnswerProjection.Details;
import baekgwa.backend.model.question.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class AnswerRepositoryImplTest extends SpringBootTestSupporter {

    @DisplayName("질문과 관련된 답변 목록을 가져옵니다. 페이징이 적용됩니다.")
    @Test
    void findAnswerListByQuestionId1() {
        // given
        Object[] savedData = testDataFactory.createUserAndQuestionAndAnswer(1, 1, 11, 20);
        Question savedQuestion = (Question) savedData[1];
        Answer savedAnswer = (Answer) savedData[2];

        int pageNumber = 0; int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // when
        Page<Details> findData = answerRepository.findAnswerListByQuestionId(pageable, savedQuestion.getId());

        // then
        assertThat(findData.getTotalElements()).isGreaterThanOrEqualTo(10L);
        assertThat(findData.isLast()).isFalse();
        assertThat(findData.isFirst()).isTrue();
        assertThat(findData.hasNext()).isTrue();
        assertThat(findData.getContent()).hasSize(10);
        assertThat(findData.getContent()).allSatisfy(data -> {
            assertThat(data.content()).contains("답변");
            assertThat(data.username()).isEqualTo("테스터1");
            assertThat(data.createDate()).isEqualToIgnoringNanos(savedAnswer.getCreateDate());
            assertThat(data.modifyDate()).isEqualToIgnoringNanos(savedAnswer.getModifyDate());
        });
    }

    @DisplayName("아무런 답변이 없다면, 비어있는 응답이 나갑니다.")
    @Test
    void findAnswerListByQuestionId2() {
        // given
        int pageNumber = 0; int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // when
        Page<Details> findData = answerRepository.findAnswerListByQuestionId(pageable, 1L);

        // then
        assertThat(findData.getTotalElements()).isEqualTo(0L);
        assertThat(findData.isLast()).isTrue();
        assertThat(findData.isFirst()).isTrue();
        assertThat(findData.hasNext()).isFalse();
        assertThat(findData.getContent()).hasSize(0);
    }
}