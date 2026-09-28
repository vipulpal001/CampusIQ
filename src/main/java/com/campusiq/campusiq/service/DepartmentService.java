package com.campusiq.campusiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.repository.DepartmentRepository;

/**
 * ============================================================================
 * [CAMPUSIQ ERP SERVICE]: DepartmentService
 * Business logic for academic department management
 * ============================================================================
 */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    public Department saveDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }

    public long count() {
        return departmentRepository.count();
    }
}
