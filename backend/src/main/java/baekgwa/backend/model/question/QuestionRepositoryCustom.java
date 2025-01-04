package baekgwa.backend.model.question;

import baekgwa.backend.model.question.projection.QuestionProjection;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepositoryCustom {
    Page<QuestionProjection.QuestionList> findQuestionByKeyword(String keyword, Pageable pageable);

    Optional<QuestionProjection.Details> findQuestionDetailById(long questionId);
}
