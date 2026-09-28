package com.campusiq.campusiq.config;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.campusiq.campusiq.model.AttendanceRecord;
import com.campusiq.campusiq.model.Course;
import com.campusiq.campusiq.model.Department;
import com.campusiq.campusiq.model.Faculty;
import com.campusiq.campusiq.model.Notice;
import com.campusiq.campusiq.model.QuestionPaper;
import com.campusiq.campusiq.model.SessionalMark;
import com.campusiq.campusiq.model.Student;
import com.campusiq.campusiq.model.TimetableEntry;
import com.campusiq.campusiq.model.User;
import com.campusiq.campusiq.repository.AttendanceRepository;
import com.campusiq.campusiq.repository.CourseRepository;
import com.campusiq.campusiq.repository.DepartmentRepository;
import com.campusiq.campusiq.repository.FacultyRepository;
import com.campusiq.campusiq.repository.NoticeRepository;
import com.campusiq.campusiq.repository.QuestionPaperRepository;
import com.campusiq.campusiq.repository.SessionalMarkRepository;
import com.campusiq.campusiq.repository.StudentRepository;
import com.campusiq.campusiq.repository.TimetableEntryRepository;
import com.campusiq.campusiq.repository.UserRepository;

/**
 * ============================================================================
 * [CAMPUSIQ ERP INITIALIZER]: DataInitializer
 * Seeds starter users (Admin, Faculty, Student), departments, courses,
 * faculty profiles, students, attendance, sessional marks, timetable entries,
 * past university papers, and circular notices.
 * ============================================================================
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final FacultyRepository facultyRepository;
    private final SessionalMarkRepository sessionalMarkRepository;
    private final NoticeRepository noticeRepository;
    private final QuestionPaperRepository questionPaperRepository;
    private final TimetableEntryRepository timetableEntryRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           CourseRepository courseRepository,
                           StudentRepository studentRepository,
                           AttendanceRepository attendanceRepository,
                           FacultyRepository facultyRepository,
                           SessionalMarkRepository sessionalMarkRepository,
                           NoticeRepository noticeRepository,
                           QuestionPaperRepository questionPaperRepository,
                           TimetableEntryRepository timetableEntryRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.facultyRepository = facultyRepository;
        this.sessionalMarkRepository = sessionalMarkRepository;
        this.noticeRepository = noticeRepository;
        this.questionPaperRepository = questionPaperRepository;
        this.timetableEntryRepository = timetableEntryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // =====================================================================
        // 1. SEED / ENSURE SYSTEM USERS (ADMIN, FACULTY, STUDENT)
        // =====================================================================
        // 1A. Admin Account (admin / admin123)
        User admin = userRepository.findByUsername("admin").orElseGet(User::new);
        admin.setUsername("admin");
        if (admin.getEmail() == null || admin.getEmail().isBlank()) {
            admin.setEmail("admin@rkgit.edu.in");
        }
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        admin.setFullName("System Administrator");
        admin.setVerified(true);
        userRepository.save(admin);

        // 1B. Faculty Account (faculty / faculty123)
        User faculty = userRepository.findByUsername("faculty").orElseGet(User::new);
        faculty.setUsername("faculty");
        if (faculty.getEmail() == null || faculty.getEmail().isBlank()) {
            faculty.setEmail("dr.sharma@rkgit.edu.in");
        }
        faculty.setPassword(passwordEncoder.encode("faculty123"));
        faculty.setRole("FACULTY");
        faculty.setFullName("Dr. Rajesh Sharma");
        faculty.setVerified(true);
        userRepository.save(faculty);

        // 1C. Student Account (student / student123)
        User student = userRepository.findByUsername("student").orElseGet(User::new);
        student.setUsername("student");
        if (student.getEmail() == null || student.getEmail().isBlank()) {
            student.setEmail("tanmay.atray@rkgit.edu.in");
        }
        student.setPassword(passwordEncoder.encode("student123"));
        student.setRole("STUDENT");
        student.setFullName("Tanmay Atray");
        student.setVerified(true);
        userRepository.save(student);

        // 1D. Auto-verify any administrator users in the database
        userRepository.findAll().forEach(u -> {
            if (u.getRole() != null && u.getRole().toUpperCase().contains("ADMIN") && !u.isVerified()) {
                u.setVerified(true);
                userRepository.save(u);
            }
        });

        // =====================================================================
        // 2. SEED DEPARTMENTS & COURSES
        // =====================================================================
        Department cse = departmentRepository.findByCode("CSE").orElseGet(() ->
                departmentRepository.save(new Department("CSE", "Computer Science & Engineering", "Department of Computer Science and Engineering"))
        );
        Department it = departmentRepository.findByCode("IT").orElseGet(() ->
                departmentRepository.save(new Department("IT", "Information Technology", "Department of Information Technology"))
        );
        Department ece = departmentRepository.findByCode("ECE").orElseGet(() ->
                departmentRepository.save(new Department("ECE", "Electronics & Communication", "Department of Electronics & Communication"))
        );
        Department me = departmentRepository.findByCode("ME").orElseGet(() ->
                departmentRepository.save(new Department("ME", "Mechanical Engineering", "Department of Mechanical Engineering"))
        );
        departmentRepository.findByCode("MBA").orElseGet(() ->
                departmentRepository.save(new Department("MBA", "Business Administration", "Department of Management Studies"))
        );

        Course cs101 = courseRepository.findByCourseCode("CS101").orElseGet(() ->
                courseRepository.save(new Course("CS101", "Data Structures & Algorithms", 4, 3, cse))
        );
        Course cs201 = courseRepository.findByCourseCode("CS201").orElseGet(() ->
                courseRepository.save(new Course("CS201", "Database Management Systems", 4, 3, cse))
        );
        courseRepository.findByCourseCode("IT101").orElseGet(() ->
                courseRepository.save(new Course("IT101", "Full Stack Web Development", 3, 2, it))
        );
        courseRepository.findByCourseCode("EC101").orElseGet(() ->
                courseRepository.save(new Course("EC101", "Digital Electronics & Circuits", 4, 1, ece))
        );
        courseRepository.findByCourseCode("ME101").orElseGet(() ->
                courseRepository.save(new Course("ME101", "Thermodynamics & Fluid Mechanics", 3, 2, me))
        );

        // =====================================================================
        // 3. SEED FACULTY MEMBERS
        // =====================================================================
        if (facultyRepository.count() == 0) {
            facultyRepository.save(new Faculty("FAC-CSE-101", "Dr. Rajesh Sharma", "dr.sharma@rkgit.edu.in", "9876500001", "Associate Professor & HOD", cse, "Block B, Room 204"));
            facultyRepository.save(new Faculty("FAC-IT-102", "Prof. Sunita Gupta", "sunita.gupta@rkgit.edu.in", "9876500002", "Assistant Professor", it, "Block A, Room 108"));
            facultyRepository.save(new Faculty("FAC-ECE-103", "Dr. Manoj Saxena", "manoj.saxena@rkgit.edu.in", "9876500003", "Professor", ece, "Block C, Room 301"));
        }

        // =====================================================================
        // 4. SEED STUDENTS (Including TANMAY ATRAY)
        // =====================================================================
        Student tanmay = studentRepository.findByRollNumber("2024CSE042").orElseGet(() ->
                studentRepository.save(new Student("2024CSE042", "Tanmay", "Atray", "tanmay.atray@rkgit.edu.in", "9876543219", "Male", LocalDate.of(2004, 3, 15), 5, cse))
        );

        Student amit = studentRepository.findByRollNumber("2024CSE001").orElseGet(() ->
                studentRepository.save(new Student("2024CSE001", "Amit", "Kumar", "amit.kumar@campusiq.edu", "9876543210", "Male", LocalDate.of(2003, 5, 14), 3, cse))
        );
        Student priya = studentRepository.findByRollNumber("2024CSE002").orElseGet(() ->
                studentRepository.save(new Student("2024CSE002", "Priya", "Sharma", "priya.sharma@campusiq.edu", "9876543211", "Female", LocalDate.of(2004, 8, 22), 3, cse))
        );

        // =====================================================================
        // 5. SEED ATTENDANCE RECORDS (Interconnected with Tanmay Atray)
        // =====================================================================
        if (attendanceRepository.count() == 0) {
            attendanceRepository.save(new AttendanceRecord(tanmay, cs101, LocalDate.now().minusDays(5), "PRESENT", "On time"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs101, LocalDate.now().minusDays(4), "PRESENT", "Active participation"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs101, LocalDate.now().minusDays(3), "PRESENT", "On time"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs101, LocalDate.now().minusDays(2), "ABSENT", "Sick leave"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs101, LocalDate.now().minusDays(1), "PRESENT", "On time"));

            attendanceRepository.save(new AttendanceRecord(tanmay, cs201, LocalDate.now().minusDays(5), "PRESENT", "Lab completed"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs201, LocalDate.now().minusDays(4), "PRESENT", "On time"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs201, LocalDate.now().minusDays(3), "PRESENT", "On time"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs201, LocalDate.now().minusDays(2), "PRESENT", "On time"));
            attendanceRepository.save(new AttendanceRecord(tanmay, cs201, LocalDate.now().minusDays(1), "PRESENT", "On time"));
        }

        // =====================================================================
        // 6. SEED SESSIONAL MARKS (Recorded by Faculty for Tanmay)
        // =====================================================================
        if (sessionalMarkRepository.count() == 0) {
            sessionalMarkRepository.save(new SessionalMark(tanmay, cs101, 27.0, 26.5, 18.0, 18.5, "Excellent code quality & algorithmic clarity"));
            sessionalMarkRepository.save(new SessionalMark(tanmay, cs201, 25.0, 24.5, 17.5, 18.0, "Great understanding of SQL normalization"));
            sessionalMarkRepository.save(new SessionalMark(amit, cs101, 21.0, 22.0, 15.0, 14.0, "Good effort in data structures"));
        }

        // =====================================================================
        // 7. SEED NOTICES (Published by Admin & Faculty)
        // =====================================================================
        if (noticeRepository.count() == 0) {
            noticeRepository.save(new Notice(
                    "Sessional-II Examination Schedule Announced",
                    "The Department of Computer Science & Engineering will conduct Sessional-II examinations starting next Monday. Students must carry their institute ID cards.",
                    "EXAMINATION",
                    "Dean Academics",
                    "ALL",
                    true
            ));

            noticeRepository.save(new Notice(
                    "AKTU University Examination Form Submission Guidelines",
                    "All B.Tech students must verify their semester subject electives on the AKTU ERP portal and clear outstanding tuition dues before the 28th of this month.",
                    "ACADEMIC",
                    "Registrar Office",
                    "STUDENT",
                    true
            ));

            noticeRepository.save(new Notice(
                    "Hackathon 2026: CampusIQ Smart Solutions Challenge",
                    "Registrations are now open for the annual inter-college hackathon. Top 3 winning teams will be awarded cash prizes and incubation support.",
                    "EVENT",
                    "Dr. Rajesh Sharma (HOD CSE)",
                    "STUDENT",
                    false
            ));
        }

        // =====================================================================
        // 8. SEED UNIVERSITY QUESTION PAPERS
        // =====================================================================
        if (questionPaperRepository.count() == 0) {
            questionPaperRepository.save(new QuestionPaper("CS101", "Data Structures & Algorithms", "KCS-301", 3, 2024, "End Semester Examination", "https://aktu.ac.in"));
            questionPaperRepository.save(new QuestionPaper("CS201", "Database Management Systems", "KCS-401", 4, 2024, "End Semester Examination", "https://aktu.ac.in"));
            questionPaperRepository.save(new QuestionPaper("IT101", "Full Stack Web Development", "KIT-302", 3, 2023, "End Semester Examination", "https://aktu.ac.in"));
            questionPaperRepository.save(new QuestionPaper("EC101", "Digital Electronics & Circuits", "KEC-301", 3, 2023, "Carry Over Examination", "https://aktu.ac.in"));
            questionPaperRepository.save(new QuestionPaper("CS301", "Operating Systems & Concurrency", "KCS-501", 5, 2024, "End Semester Examination", "https://aktu.ac.in"));
        }

        // =====================================================================
        // 9. SEED TIMETABLE SLOTS
        // =====================================================================
        if (timetableEntryRepository.count() == 0) {
            timetableEntryRepository.save(new TimetableEntry("MON", "09:00 - 10:00", cs101, "Data Structures & Algorithms", "Dr. Rajesh Sharma", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("MON", "10:00 - 11:00", cs201, "Database Management Systems", "Prof. Sunita Gupta", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("MON", "02:00 - 04:00", cs101, "Data Structures Lab", "Dr. Rajesh Sharma", "Lab 4", cse, 5));

            timetableEntryRepository.save(new TimetableEntry("TUE", "09:00 - 10:00", cs201, "Database Management Systems", "Prof. Sunita Gupta", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("TUE", "10:00 - 11:00", cs101, "Data Structures & Algorithms", "Dr. Rajesh Sharma", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("TUE", "02:00 - 04:00", cs201, "DBMS & SQL Lab", "Prof. Sunita Gupta", "Lab 2", cse, 5));

            timetableEntryRepository.save(new TimetableEntry("WED", "09:00 - 10:00", cs101, "Data Structures & Algorithms", "Dr. Rajesh Sharma", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("WED", "10:00 - 11:00", cs201, "Database Management Systems", "Prof. Sunita Gupta", "R301", cse, 5));

            timetableEntryRepository.save(new TimetableEntry("THU", "09:00 - 10:00", cs201, "Database Management Systems", "Prof. Sunita Gupta", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("THU", "10:00 - 11:00", cs101, "Data Structures & Algorithms", "Dr. Rajesh Sharma", "R301", cse, 5));

            timetableEntryRepository.save(new TimetableEntry("FRI", "09:00 - 10:00", cs101, "Data Structures & Algorithms", "Dr. Rajesh Sharma", "R301", cse, 5));
            timetableEntryRepository.save(new TimetableEntry("FRI", "10:00 - 11:00", cs201, "Database Management Systems", "Prof. Sunita Gupta", "R301", cse, 5));
        }
    }
}
