package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.DateOff;
import swp490.spa.entities.StatusDateOff;

import java.sql.Date;
import java.util.List;

@Repository
public interface DateOffRepository extends JpaRepository<DateOff, Integer> {
    List<DateOff> findByDateOffAndAndSpa_IdAndAndStatusDateOff(Date dateOff,
                                                               Integer spaId,
                                                               StatusDateOff approve);
}
