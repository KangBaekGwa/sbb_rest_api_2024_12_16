package baekgwa.backend.model.answer;

import baekgwa.backend.model.answer.projection.AnswerProjection;
import baekgwa.backend.model.answer.projection.AnswerProjection.Details;
import baekgwa.backend.model.question.QQuestion;
import baekgwa.backend.model.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<AnswerProjection.Details> findAnswerListByQuestionId(Pageable pageable, long questionId) {
        QAnswer answer = QAnswer.answer;
        QQuestion question = QQuestion.question;
        QUser user = QUser.user;

        List<Details> fetch = queryFactory
                .select(Projections.constructor(Details.class,
                        answer.id,
                        answer.content,
                        user.username,
                        answer.createDate,
                        answer.modifyDate))
                .from(answer)
                .join(answer.question, question)
                .where(question.id.eq(questionId))
                .join(user).on(user.id.eq(answer.authorId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(answer.count())
                .from(answer)
                .join(answer.question, question)
                .where(question.id.eq(questionId))
                .fetchOne();

        total = total != null ? total : 0L;

        return new PageImpl<>(fetch, pageable, total);
    }
}
