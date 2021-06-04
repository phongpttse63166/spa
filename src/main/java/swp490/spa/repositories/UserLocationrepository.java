package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.UserLocation;

@Repository
public interface UserLocationrepository extends JpaRepository<UserLocation, Integer> {
    @Query("FROM UserLocation u WHERE u.id = ?1")
    UserLocation findUseLocationByUserId(Integer userId);


}
