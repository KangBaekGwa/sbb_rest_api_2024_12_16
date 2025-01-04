package baekgwa.backend.model.answer;

import baekgwa.backend.model.answer.projection.AnswerProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnswerRepositoryCustom {

    Page<AnswerProjection.Details> findAnswerListByQuestionId(Pageable pageable, long questionId);
}
