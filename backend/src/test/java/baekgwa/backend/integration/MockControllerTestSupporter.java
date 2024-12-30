package baekgwa.backend.integration;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import baekgwa.backend.domain.user.UserController;
import baekgwa.backend.domain.user.UserService;
import baekgwa.backend.global.config.SecurityConfig;
import baekgwa.backend.global.security.jwt.JWTUtil;
import baekgwa.backend.model.redis.RedisRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(controllers = {
        UserController.class
})
@Import({
        SecurityConfig.class,
        JWTUtil.class,
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
    protected UserService userService;

    @MockitoBean
    protected RedisRepository redisRepository;

    @BeforeEach
    void setup(RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(documentationConfiguration(provider)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }
}
