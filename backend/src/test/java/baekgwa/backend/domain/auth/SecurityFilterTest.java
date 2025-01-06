package baekgwa.backend.domain.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import baekgwa.backend.global.constant.JwtConstants;
import baekgwa.backend.global.response.SuccessCode;
import baekgwa.backend.global.security.filter.LoginDto;
import baekgwa.backend.integration.MockControllerTestSupporter;
import baekgwa.backend.model.user.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SecurityFilterTest extends MockControllerTestSupporter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @DisplayName("[Docs] 로그인 요청을 처리합니다.")
    @Test
    void loginFilter() throws Exception {
        // given
        LoginDto request = LoginDto.builder().loginId("test").password("1234").build();
        User registeredUser = User.createNewUser("test", "테스터", "test@test.com",
                passwordEncoder.encode("1234"));

        // stubbing
        BDDMockito.when(userRepository.findByLoginId(anyString()))
                .thenReturn(Optional.of(registeredUser));

        // when // then
        mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(
                        SuccessCode.LOGIN_SUCCESS.getIsSuccess()))
                .andExpect(jsonPath("$.message").value(
                        SuccessCode.LOGIN_SUCCESS.getMessage()))
                .andExpect(
                        jsonPath("$.code").value(SuccessCode.LOGIN_SUCCESS.getCode()))
                .andExpect(header().exists(JwtConstants.ACCESS))
                .andExpect(result -> {
                    String access = result.getResponse().getHeader(JwtConstants.ACCESS);
                    assertNotNull(access);
                    assertFalse(access.isBlank());
                })
                .andExpect(cookie().exists(JwtConstants.REFRESH))
                .andExpect(result -> {
                    String refresh = result.getResponse().getCookie(JwtConstants.REFRESH)
                            .getValue();
                    assertNotNull(refresh);
                    assertFalse(refresh.isBlank());
                })
                //문서 생성
                .andDo(document("user/post-login",
                        requestFields(
                                fieldWithPath("loginId").type(JsonFieldType.STRING)
                                        .description("로그인 아이디"),
                                fieldWithPath("password").type(JsonFieldType.STRING)
                                        .description("패스워드")
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
                        ),
                        responseHeaders(
                                headerWithName(JwtConstants.ACCESS)
                                        .description("Access Token")
                        ),
                        responseCookies(
                                cookieWithName(JwtConstants.REFRESH)
                                        .description("Refresh Token")
                        ))
                );
    }
}
