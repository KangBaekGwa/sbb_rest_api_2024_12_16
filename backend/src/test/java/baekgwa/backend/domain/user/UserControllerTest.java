package baekgwa.backend.domain.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import baekgwa.backend.domain.user.UserRequest.Signup;
import baekgwa.backend.global.exception.CustomException;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.global.response.SuccessCode;
import baekgwa.backend.integration.MockControllerTestSupporter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

class UserControllerTest extends MockControllerTestSupporter {

    @Autowired
    public UserController userController;

    @DisplayName("[docs] 회원가입 테스트")
    @Test
    void signup1() throws Exception {
        // given
        Signup request = Signup
                .builder()
                .username("테스트유저")
                .email("test@test.com")
                .loginId("test1")
                .password("!asdf1234")
                .build();

        // when // then
        mockMvc.perform(post("/user/signup")
                    .content(objectMapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(SuccessCode.SIGNUP_SUCCESS.getIsSuccess()))
                .andExpect(jsonPath("$.message").value(SuccessCode.SIGNUP_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.code").value(SuccessCode.SIGNUP_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist())
                //문서 생성
                .andDo(document("user/signup",
                requestFields(
                        fieldWithPath("loginId").type(JsonFieldType.STRING)
                                .description("로그인 아이디")
                                .attributes(key("validity").value("로그인 아이디는 5자리 ~ 20자리 사이입니다. 로그인 아이디는 영문(대소문자 구분)과 숫자만 허용합니다.")),
                        fieldWithPath("password").type(JsonFieldType.STRING)
                                .description("패스워드")
                                .attributes(key("validity").value("비밀번호는 8자리 ~ 20자리 사이 입니다. 비밀번호는 특수문자를 반드시 포함하여야 합니다.")),
                        fieldWithPath("email").type(JsonFieldType.STRING)
                                .description("이메일")
                                .attributes(key("validity").value("이메일은 최소 3자리 부터 최대 25자리 까지 입니다. 이메일 형식을 지켜야 합니다.")),
                        fieldWithPath("username").type(JsonFieldType.STRING)
                                .description("회원 이름")
                                .attributes(key("validity").value("이름은 2글자 ~ 15글자 사이입니다. 이름은 한글과 영문만 허용됩니다."))
                ),
                responseFields(
                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                .description("성공 여부"),
                        fieldWithPath("message").type(JsonFieldType.STRING)
                                .description("응답 메세지"),
                        fieldWithPath("code").type(JsonFieldType.NUMBER)
                                .description("응답 코드"),
                        fieldWithPath("data").type(JsonFieldType.NULL)
                                .description("응답 데이터")
                ))
        );
    }

    @DisplayName("회원가입 중, 실패한다면 오류가 서빙됩니다.")
    @Test
    void signup2() throws Exception {
        // given
        Signup dto = Signup
                .builder()
                .username("테스트유저")
                .email("test@test.com")
                .loginId("test1")
                .password("!asdf1234")
                .build();

        // stubbing
        doThrow(new CustomException(ErrorCode.DUPLICATED_SIGNUP_DATA))
                .when(userService).signup(any());

        // when // then
        mockMvc.perform(post("/user/signup")
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(ErrorCode.DUPLICATED_SIGNUP_DATA.getIsSuccess()))
                .andExpect(jsonPath("$.message").value(ErrorCode.DUPLICATED_SIGNUP_DATA.getMessage()))
                .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATED_SIGNUP_DATA.getCode()))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}