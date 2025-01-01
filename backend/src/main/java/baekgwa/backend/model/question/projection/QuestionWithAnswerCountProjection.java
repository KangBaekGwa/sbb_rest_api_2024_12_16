package baekgwa.backend.model.question.projection;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class QuestionWithAnswerCountProjection {
    private final Long id;
    private final String subject;
    private final String content;
    private final String username;
    private final Long answerCount;
    private final LocalDateTime createDate;
    private final LocalDateTime modifyDate;

    public QuestionWithAnswerCountProjection(Long id, String subject, String content,
            String username,
            Long answerCount, LocalDateTime createDate, LocalDateTime modifyDate) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.username = username;
        this.answerCount = answerCount;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }
}
