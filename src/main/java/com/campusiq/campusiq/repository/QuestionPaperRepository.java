package com.campusiq.campusiq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.QuestionPaper;

@Repository
public interface QuestionPaperRepository extends JpaRepository<QuestionPaper, Long> {

    List<QuestionPaper> findAllByOrderByExamYearDesc();

    List<QuestionPaper> findBySemesterOrderByExamYearDesc(Integer semester);

    List<QuestionPaper> findByCourseCode(String courseCode);
}
