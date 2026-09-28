package com.campusiq.campusiq.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.SessionalMark;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.repository.CourseRepository;
import com.campusiq.campusiq.repository.SessionalMarkRepository;
import com.campusiq.campusiq.repository.StudentRepository;

@Service
public class SessionalMarkService {

    private final SessionalMarkRepository sessionalMarkRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public SessionalMarkService(SessionalMarkRepository sessionalMarkRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.sessionalMarkRepository = sessionalMarkRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<SessionalMark> getMarksByStudentId(Long studentId) {
        return sessionalMarkRepository.findByStudentId(studentId);
    }

    public List<SessionalMark> getMarksByStudentRollNumber(String rollNumber) {
        return sessionalMarkRepository.findByStudentRollNumber(rollNumber);
    }

    public List<SessionalMark> getMarksByCourseId(Long courseId) {
        return sessionalMarkRepository.findByCourseId(courseId);
    }

    public Optional<SessionalMark> getMark(Long studentId, Long courseId) {
        return sessionalMarkRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @Transactional
    public SessionalMark recordOrUpdateMark(Long studentId, Long courseId,
                                           Double s1, Double s2, Double assignment, Double ta, String remarks) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found ID: " + studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found ID: " + courseId));

        SessionalMark mark = sessionalMarkRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElse(new SessionalMark(student, course, s1, s2, assignment, ta, remarks));

        mark.setSessionalOne(s1 != null ? s1 : 0.0);
        mark.setSessionalTwo(s2 != null ? s2 : 0.0);
        mark.setAssignmentMarks(assignment != null ? assignment : 0.0);
        mark.setTeacherAssessment(ta != null ? ta : 0.0);
        mark.setRemarks(remarks);
        mark.calculateTotal();

        return sessionalMarkRepository.save(mark);
    }

    @Transactional
    public void batchRecordMarks(Long courseId,
                                 Map<Long, Double> s1Map,
                                 Map<Long, Double> s2Map,
                                 Map<Long, Double> assMap,
                                 Map<Long, Double> taMap,
                                 Map<Long, String> remarksMap) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found ID: " + courseId));

        for (Long studentId : s1Map.keySet()) {
            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) continue;

            Double s1 = s1Map.getOrDefault(studentId, 0.0);
            Double s2 = s2Map.getOrDefault(studentId, 0.0);
            Double ass = assMap.getOrDefault(studentId, 0.0);
            Double ta = taMap.getOrDefault(studentId, 0.0);
            String remarks = remarksMap.getOrDefault(studentId, "");

            SessionalMark mark = sessionalMarkRepository.findByStudentIdAndCourseId(studentId, courseId)
                    .orElse(new SessionalMark(student, course, s1, s2, ass, ta, remarks));

            mark.setSessionalOne(s1);
            mark.setSessionalTwo(s2);
            mark.setAssignmentMarks(ass);
            mark.setTeacherAssessment(ta);
            mark.setRemarks(remarks);
            mark.calculateTotal();

            sessionalMarkRepository.save(mark);
        }
    }

    public long count() {
        return sessionalMarkRepository.count();
    }
}
