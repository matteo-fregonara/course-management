package nl.tudelft.sem.template.repositories;

import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.CourseView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times in parameter names
public interface CourseRepository  extends JpaRepository<Course, Integer> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM courses c WHERE c.course_code = :course_code")
    Optional<Course> findCourseByCourseCode(
            @Param("course_code") int courseCode);

    // Gets all courses based on a list of course_codes
    @Query(nativeQuery = true,
            value = "SELECT * FROM courses c WHERE c.course_code IN :course_code")
    List<Course> findCourseByCourseCodes(
            @Param("course_code") List<Integer> courseCode);

    // Updates a course based on the provided parameters
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "UPDATE courses c SET c.description = :description,"
                    + "c.max_size = :max_size, c.can_enroll = :can_enroll "
                    + "WHERE c.course_code = :course_code")
    void updateCourse(@Param("course_code") int courseCode,
                      @Param("description") String description,
                      @Param("max_size") int maxSize,
                      @Param("can_enroll") boolean canEnroll);

    // Deletes a course based on the provided course code
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "DELETE c FROM courses c WHERE c.course_code = :course_code")
    void deleteCourse(@Param("course_code") String courseCode);

    @Query(nativeQuery = true,
            value = "SELECT COUNT(course_code) FROM courses c where c.course_code = :course_code ")
    int checkCourse(@Param("course_code") int courseCode);

    @Query(nativeQuery = true,
        value = "SELECT room_code FROM courses c WHERE c.course_code = :course_code")
    int findRoomCodeByCourseCode(@Param("course_code") int courseCode);

    @Query(nativeQuery = true,
            value = "SELECT * FROM courses c WHERE c.can_enroll = true")
    List<CourseView> findCourseOpenEnrol();

    @Query(nativeQuery = true,
            value = "SELECT can_enroll FROM courses c WHERE c.course_code = :course_code")
    boolean canEnroll(@Param("course_code") int courseCode);

    @Query(nativeQuery = true,
            value = "SELECT * FROM courses c ORDER BY c.max_size DESC")
    List<Course> getAllSorted();
}
