package com.example.examservice.service;

import com.example.examservice.dto.*;
import com.example.examservice.model.*;
import com.example.examservice.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExamService {
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamAttemptRepository attemptRepository;

    public ExamService(ExamRepository examRepository, QuestionRepository questionRepository, ExamAttemptRepository attemptRepository) {
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
    }

    // Map Exam to DTO
    private ExamDto toExamDto(Exam exam) {
        ExamDto dto = new ExamDto();
        dto.id = exam.getId();
        dto.title = exam.getTitle();
        dto.questions = exam.getQuestions() != null ? exam.getQuestions().stream().map(this::toQuestionDto).collect(Collectors.toList()) : List.of();
        return dto;
    }

    private QuestionDto toQuestionDto(Question q) {
        QuestionDto dto = new QuestionDto();
        dto.id = q.getId();
        dto.text = q.getText();
        dto.choices = q.getChoices();
        // Only for admin/creation
        // dto.correctAnswerIndex = q.getCorrectAnswerIndex();
        return dto;
    }

    public ExamDto createExam(ExamDto dto) {
        Exam exam = new Exam();
        exam.setTitle(dto.title);
        if (dto.questions != null) {
            List<Question> questions = dto.questions.stream().map(qdto -> {
                Question q = new Question();
                q.setText(qdto.text);
                q.setChoices(qdto.choices);
                q.setCorrectAnswerIndex(qdto.correctAnswerIndex);
                q.setExam(exam);
                return q;
            }).collect(Collectors.toList());
            exam.setQuestions(questions);
        }
        Exam saved = examRepository.save(exam);
        return toExamDto(saved);
    }

    public ExamDto getExam(Long id) {
        return examRepository.findById(id).map(this::toExamDto).orElseThrow();
    }

    public List<ExamDto> getAllExams() {
        return examRepository.findAll().stream().map(this::toExamDto).collect(Collectors.toList());
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

    public QuestionDto addQuestion(Long examId, QuestionDto qdto) {
        Exam exam = examRepository.findById(examId).orElseThrow();
        Question q = new Question();
        q.setText(qdto.text);
        q.setChoices(qdto.choices);
        q.setCorrectAnswerIndex(qdto.correctAnswerIndex);
        q.setExam(exam);
        Question saved = questionRepository.save(q);
        return toQuestionDto(saved);
    }

    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    @Transactional
    public ExamAttemptResponseDto attemptExam(Long examId, String userId, List<Integer> userAnswers) {
        Exam exam = examRepository.findById(examId).orElseThrow();
        List<ExamAttempt> attempts = attemptRepository.findByExamIdAndUserIdOrderByCreatedAtAsc(examId, userId);

        if (attempts.size() >= 3) {
            ExamAttempt last = attempts.get(attempts.size() - 1);
            return toAttemptResponseDto(last, exam.getQuestions());
        }

        List<Question> questions = exam.getQuestions();
        List<Integer> correctAnswers = questions.stream()
                .map(Question::getCorrectAnswerIndex)
                .collect(Collectors.toList());
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (i < userAnswers.size() && userAnswers.get(i).equals(correctAnswers.get(i))) score++;
        }

        boolean completed = (attempts.size() + 1) >= 3;
        ExamAttempt attempt = new ExamAttempt();
        attempt.setExam(exam);
        attempt.setUserId(userId);
        attempt.setAnswers(userAnswers);
        attempt.setScore(score);
        attempt.setTries(attempts.size() + 1);
        attempt.setCompleted(completed);

        ExamAttempt savedAttempt = attemptRepository.save(attempt);

        return toAttemptResponseDto(savedAttempt, questions);
    }

    private ExamAttemptResponseDto toAttemptResponseDto(ExamAttempt attempt, List<Question> questions) {
        ExamAttemptResponseDto resp = new ExamAttemptResponseDto();
        resp.attemptId = attempt.getId();
        resp.score = attempt.getScore();
        resp.userAnswers = attempt.getAnswers();
        resp.correctAnswers = questions.stream().map(Question::getCorrectAnswerIndex).collect(Collectors.toList());
        resp.completed = attempt.isCompleted();
        resp.triesLeft = Math.max(0, 3 - attempt.getTries());
        return resp;
    }
}
