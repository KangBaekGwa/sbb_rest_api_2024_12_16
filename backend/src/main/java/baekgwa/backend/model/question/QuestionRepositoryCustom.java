package baekgwa.backend.model.question;

import baekgwa.backend.model.question.projection.QuestionWithAnswerCountProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<QuestionWithAnswerCountProjection> findByKeywordWithQueryDsl(String keyword, Pageable pageable);
}
