package baekgwa.backend.domain.board;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class BoardResponse {

    @Getter
    public static class Content {
        private final List<ContentDetails> contentDetails;
        private final int currentPage;
        private final int totalPages;
        private final long totalElements;
        private final int pageSize;
        private final boolean hasNext;
        private final boolean hasPrevious;
        private final boolean isLast;

        @Builder
        private Content(List<ContentDetails> contentDetails, int currentPage, int totalPages,
                long totalElements, int pageSize, boolean hasNext, boolean hasPrevious,
                boolean isLast) {
            this.contentDetails = contentDetails;
            this.currentPage = currentPage;
            this.totalPages = totalPages;
            this.totalElements = totalElements;
            this.pageSize = pageSize;
            this.hasNext = hasNext;
            this.hasPrevious = hasPrevious;
            this.isLast = isLast;
        }
    }

    @Getter
    public static class ContentDetails {
        private final long id;
        private final String subject;
        private final LocalDateTime createdDate;
        private final long answerCount;
        private final String author;

        @Builder
        private ContentDetails(String subject, long id, LocalDateTime createdDate, long answerCount,
                String author) {
            this.subject = subject;
            this.id = id;
            this.createdDate = createdDate;
            this.answerCount = answerCount;
            this.author = author;
        }
    }
}
