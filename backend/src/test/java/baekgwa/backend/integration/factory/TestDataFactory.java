package baekgwa.backend.integration.factory;

import baekgwa.backend.model.answer.Answer;
import baekgwa.backend.model.answer.AnswerRepository;
import baekgwa.backend.model.question.Question;
import baekgwa.backend.model.question.QuestionRepository;
import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TestDataFactory {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final EntityManager em;

    /**
     * User : 1 ~ N명 (loginId = test1, username = 테스터1)
     * Question : 1 ~ M개 (subject = 1번 제목)
     * Answer : 질문당 K1 ~ K2개 생성 (content = 1번질문의 1번째 답변)
     * 만약 질문을 생성하고 싶지 않다면, k1 == k2 값을 입력하면 됩니다.
     * 저장 후, 명시적 flush / clear 진행합니다.
     * @return Object[첫번째로 저장된 회원, 첫번째로 저장된 질문, 첫번째로 저장된 답변]
     */
    public Object[] createUserAndQuestionAndAnswer(int N, int M, int k1, int k2) {
        User savedFirstUser = null;
        Question savedFirstQuestion = null;
        Answer savedFirstAnswer = null;

        for(int i=1; i<=N; i++) {
            User savedUser = userRepository.save(
                    User.createNewUser("test" + i, "테스터" + i, "test" + i + "@test.com", "1234"));
            if(savedFirstUser == null) savedFirstUser = savedUser;
        }

        for(int i=1; i<=M; i++) {
            Question question = Question.createNewQuestion(i + "번 제목", i + "번 내용", savedFirstUser.getId());
            Question savedQuestion = questionRepository.save(question);
            if(savedFirstQuestion == null) savedFirstQuestion = savedQuestion;

            if(k1 == k2) continue;
            int random = new Random().nextInt(k1, k2);
            for(int j=1; j<=random; j++) {
                Answer answer = Answer.createNewAnswer(i + "번 질문의 " + j + "번째 답변", question, savedFirstUser.getId());
                Answer savedAnswer = answerRepository.save(answer);
                if(savedFirstAnswer == null) savedFirstAnswer = savedAnswer;
            }
        }

        em.flush();
        em.clear();

        return new Object[] { savedFirstUser, savedFirstQuestion, savedFirstAnswer };
    }
}
