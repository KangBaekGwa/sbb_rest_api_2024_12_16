package baekgwa.backend.domain.board;

import baekgwa.backend.global.response.BaseResponse;
import baekgwa.backend.global.response.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/list")
    public BaseResponse<BoardResponse.Content> getBoardList(
            @Valid @ModelAttribute BoardRequest.List request
    ) {
        BoardResponse.Content list = boardService.getList(request);
        return BaseResponse.ok(SuccessCode.FIND_BOARD_LIST_SUCCESS, list);
    }
}
