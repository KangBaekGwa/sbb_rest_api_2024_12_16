package baekgwa.backend.domain.board;

public interface BoardService {

    BoardResponse.Content getList(BoardRequest.List request);

    BoardResponse.NewQuestion createNewQuestion(BoardRequest.NewQuestion newQuestion, String uuid);
}
