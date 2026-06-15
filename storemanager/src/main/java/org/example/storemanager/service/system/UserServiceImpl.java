package org.example.storemanager.service.system;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.storemanager.dto.request.system.UserRequest;
import org.example.storemanager.dto.response.system.UserResponse;
import org.example.storemanager.entity.system.User;
import org.example.storemanager.repository.system.UserRepository;
import org.example.storemanager.service.system.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.info("Đang tạo người dùng mới với username: {}", request.getUsername());

        return UserResponse.builder()
                .id(1L)
                .username(request.getUsername())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .status("ACTIVE")
                .build();
    }
}