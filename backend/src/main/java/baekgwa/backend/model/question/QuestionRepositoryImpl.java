package baekgwa.backend.model.question;

import baekgwa.backend.model.answer.QAnswer;
import baekgwa.backend.model.question.projection.QuestionWithAnswerCountProjection;
import baekgwa.backend.model.user.QUser;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryImpl implements QuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<QuestionWithAnswerCountProjection> findByKeywordWithQueryDsl(String keyword, Pageable pageable) {
        QQuestion question = QQuestion.question;
        QAnswer answer = QAnswer.answer;
        QUser user = QUser.user;

        BooleanExpression predicate = keyword != null && !keyword.isEmpty()
                ? question.subject.containsIgnoreCase(keyword)
                : null;

        List<QuestionWithAnswerCountProjection> content = queryFactory
                .select(Projections.constructor(QuestionWithAnswerCountProjection.class,
                        question.id,
                        question.subject,
                        question.content,
                        user.username,
                        answer.count().as("answerCount"),
                        question.createDate,
                        question.modifyDate))
                .from(question)
                .leftJoin(answer).on(answer.question.id.eq(question.id))
                .leftJoin(user).on(user.id.eq(question.authorId))
                .where(predicate)
                .groupBy(question.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(question.count())
                .from(question)
                .where(predicate)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
