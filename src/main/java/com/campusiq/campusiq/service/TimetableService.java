package com.campusiq.campusiq.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.TimetableEntry;
import com.campusiq.campusiq.repository.TimetableEntryRepository;

@Service
public class TimetableService {

    private final TimetableEntryRepository timetableEntryRepository;

    public TimetableService(TimetableEntryRepository timetableEntryRepository) {
        this.timetableEntryRepository = timetableEntryRepository;
    }

    public List<TimetableEntry> getAllEntries() {
        return timetableEntryRepository.findAll();
    }

    public List<TimetableEntry> getEntriesByDepartmentAndSemester(Long departmentId, Integer semester) {
        return timetableEntryRepository.findByDepartmentIdAndSemesterOrderByDayOfWeekAsc(departmentId, semester);
    }

    public Optional<TimetableEntry> getEntryById(Long id) {
        return timetableEntryRepository.findById(id);
    }

    public TimetableEntry saveEntry(TimetableEntry entry) {
        return timetableEntryRepository.save(entry);
    }

    public void deleteEntry(Long id) {
        timetableEntryRepository.deleteById(id);
    }

    public long count() {
        return timetableEntryRepository.count();
    }
}
