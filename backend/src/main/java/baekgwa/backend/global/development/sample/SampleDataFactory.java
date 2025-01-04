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
        addQuestionAndAnswer();
    }

    private void addTestUser() {
        User testUser1 = User.builder()
                .loginId("test")
                .username("테스터")
                .email("test@test.com")
                .password(passwordEncoder.encode("1234"))
                .role(UserRole.ROLE_USER)
                .uuid(UUID.randomUUID().toString())
                .build();

        User testUser2 = User.builder()
                .loginId("test2")
                .username("테스터2")
                .email("test2@test.com")
                .password(passwordEncoder.encode("1234"))
                .role(UserRole.ROLE_USER)
                .uuid(UUID.randomUUID().toString())
                .build();

        userRepository.save(testUser1);
        userRepository.save(testUser2);
    }

    private void addQuestionAndAnswer() {
        String subject = "번 질문 제목입니다.";
        String content = "번 상세 질문 내용입니다.";

        User questionUser = userRepository.findByLoginId("test").get();
        User answerUser = userRepository.findByLoginId("test2").get();

        Random random = new Random();

        for(int i=1; i<=100; i++) {
            Question question = Question
                    .builder()
                    .subject(i + subject)
                    .content(i + content)
                    .authorId(questionUser.getId())
                    .build();
            questionRepository.save(question);

            int randomCount = random.nextInt(5, 20);
            for(int j=1; j<randomCount; j++) {
                Answer answer = Answer
                        .builder()
                        .content(i + "질문에 대한 " + j + "번째 답변")
                        .question(question)
                        .authorId(answerUser.getId())
                        .build();

                answerRepository.save(answer);
            }
        }
    }
}
