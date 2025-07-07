package com.example.examservice.repository;

import com.example.examservice.model.ExamAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {
    List<ExamAttempt> findByExamIdAndUserIdOrderByCreatedAtAsc(Long examId, String userId);
}
