package baekgwa.backend.domain.board;

import baekgwa.backend.model.answer.projection.AnswerProjection;
import baekgwa.backend.model.question.projection.QuestionProjection;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class BoardResponse {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Content {
        private final List<ContentDetails> contentDetails;
        private final int currentPage;
        private final int totalPages;
        private final long totalElements;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        private final boolean isLast;

        public static Content from(List<BoardResponse.ContentDetails> contentDetails, Page<QuestionProjection.QuestionList> questionListPage) {
            return Content
                    .builder()
                    .contentDetails(contentDetails)
                    .currentPage(questionListPage.getNumber() + 1)
                    .totalPages(questionListPage.getTotalPages())
                    .totalElements(questionListPage.getTotalElements())
                    .pageSize(questionListPage.getSize())
                    .hasNext(questionListPage.hasNext())
                    .hasPrevious(questionListPage.hasPrevious())
                    .isLast(questionListPage.isLast())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentDetails {
        private final long id;
        private final String subject;
        private final LocalDateTime createdDate;
        private final long answerCount;
        private final String author;

        public static ContentDetails from(QuestionProjection.QuestionList question) {
            return ContentDetails.builder()
                    .id(question.id())
                    .subject(question.subject())
                    .createdDate(question.createDate())
                    .answerCount(question.answerCount())
                    .author(question.username())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NewQuestion {
        private final Long questionId;

        public static NewQuestion from(Long id) {
            return NewQuestion.builder().questionId(id).build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class QuestionDetails {
        private final long id;
        private final String subject;
        private final String content;
        private final LocalDateTime createDate;
        private final LocalDateTime modifyDate;
        private final String author;
        private final AnswerDetails answerDetails;

        public static QuestionDetails from(QuestionProjection.Details findQuestion, AnswerDetails answerDetails) {
            return QuestionDetails.builder()
                    .id(findQuestion.id())
                    .subject(findQuestion.subject())
                    .content(findQuestion.content())
                    .createDate(findQuestion.createDate())
                    .modifyDate(findQuestion.modifyDate())
                    .author(findQuestion.username())
                    .answerDetails(answerDetails)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AnswerDetails {
        private final List<AnswerInfo> answerInfos;
        private final int currentPage;
        private final int totalPages;
        private final long totalElements;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        private final boolean isLast;

        public static AnswerDetails from(Page<AnswerProjection.Details> findAnswerList) {
            return AnswerDetails
                    .builder()
                    .answerInfos(AnswerInfo.from(findAnswerList.getContent()))
                    .currentPage(findAnswerList.getNumber() + 1)
                    .totalPages(findAnswerList.getTotalPages())
                    .totalElements(findAnswerList.getTotalElements())
                    .pageSize(findAnswerList.getSize())
                    .hasNext(findAnswerList.hasNext())
                    .hasPrevious(findAnswerList.hasPrevious())
                    .isLast(findAnswerList.isLast())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AnswerInfo {
        private final long id;
        private final String content;
        private final LocalDateTime modifyDate;
        private final LocalDateTime createdDate;
        private final String author;

        public static List<AnswerInfo> from(List<AnswerProjection.Details> answerList) {
            return answerList.stream()
                    .map(answer -> AnswerInfo.builder()
                            .id(answer.id())
                            .content(answer.content())
                            .createdDate(answer.createDate())
                            .modifyDate(answer.modifyDate())
                            .author(answer.username())
                            .build())
                    .toList();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class NewAnswer {
        private final Long answerId;

        public static NewAnswer from(Long id) {
            return NewAnswer.builder().answerId(id).build();
        }
    }
}
