package com.example.examservice.dto;

import java.util.List;

public class ExamAttemptResponseDto {
    public Long attemptId;
    public int score;
    public List<Integer> userAnswers;
    public List<Integer> correctAnswers;
    public boolean completed;
    public int triesLeft;
}
