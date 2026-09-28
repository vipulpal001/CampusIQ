package com.campusiq.campusiq.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ============================================================================
 * [CAMPUSIQ ERP ENTITY]: Faculty
 * Represents a teaching faculty / professor profile in the ERP system
 * ============================================================================
 */
@Entity
@Table(name = "faculties")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String designation = "Assistant Professor"; // Professor, Associate Professor, Assistant Professor, HOD

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "cabin_location")
    private String cabinLocation = "Block A, Room 302";

    public Faculty() {
    }

    public Faculty(String employeeCode, String fullName, String email, String phoneNumber, String designation, Department department, String cabinLocation) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.designation = designation;
        this.department = department;
        this.cabinLocation = cabinLocation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getCabinLocation() {
        return cabinLocation;
    }

    public void setCabinLocation(String cabinLocation) {
        this.cabinLocation = cabinLocation;
    }
}
