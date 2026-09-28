package com.campusiq.campusiq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.SessionalMark;

@Repository
public interface SessionalMarkRepository extends JpaRepository<SessionalMark, Long> {

    List<SessionalMark> findByStudentId(Long studentId);

    List<SessionalMark> findByCourseId(Long courseId);

    Optional<SessionalMark> findByStudentIdAndCourseId(Long studentId, Long courseId);

    List<SessionalMark> findByStudentRollNumber(String rollNumber);
}
