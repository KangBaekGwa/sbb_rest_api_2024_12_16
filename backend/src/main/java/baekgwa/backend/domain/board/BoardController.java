package baekgwa.backend.domain.board;

import baekgwa.backend.domain.board.BoardResponse.NewQuestion;
import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.SuccessCode;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public BaseResponse<BoardResponse.Content> getBoardList(
            @Valid @ModelAttribute BoardRequest.List request,
            HttpServletResponse response
    ) {
        BoardResponse.Content list = boardService.getList(request);
        return BaseResponse.ok(response, SuccessCode.FIND_BOARD_LIST_SUCCESS, list);
    }

    @PostMapping("/question")
    public BaseResponse<BoardResponse.NewQuestion> createNewQuestion (
            @Valid @RequestBody BoardRequest.NewQuestion newQuestion,
            Principal principal,
            HttpServletResponse response
    ) {
        BoardResponse.NewQuestion responseData = boardService.createNewQuestion(newQuestion, principal.getName());
        return BaseResponse.ok(response, SuccessCode.CREATE_NEW_QUESTION_SUCCESS, responseData);
    }
}
