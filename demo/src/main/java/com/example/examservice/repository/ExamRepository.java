package com.example.examservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.examservice.model.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long> {
}