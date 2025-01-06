package baekgwa.backend.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import baekgwa.backend.domain.board.BoardController;
import baekgwa.backend.domain.board.BoardService;
import baekgwa.backend.domain.user.UserController;
import baekgwa.backend.domain.user.UserService;
import baekgwa.backend.global.config.SecurityConfig;
import baekgwa.backend.global.security.entrypoint.CustomAccessDeniedHandler;
import baekgwa.backend.global.security.entrypoint.CustomAuthenticationEntryPoint;
import baekgwa.backend.global.security.filter.JWTFilter;
import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.global.security.user.CustomUserDetailService;
import baekgwa.backend.model.redis.RedisRepository;
import baekgwa.backend.model.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = {
        UserController.class,
        BoardController.class
})
@Import({
        SecurityConfig.class,
        JWTUtil.class,
        CustomUserDetailService.class,
        CustomAuthenticationEntryPoint.class,
        CustomAccessDeniedHandler.class
})
@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
public abstract class MockControllerTestSupporter {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext applicationContext;

    @MockitoBean
    private JWTFilter jwtFilter;

    @MockitoBean
    protected UserService userService;

    @MockitoBean
    protected BoardService boardService;

    @MockitoBean
    protected UserRepository userRepository;

    @MockitoBean
    protected RedisRepository redisRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider provider) throws ServletException, IOException {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(documentationConfiguration(provider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .apply(springSecurity())
                .build();

        //JwtFilter Mocking 진행
        //JwtFilter 를 Mocking 하지 않으면, API 문서를 위한 access Token 을 Decoding 해버려서, 유효성 오류가 발생함.
        BDDMockito.doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(invocation.getArgument(0), invocation.getArgument(1));
            return null;
        }).when(jwtFilter).doFilter(any(), any(), any());
    }
}
