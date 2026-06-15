package org.example.storemanager.repository.system;

import org.example.storemanager.entity.system.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END " +
            "FROM User u " +
            "JOIN u.role r " +
            "JOIN r.rolePermissions rp " +
            "JOIN rp.permission p " +
            "WHERE u.username = :username " +
            "AND p.permissionCode = :permissionCode " +
            "AND u.isDeleted = false " +
            "AND u.status = 'ACTIVE' " +
            "AND r.isActive = true")
    boolean hasPermission(@Param("username") String username, @Param("permissionCode") String permissionCode);
}