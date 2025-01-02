package baekgwa.backend.domain.board;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import baekgwa.backend.domain.board.BoardRequest.NewQuestion;
import baekgwa.backend.domain.board.BoardResponse.Content;
import baekgwa.backend.domain.board.BoardResponse.ContentDetails;
import baekgwa.backend.global.response.SuccessCode;
import baekgwa.backend.integration.MockControllerTestSupporter;
import baekgwa.backend.integration.security.WithCustomUser;
import baekgwa.backend.model.category.CategoryType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class BoardControllerTest extends MockControllerTestSupporter {

    @DisplayName("[docs] 게시판 목록 조회")
    @Test
    void getBoardList1() throws Exception {
        // given
        BoardResponse.Content response = createQuestionContent();

        // stubbing
        BDDMockito.when(boardService.getList(any())).thenReturn(response);

        // when // then
        String[] categoryValues = Arrays.stream(CategoryType.values())
                .map(Enum::name)
                .toArray(String[]::new);
        String[] boardSortValues = Arrays.stream(BoardSortType.values())
                .map(Enum::name)
                .toArray(String[]::new);

        mockMvc.perform(get("/board/list")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .queryParam("sort", "createDate")
                        .queryParam("keyword", "")
                        .queryParam("category", "QUESTION"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(
                        SuccessCode.FIND_BOARD_LIST_SUCCESS.getIsSuccess()))
                .andExpect(jsonPath("$.message").value(
                        SuccessCode.FIND_BOARD_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.FIND_BOARD_LIST_SUCCESS.getCode()))
                //문서 생성
                .andDo(document("board/list",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호")
                                        .optional()
                                        .attributes(key("validity").value("페이지 번호는 1 이상이어야 합니다."))
                                        .attributes(key("default").value(1)),
                                parameterWithName("size").description("페이지 사이즈")
                                        .optional()
                                        .attributes(key("validity").value("페이지 사이즈는 1 이상이어야 합니다."))
                                        .attributes(key("default").value(10)),
                                parameterWithName("sort").description("정렬 조건")
                                        .optional()
                                        .attributes(key("validity").value(
                                                "[" + String.join(", ", boardSortValues)
                                                        + "] 사용가능"))
                                        .attributes(key("default").value("createDate")),
                                parameterWithName("keyword").description("검색 키워드")
                                        .optional()
                                        .attributes(key("validity").value("N/A"))
                                        .attributes(key("default").value("N/A")),
                                parameterWithName("category").description("검색 카테고리")
                                        .optional()
                                        .attributes(key("validity").value(
                                                "[" + String.join(", ", categoryValues) + "] 사용가능"))
                                        .attributes(key("default").value("QUESTION"))
                        ),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메세지"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.contentDetails").type(JsonFieldType.ARRAY)
                                        .description("게시물 목록"),
                                fieldWithPath("data.contentDetails[].id").type(JsonFieldType.NUMBER)
                                        .description("게시물 ID"),
                                fieldWithPath("data.contentDetails[].subject").type(
                                                JsonFieldType.STRING)
                                        .description("게시물 제목"),
                                fieldWithPath("data.contentDetails[].createdDate").type(
                                                JsonFieldType.STRING)
                                        .description("게시물 생성 날짜/시간"),
                                fieldWithPath("data.contentDetails[].answerCount").type(
                                                JsonFieldType.NUMBER)
                                        .description("게시물 답변 수"),
                                fieldWithPath("data.contentDetails[].author").type(
                                                JsonFieldType.STRING)
                                        .description("게시물 작성자"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER)
                                        .description("전체 페이지 수"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER)
                                        .description("전체 게시물 수"),
                                fieldWithPath("data.pageSize").type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("data.hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("다음 페이지 여부"),
                                fieldWithPath("data.hasPrevious").type(JsonFieldType.BOOLEAN)
                                        .description("이전 페이지 여부"),
                                fieldWithPath("data.last").type(JsonFieldType.BOOLEAN)
                                        .description("마지막 페이지 여부")
                        ))
                );
    }

    @DisplayName("[Docs] 신규 질문 등록")
    @WithCustomUser
    @Test
    void createNewQuestion() throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication: " + authentication);
        System.out.println("Principal: " + authentication.getPrincipal());

        // given
        BoardRequest.NewQuestion request = NewQuestion
                .builder()
                .subject("제목")
                .content("내용")
                .build();

        BoardResponse.NewQuestion response = BoardResponse.NewQuestion
                .builder()
                .questionId(1L)
                .build();

        // stubbing
        BDDMockito.when(boardService.createNewQuestion(any(), anyString()))
                .thenReturn(response);

        // when // then
        mockMvc.perform(post("/board/question")
                        .header("access", "your-access-token")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(
                        SuccessCode.CREATE_NEW_QUESTION_SUCCESS.getIsSuccess()))
                .andExpect(jsonPath("$.message").value(
                        SuccessCode.CREATE_NEW_QUESTION_SUCCESS.getMessage()))
                .andExpect(
                        jsonPath("$.code").value(SuccessCode.CREATE_NEW_QUESTION_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.questionId").value(response.getQuestionId()))
                //문서 생성
                .andDo(document("board/question",
                        requestHeaders(
                                headerWithName("access").description("your-access-token")
                        ),
                        requestFields(
                                fieldWithPath("subject").type(JsonFieldType.STRING)
                                        .description("질문 제목")
                                        .attributes(
                                                key("validity").value("제목은 1글자 이상, 200자 미만 입니다.")),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("질문 내용")
                                        .attributes(key("validity").value("질문 내용은 필수값 입니다."))
                        ),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답 메세지"),
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("응답 코드"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.questionId").type(JsonFieldType.NUMBER)
                                        .description("등록된 질문 고유 ID")
                        ))
                );
    }

    private Content createQuestionContent() {
        List<ContentDetails> contentDetails = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            ContentDetails content = ContentDetails
                    .builder()
                    .id(1L)
                    .subject("댓글" + i)
                    .createdDate(LocalDateTime.now())
                    .answerCount(i * i)
                    .author("댓글러" + i)
                    .build();
            contentDetails.add(content);
        }

        return Content
                .builder()
                .contentDetails(contentDetails)
                .currentPage(1)
                .totalPages(10)
                .totalElements(100)
                .pageSize(10)
                .hasNext(true)
                .hasPrevious(false)
                .isLast(false)
                .build();
    }
}