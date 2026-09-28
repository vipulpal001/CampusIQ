package com.campusiq.campusiq.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.campusiq.campusiq.model.Notice;
import com.campusiq.campusiq.repository.NoticeRepository;

@Service
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<Notice> getNoticesForStudents() {
        return noticeRepository.findByTargetRoleInOrderByCreatedAtDesc(Arrays.asList("ALL", "STUDENT"));
    }

    public List<Notice> getNoticesForFaculty() {
        return noticeRepository.findByTargetRoleInOrderByCreatedAtDesc(Arrays.asList("ALL", "FACULTY"));
    }

    public Notice saveNotice(String title, String content, String category, String postedBy, String targetRole, boolean important) {
        Notice notice = new Notice(title, content, category, postedBy, targetRole, important);
        return noticeRepository.save(notice);
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    public long count() {
        return noticeRepository.count();
    }
}
