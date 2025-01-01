package baekgwa.backend.domain.board;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BoardSortType {
    createDate("생성일"),

    ;
    private final String sortName;
}
