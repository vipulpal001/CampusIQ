package com.campusiq.campusiq.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.Department;

/**
 * ============================================================================
 * [CAMPUSIQ ERP REPOSITORY]: DepartmentRepository
 * Spring Data JPA Repository for academic departments
 * ============================================================================
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);
}
