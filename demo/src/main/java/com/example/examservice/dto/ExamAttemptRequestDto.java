package com.example.examservice.dto;

import java.util.List;

public class ExamAttemptRequestDto {
    public String userId;
    public List<Integer> answers; // index per question
}