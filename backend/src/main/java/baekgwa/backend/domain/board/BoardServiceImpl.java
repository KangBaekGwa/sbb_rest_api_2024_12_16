package baekgwa.backend.domain.board;

import baekgwa.backend.domain.board.BoardResponse.ContentDetails;
import baekgwa.backend.model.category.CategoryType;
import baekgwa.backend.model.question.QuestionRepository;
import baekgwa.backend.model.question.projection.QuestionWithAnswerCountProjection;
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

    @Transactional(readOnly = true)
    @Override
    public BoardResponse.Content getList(BoardRequest.List request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), Sort.by(Sort.Order.desc(
                request.getSort())));

        if(request.getCategory().equals(CategoryType.QUESTION.name())) {
            return getQuestionList(pageable, request.getKeyword());
        }
        return null;
    }

    private BoardResponse.Content getQuestionList(Pageable pageable, String keyword) {
        Page<QuestionWithAnswerCountProjection> findData
                = questionRepository.findByKeywordWithQueryDsl(keyword, pageable);

        List<ContentDetails> contentDetails = findData.getContent().stream()
                .map(q -> ContentDetails
                        .builder()
                        .id(q.getId())
                        .subject(q.getSubject())
                        .createdDate(q.getCreateDate())
                        .answerCount(q.getAnswerCount())
                        .author(q.getUsername())
                        .build())
                .toList();

        return BoardResponse.Content
                .builder()
                .contentDetails(contentDetails)
                .currentPage(findData.getNumber() + 1)
                .totalPages(findData.getTotalPages())
                .totalElements(findData.getTotalElements())
                .pageSize(findData.getSize())
                .hasNext(findData.hasNext())
                .hasPrevious(findData.hasPrevious())
                .isLast(findData.isLast())
                .build();
    }
}