package baekgwa.backend.domain.board;

import baekgwa.backend.domain.board.BoardRequest.BoardList;
import baekgwa.backend.domain.board.BoardResponse.AnswerDetails;
import baekgwa.backend.domain.board.BoardResponse.NewAnswer;
import baekgwa.backend.domain.board.BoardResponse.NewQuestion;
import baekgwa.backend.global.exception.CustomException;
import baekgwa.backend.global.response.ErrorCode;
import baekgwa.backend.model.answer.Answer;
import baekgwa.backend.model.answer.AnswerRepository;
import baekgwa.backend.model.answer.projection.AnswerProjection;
import baekgwa.backend.model.category.CategoryType;
import baekgwa.backend.model.question.Question;
import baekgwa.backend.model.question.QuestionRepository;
import baekgwa.backend.model.question.projection.QuestionProjection;
import baekgwa.backend.model.user.User;
import baekgwa.backend.model.user.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;
    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    @Override
    public BoardResponse.Content getList(BoardList request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(),
                Sort.by(Sort.Order.desc(
                        request.getSort())));

        if (request.getCategory().equals(CategoryType.QUESTION.name())) {
            return getQuestionList(pageable, request.getKeyword());
        }
        return null;
    }

    @Transactional
    @Override
    public BoardResponse.NewQuestion createNewQuestion(BoardRequest.NewQuestion request, String uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_LOGIN_INFO));
        Question newQuestion = Question.createNewQuestion(request.getSubject(),
                request.getContent(), user.getId());
        Question savedQuestion = questionRepository.save(newQuestion);

        return BoardResponse.NewQuestion.from(savedQuestion.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public BoardResponse.QuestionDetails getQuestion(
            BoardRequest.AnswerList request, long questionId
    ) {
        //질문 정보 찾기
        QuestionProjection.Details findQuestion = questionRepository.findQuestionDetailById(questionId)
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_QUESTION));

        //질문과 관련된 답변 Paging 찾기
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Page<AnswerProjection.Details> findAnswerList =
                answerRepository.findAnswerListByQuestionId(pageable, findQuestion.id());
        AnswerDetails findAnswer = AnswerDetails.from(findAnswerList);

        return BoardResponse.QuestionDetails.from(findQuestion, findAnswer);
    }

    @Transactional
    @Override
    public BoardResponse.NewAnswer createNewAnswer(BoardRequest.NewAnswer request, long questionId, String uuid) {

        User findUser = userRepository.findByUuid(uuid).orElseThrow(
                () -> new CustomException(ErrorCode.INVALID_LOGIN_INFO));

        Answer newAnswer = Answer.createNewAnswer(
                request.getContent(),
                entityManager.getReference(Question.class, questionId),
                findUser.getId()
        );

        Answer saveAnswer = answerRepository.save(newAnswer);
        return BoardResponse.NewAnswer.from(saveAnswer.getId());
    }

    private BoardResponse.Content getQuestionList(Pageable pageable, String keyword) {
        Page<QuestionProjection.QuestionList> questionPageList =
                questionRepository.findQuestionByKeyword(keyword, pageable);

        List<BoardResponse.ContentDetails> contentDetails = questionPageList.getContent().stream()
                .map(BoardResponse.ContentDetails::from)
                .toList();

        return BoardResponse.Content.from(contentDetails, questionPageList);
    }
}