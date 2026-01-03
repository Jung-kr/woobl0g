package woobl0g.boardservice.board.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.dto.SavedUserRequestDto;
import woobl0g.boardservice.board.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void save(SavedUserRequestDto dto) {
        User user = User.create(dto.getUserId(), dto.getName(), dto.getEmail());
        userRepository.save(user);
    }
}
