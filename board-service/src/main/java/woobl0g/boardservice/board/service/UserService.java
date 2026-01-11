package woobl0g.boardservice.board.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.boardservice.board.domain.User;
import woobl0g.boardservice.board.dto.SavedUserRequestDto;
import woobl0g.boardservice.board.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void save(SavedUserRequestDto dto) {
        log.info("사용자 정보 동기화: userId={}, email={}", dto.getUserId(), dto.getEmail());
        
        User user = User.create(dto.getUserId(), dto.getName(), dto.getEmail());
        userRepository.save(user);
        
        log.debug("사용자 정보 저장 완료: userId={}", dto.getUserId());
    }
}
