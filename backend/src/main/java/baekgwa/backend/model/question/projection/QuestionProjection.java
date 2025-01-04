package baekgwa.backend.model.question.projection;

import java.time.LocalDateTime;

public class QuestionProjection {

    public record QuestionList(long id, String subject, String content, String username, long answerCount,
                               LocalDateTime createDate, LocalDateTime modifyDate) {

    }

    public record Details(long id, String subject, String content, String username, LocalDateTime createDate, LocalDateTime modifyDate) {

    }
}
