package com.campusiq.campusiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.repository.StudentRepository;

/**
 * ============================================================================
 * [CAMPUSIQ ERP SERVICE]: StudentService
 * Business logic for Student Information System (SIS), search, and analytics
 * ============================================================================
 */
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByRollNumber(String rollNumber) {
        return studentRepository.findByRollNumber(rollNumber);
    }

    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public List<Student> getRecentStudents() {
        return studentRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<Student> searchStudents(String keyword, Long departmentId) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        boolean hasDepartment = departmentId != null && departmentId > 0;

        if (hasDepartment && hasKeyword) {
            return studentRepository.searchStudentsByDepartment(departmentId, keyword.trim());
        } else if (hasDepartment) {
            return studentRepository.findByDepartmentId(departmentId);
        } else if (hasKeyword) {
            return studentRepository.searchStudents(keyword.trim());
        } else {
            return studentRepository.findAll();
        }
    }

    public long count() {
        return studentRepository.count();
    }

    public long countActiveStudents() {
        return studentRepository.countByStatus("ACTIVE");
    }
}
