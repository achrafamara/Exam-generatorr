package dev.danvega.repository;

import dev.danvega.model.TestHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestHistoryRepository extends JpaRepository<TestHistory, Long> {
    List<TestHistory> findByUserId(Long userId);
}