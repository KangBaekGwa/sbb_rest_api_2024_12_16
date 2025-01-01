package baekgwa.backend.model.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CategoryType {
    QUESTION("질문"),
    COURSE("강좌"),
    FREEBOARD("자유게시판")
    ;

    private final String categoryName;
}

