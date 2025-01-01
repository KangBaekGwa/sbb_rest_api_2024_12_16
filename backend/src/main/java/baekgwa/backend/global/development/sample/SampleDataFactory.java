package baekgwa.backend.global.development.sample;

import baekgwa.backend.model.answer.Answer;
import baekgwa.backend.model.answer.AnswerRepository;
import baekgwa.backend.model.question.Question;
import baekgwa.backend.model.question.QuestionRepository;
import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRepository;
import baekgwa.backend.model.user.UserRole;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Profile("dev")
public class SampleDataFactory {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    protected void registerSampleData() {
        addTestUser();
        addQuestion();
        addAnswer();
    }

    private void addTestUser() {
        User user = User.builder()
                .loginId("test")
                .username("테스터")
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .role(UserRole.ROLE_USER)
                .uuid(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);
    }

    private void addQuestion() {
        String subject = "번 질문 제목입니다.";
        String content = "번 상세 질문 내용입니다.";

        for(int i=1; i<=100; i++) {
            Question question = Question
                    .builder()
                    .subject(i + subject)
                    .content(i + content)
                    .authorId(1L)
                    .build();

            questionRepository.save(question);
        }
    }

    private void addAnswer() {
        for(int i=1; i<=100; i++) {
            int randomCount = new Random().nextInt(5, 20);
            for(int j=1; j<randomCount; j++) {
                Question question = questionRepository.findById((long) i).get();
                Answer answer = Answer
                        .builder()
                        .content(i + "질문에 대한 " + j + "번째 답변")
                        .question(question)
                        .authorId(1L)
                        .build();

                answerRepository.save(answer);
            }
        }
    }
}
