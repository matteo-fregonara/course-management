package nl.tudelft.sem.template.repositories;

import java.util.List;
import nl.tudelft.sem.template.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query(nativeQuery = true,
            value = "SELECT * FROM user "
                    + "WHERE netID = :netID")
    List<User> findByUsername(@Param("netID") String netID);

    @Query(nativeQuery = true,
            value = "SELECT roles FROM user "
                    + "WHERE netID = :netID")
    String findRoleByUsername(@Param("netID") String netID);
}




