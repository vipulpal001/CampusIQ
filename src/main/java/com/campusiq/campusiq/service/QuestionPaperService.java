package com.campusiq.campusiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.QuestionPaper;
import com.campusiq.campusiq.repository.QuestionPaperRepository;

@Service
public class QuestionPaperService {

    private final QuestionPaperRepository questionPaperRepository;

    public QuestionPaperService(QuestionPaperRepository questionPaperRepository) {
        this.questionPaperRepository = questionPaperRepository;
    }

    public List<QuestionPaper> getAllQuestionPapers() {
        return questionPaperRepository.findAllByOrderByExamYearDesc();
    }

    public List<QuestionPaper> getQuestionPapersBySemester(Integer semester) {
        return questionPaperRepository.findBySemesterOrderByExamYearDesc(semester);
    }

    public Optional<QuestionPaper> getQuestionPaperById(Long id) {
        return questionPaperRepository.findById(id);
    }

    public QuestionPaper saveQuestionPaper(QuestionPaper questionPaper) {
        return questionPaperRepository.save(questionPaper);
    }

    public void deleteQuestionPaper(Long id) {
        questionPaperRepository.deleteById(id);
    }

    public long count() {
        return questionPaperRepository.count();
    }
}
