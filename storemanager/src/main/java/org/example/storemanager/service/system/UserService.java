package org.example.storemanager.service.system;

import org.example.storemanager.dto.request.system.UserRequest;
import org.example.storemanager.dto.response.system.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest request);
}