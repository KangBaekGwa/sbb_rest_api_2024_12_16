package baekgwa.backend.integration;

import baekgwa.backend.domain.board.BoardService;
import baekgwa.backend.domain.user.UserService;
import baekgwa.backend.integration.factory.TestDataFactory;
import baekgwa.backend.model.answer.AnswerRepository;
import baekgwa.backend.model.question.QuestionRepository;
import baekgwa.backend.model.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class SpringBootTestSupporter {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected QuestionRepository questionRepository;

    @Autowired
    protected AnswerRepository answerRepository;

    @Autowired
    protected EntityManager em;

    @Autowired
    protected UserService userService;

    @Autowired
    protected BoardService boardService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected TestDataFactory testDataFactory;
}
