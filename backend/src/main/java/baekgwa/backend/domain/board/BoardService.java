package baekgwa.backend.domain.board;

public interface BoardService {

    BoardResponse.Content getList(BoardRequest.List request);
}
