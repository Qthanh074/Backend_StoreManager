package org.example.storemanager.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.storemanager.repository.system.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component("securityEvaluator")
@RequiredArgsConstructor
public class SecurityEvaluator {

    private final UserRepository userRepository;

    public boolean hasPermission(String permissionCode) {
        // 1. Lấy thông tin user đang đăng nhập từ Context (do JWT filter set vào)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Truy cập bị từ chối: Người dùng chưa đăng nhập");
            return false;
        }

        String username = auth.getName();

        // 2. Query kiểm tra quyền từ Database
        // LƯU Ý CHO THÀNH: Ở giai đoạn production, bạn NÊN đưa danh sách quyền của User vào Redis
        // hoặc nén luôn vào chuỗi JWT để tránh việc mỗi request lại phải Query DB 1 lần.
        boolean isPermitted = userRepository.hasPermission(username, permissionCode);

        if (!isPermitted) {
            log.warn("Truy cập bị từ chối: User '{}' không có quyền '{}'", username, permissionCode);
        }

        return isPermitted;
    }
}