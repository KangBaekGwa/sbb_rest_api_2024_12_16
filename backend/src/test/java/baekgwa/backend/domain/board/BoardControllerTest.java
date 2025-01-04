package baekgwa.backend.domain.board;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import baekgwa.backend.domain.board.BoardRequest.NewQuestion;
import baekgwa.backend.domain.board.BoardResponse.Content;
import baekgwa.backend.domain.board.BoardResponse.ContentDetails;
import baekgwa.backend.domain.board.BoardResponse.QuestionDetails;
import baekgwa.backend.global.response.SuccessCode;
import baekgwa.backend.integration.MockControllerTestSupporter;
import baekgwa.backend.integration.security.WithCustomUser;
import baekgwa.backend.model.category.CategoryType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
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
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.contentDetails").isArray())
                .andExpect(jsonPath("$.data.contentDetails[0].id").isNumber())
                .andExpect(jsonPath("$.data.contentDetails[0].subject").value("댓글1"))
                .andExpect(jsonPath("$.data.contentDetails[0].createdDate").isNotEmpty())
                .andExpect(jsonPath("$.data.contentDetails[0].answerCount").value(1))
                .andExpect(jsonPath("$.data.contentDetails[0].author").value("댓글러1"))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(100))
                .andExpect(jsonPath("$.data.pageSize").value(10))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.last").value(false))
                //문서 생성
                .andDo(document("board/get-list",
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
                .andDo(document("board/post-question",
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

    @DisplayName("[Docs] 질문 상세 조회")
    @Test
    void getQuestion() throws Exception {
        // given
        QuestionDetails response = createQuestionDetails();

        // stubbing
        BDDMockito.when(boardService.getQuestion(any(), anyLong())).thenReturn(response);

        // when // then
        mockMvc.perform(get("/board/question/{questionId}", 1)
                        .queryParam("page", "1")
                        .queryParam("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(
                        SuccessCode.FIND_QUESTION_DETAIL_SUCCESS.getIsSuccess()))
                .andExpect(jsonPath("$.message").value(
                        SuccessCode.FIND_QUESTION_DETAIL_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.FIND_BOARD_LIST_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.subject").value("질문 제목"))
                .andExpect(jsonPath("$.data.content").value("질문 내용"))
                .andExpect(jsonPath("$.data.createDate").isNotEmpty())
                .andExpect(jsonPath("$.data.modifyDate").isNotEmpty())
                .andExpect(jsonPath("$.data.author").value("테스터"))
                .andExpect(jsonPath("$.data.answerDetails").isNotEmpty())
                .andExpect(jsonPath("$.data.answerDetails.answerInfos").isArray())
                .andExpect(jsonPath("$.data.answerDetails.answerInfos[0].id").value(1L))
                .andExpect(jsonPath("$.data.answerDetails.answerInfos[0].content").value("내용1"))
                .andExpect(jsonPath("$.data.answerDetails.answerInfos[0].modifyDate").isNotEmpty())
                .andExpect(jsonPath("$.data.answerDetails.answerInfos[0].createdDate").isNotEmpty())
                .andExpect(jsonPath("$.data.answerDetails.answerInfos[0].author").value("테스터1"))
                .andExpect(jsonPath("$.data.answerDetails.currentPage").value(1))
                .andExpect(jsonPath("$.data.answerDetails.totalPages").value(2))
                .andExpect(jsonPath("$.data.answerDetails.totalElements").value(4))
                .andExpect(jsonPath("$.data.answerDetails.pageSize").value(2))
                .andExpect(jsonPath("$.data.answerDetails.hasNext").value(true))
                .andExpect(jsonPath("$.data.answerDetails.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.answerDetails.last").value(false))
                //문서 생성
                .andDo(document("board/get-question",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호")
                                        .optional()
                                        .attributes(key("validity").value("페이지 번호는 1 이상이어야 합니다."))
                                        .attributes(key("default").value(1)),
                                parameterWithName("size").description("페이지 사이즈")
                                        .optional()
                                        .attributes(key("validity").value("페이지 사이즈는 1 이상이어야 합니다."))
                                        .attributes(key("default").value(10))
                        ),
                        pathParameters(
                                parameterWithName("questionId").description("질문 식별 ID")
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
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("질문 식별 ID"),
                                fieldWithPath("data.subject").type(JsonFieldType.STRING)
                                        .description("질문 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("질문 내용"),
                                fieldWithPath("data.createDate").type(JsonFieldType.STRING)
                                        .description("질문 생성 날짜/시간"),
                                fieldWithPath("data.modifyDate").type(JsonFieldType.STRING)
                                        .description("질문 수정 날짜/시간"),
                                fieldWithPath("data.author").type(JsonFieldType.STRING)
                                        .description("질문 작성자"),
                                fieldWithPath("data.answerDetails").type(JsonFieldType.OBJECT)
                                        .description("질문에 대한 답변 목록"),
                                fieldWithPath("data.answerDetails.answerInfos").type(JsonFieldType.ARRAY)
                                        .description("답변 목록"),
                                fieldWithPath("data.answerDetails.answerInfos[].id").type(JsonFieldType.NUMBER)
                                        .description("답변 식별 ID"),
                                fieldWithPath("data.answerDetails.answerInfos[].content").type(JsonFieldType.STRING)
                                        .description("답변 내용"),
                                fieldWithPath("data.answerDetails.answerInfos[].modifyDate").type(JsonFieldType.STRING)
                                        .description("답변 질문 수정 날짜/시간"),
                                fieldWithPath("data.answerDetails.answerInfos[].createdDate").type(JsonFieldType.STRING)
                                        .description("답변 질문 생성 날짜/시간"),
                                fieldWithPath("data.answerDetails.answerInfos[].author").type(JsonFieldType.STRING)
                                        .description("답변 답변 작성자"),
                                fieldWithPath("data.answerDetails.currentPage").type(JsonFieldType.NUMBER)
                                        .description("답변 현재 페이지 번호"),
                                fieldWithPath("data.answerDetails.totalPages").type(JsonFieldType.NUMBER)
                                        .description("답변 전체 페이지 수"),
                                fieldWithPath("data.answerDetails.totalElements").type(JsonFieldType.NUMBER)
                                        .description("답변 전체 게시물 수"),
                                fieldWithPath("data.answerDetails.pageSize").type(JsonFieldType.NUMBER)
                                        .description("답변 페이지 크기"),
                                fieldWithPath("data.answerDetails.hasNext").type(JsonFieldType.BOOLEAN)
                                        .description("답변 다음 페이지 여부"),
                                fieldWithPath("data.answerDetails.hasPrevious").type(JsonFieldType.BOOLEAN)
                                        .description("답변 이전 페이지 여부"),
                                fieldWithPath("data.answerDetails.last").type(JsonFieldType.BOOLEAN)
                                        .description("답변 마지막 페이지 여부")
                                )
                ));
    }

    private List<BoardResponse.AnswerInfo> createAnswerInfos() {
        return IntStream.range(1, 3)
                .mapToObj(i -> BoardResponse.AnswerInfo
                        .builder()
                        .id(i)
                        .content("내용" + i)
                        .createdDate(LocalDateTime.now())
                        .modifyDate(LocalDateTime.now())
                        .author("테스터" + i)
                        .build())
                .toList();
    }

    private BoardResponse.AnswerDetails createAnswerDetails() {
        return BoardResponse.AnswerDetails
                .builder()
                .answerInfos(createAnswerInfos())
                .currentPage(1)
                .totalPages(2)
                .totalElements(4)
                .pageSize(2)
                .hasNext(true)
                .hasPrevious(false)
                .isLast(false)
                .build();
    }

    private BoardResponse.QuestionDetails createQuestionDetails() {
        return BoardResponse.QuestionDetails
                .builder()
                .id(1L)
                .subject("질문 제목")
                .content("질문 내용")
                .createDate(LocalDateTime.now())
                .modifyDate(LocalDateTime.now())
                .author("테스터")
                .answerDetails(createAnswerDetails())
                .build();
    }

    private BoardResponse.Content createQuestionContent() {
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