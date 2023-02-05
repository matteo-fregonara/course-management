package nl.tudelft.sem.template.repositories;

import java.util.List;
import nl.tudelft.sem.template.entities.Course;
import nl.tudelft.sem.template.entities.Student;
import nl.tudelft.sem.template.entities.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
//Duplicate literals suppressed as identical strings were used multiple times in parameter names
public interface TeacherRepository  extends JpaRepository<Teacher, Integer> {


    // get all course_code teacher takes part in by netID
    @Query(nativeQuery = true,
            value = "SELECT course_code FROM teachers_give_course "
                    + "WHERE netID = :netID")
    List<Integer> findByNetID(@Param("netID") String netID);


    // get all students by course
    List<Teacher> findAllByCourseCode(@Param("courseCode") int courseCode);

    // get number of teachers by course
    Integer countAllByCourseCode(@Param("courseCode") int courseCode);



    @Query(nativeQuery = true,
            value = "SELECT COUNT(netID)>0 FROM teachers_give_course s "
                    + "WHERE s.netID = :netID AND s.course_code = :courseCode")
    Integer existsByNetIDAndCourseCode(@Param("netID") String netID,
                                        @Param("courseCode") Integer courseCode);

    // removes a teacher given his/her netID and the course code
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
            value = "DELETE FROM teachers_give_course "
                    + "WHERE netID = :netID AND course_code = :courseCode")
    void deleteByNetIDAndCourseCode(@Param("netID") String netID,
                                       @Param("courseCode") Integer courseCode);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(nativeQuery = true,
        value = "DELETE FROM teachers_give_course WHERE course_code = :course_code")
    void deleteAllByCourseCode(@Param("course_code") String courseCode);
}

