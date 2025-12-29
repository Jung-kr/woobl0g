package woobl0g.boardservice.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.domain.Board;
import woobl0g.boardservice.board.dto.CreateBoardRequestDto;
import woobl0g.boardservice.board.repository.BoardRepository;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public void create(CreateBoardRequestDto dto) {
        Board board = Board.create(dto.getTitle(), dto.getContent(), dto.getUserId());
        boardRepository.save(board);
    }
}
