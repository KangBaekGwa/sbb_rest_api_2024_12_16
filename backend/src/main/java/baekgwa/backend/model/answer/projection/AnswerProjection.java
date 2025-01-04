package baekgwa.backend.model.answer.projection;

import java.time.LocalDateTime;

public class AnswerProjection {

    public record Details(long id, String content, String username, LocalDateTime createDate, LocalDateTime modifyDate) {

    }
}
