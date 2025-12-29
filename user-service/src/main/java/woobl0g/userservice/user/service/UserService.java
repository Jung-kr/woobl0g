package woobl0g.userservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.dto.SignUpRequestDto;
import woobl0g.userservice.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(ResponseCode.DUPLICATE_EMAIL);
        }

        User user = User.create(dto.getEmail(), dto.getName(), dto.getPassword());
        userRepository.save(user);
    }
}
