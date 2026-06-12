package org.example.storemanager.repository.inventory;

import org.example.storemanager.entity.inventory.CancelIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CancelIssueRepository extends JpaRepository<CancelIssue, Long> {

    Optional<CancelIssue> findByCancelCodeAndIsDeletedFalse(String cancelCode);

    List<CancelIssue> findByBranchIdAndIsDeletedFalse(Long branchId);

    List<CancelIssue> findByIsDeletedFalse();
}
