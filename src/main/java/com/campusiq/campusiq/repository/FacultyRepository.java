package com.campusiq.campusiq.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.Faculty;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByEmail(String email);

    Optional<Faculty> findByEmployeeCode(String employeeCode);

    Optional<Faculty> findByPhoneNumber(String phoneNumber);

    List<Faculty> findByDepartmentId(Long departmentId);
}
