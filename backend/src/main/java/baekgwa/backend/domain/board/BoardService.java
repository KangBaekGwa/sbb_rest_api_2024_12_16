package baekgwa.backend.domain.board;

import baekgwa.backend.domain.board.BoardRequest.BoardList;

public interface BoardService {

    BoardResponse.Content getList(BoardList request);

    BoardResponse.NewQuestion createNewQuestion(BoardRequest.NewQuestion newQuestion, String uuid);

    BoardResponse.QuestionDetails getQuestion(BoardRequest.AnswerList request, long questionId);
}
