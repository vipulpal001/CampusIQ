package com.campusiq.campusiq.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campusiq.campusiq.dto.AttendanceSummaryDto;
import com.campusiq.campusiq.model.AttendanceRecord;
import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.repository.AttendanceRepository;
import com.campusiq.campusiq.repository.CourseRepository;
import com.campusiq.campusiq.repository.StudentRepository;

/**
 * ============================================================================
 * [CAMPUSIQ ERP SERVICE]: AttendanceService
 * Business logic for student attendance recording, calculation, and summaries
 * ============================================================================
 */
@Service
public class attendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public attendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void markAttendanceBatch(Long courseId, LocalDate date, Map<Long, String> statusMap, Map<Long, String> remarksMap) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found with id: " + courseId));

        for (Map.Entry<Long, String> entry : statusMap.entrySet()) {
            Long studentId = entry.getKey();
            String status = entry.getValue();
            String remarks = (remarksMap != null) ? remarksMap.get(studentId) : null;

            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                continue;
            }

            Optional<AttendanceRecord> existing = attendanceRepository
                    .findByStudentIdAndCourseIdAndDate(studentId, courseId, date);

            if (existing.isPresent()) {
                AttendanceRecord record = existing.get();
                record.setStatus(status != null ? status : "PRESENT");
                record.setRemarks(remarks);
                attendanceRepository.save(record);
            } else {
                AttendanceRecord record = new AttendanceRecord(
                        student,
                        course,
                        date,
                        status != null ? status : "PRESENT",
                        remarks
                );
                attendanceRepository.save(record);
            }
        }
    }

    public List<AttendanceRecord> getAttendanceByCourseAndDate(Long courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndDate(courseId, date);
    }

    public List<AttendanceRecord> getStudentAttendanceHistory(Long studentId) {
        return attendanceRepository.findByStudentIdOrderByDateDesc(studentId);
    }

    public AttendanceSummaryDto getStudentOverallSummary(Long studentId) {
        long total = attendanceRepository.countByStudentId(studentId);
        long present = attendanceRepository.countByStudentIdAndStatus(studentId, "PRESENT");
        long late = attendanceRepository.countByStudentIdAndStatus(studentId, "LATE");
        long excused = attendanceRepository.countByStudentIdAndStatus(studentId, "EXCUSED");

        return new AttendanceSummaryDto(total, present + late + excused);
    }

    public List<AttendanceSummaryDto> getStudentCourseWiseSummaries(Long studentId, List<Course> courses) {
        List<AttendanceSummaryDto> summaries = new ArrayList<>();
        for (Course course : courses) {
            long total = attendanceRepository.countByStudentIdAndCourseId(studentId, course.getId());
            long present = attendanceRepository.countByStudentIdAndCourseIdAndStatus(studentId, course.getId(), "PRESENT");
            long late = attendanceRepository.countByStudentIdAndCourseIdAndStatus(studentId, course.getId(), "LATE");
            long excused = attendanceRepository.countByStudentIdAndCourseIdAndStatus(studentId, course.getId(), "EXCUSED");

            summaries.add(new AttendanceSummaryDto(course, total, present + late + excused));
        }
        return summaries;
    }

    public String getOverallAverageAttendancePercentage() {
        long total = attendanceRepository.count();
        if (total == 0) {
            return "100%";
        }
        long present = attendanceRepository.countByStatus("PRESENT");
        long late = attendanceRepository.countByStatus("LATE");
        long excused = attendanceRepository.countByStatus("EXCUSED");

        double percentage = ((double) (present + late + excused) / total) * 100.0;
        return String.format("%.0f%%", percentage);
    }

    public List<AttendanceRecord> getRecentRecords() {
        return attendanceRepository.findTop20ByOrderByDateDescRecordedAtDesc();
    }
}
