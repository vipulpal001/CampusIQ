package com.campusiq.campusiq.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.campusiq.campusiq.model.TimetableEntry;

@Repository
public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

    List<TimetableEntry> findByDepartmentIdAndSemesterOrderByDayOfWeekAsc(Long departmentId, Integer semester);

    List<TimetableEntry> findByDayOfWeek(String dayOfWeek);
}
