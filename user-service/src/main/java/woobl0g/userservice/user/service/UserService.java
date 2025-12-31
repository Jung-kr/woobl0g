package woobl0g.userservice.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woobl0g.userservice.global.exception.UserException;
import woobl0g.userservice.global.response.ResponseCode;
import woobl0g.userservice.user.client.PointClient;
import woobl0g.userservice.user.domain.User;
import woobl0g.userservice.user.dto.SignUpRequestDto;
import woobl0g.userservice.user.dto.UserResponseDto;
import woobl0g.userservice.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PointClient pointClient;
    private final UserRepository userRepository;

    @Transactional
    public void signUp(SignUpRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new UserException(ResponseCode.DUPLICATE_EMAIL);
        }

        User user = User.create(dto.getEmail(), dto.getName(), dto.getPassword());
        User savedUser = userRepository.save(user);

        pointClient.addPoints(savedUser.getUserId(), "SIGN_UP");
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(ResponseCode.USER_NOT_FOUND));

        return UserResponseDto.from(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsers(List<Long> userIds) {
        List<User> users = userRepository.findAllById(userIds);

        return users.stream()
                .map(user -> new UserResponseDto(
                        user.getUserId(),
                        user.getEmail(),
                        user.getName()
                ))
                .toList();
    }
}
