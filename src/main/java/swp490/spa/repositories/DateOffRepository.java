package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<DateOff> findBySpa_IdAndStatusDateOffAndDateOffBetweenOrderByDateOff(Integer spaId,
                                                                              StatusDateOff status,
                                                                              Date fromDate,
                                                                              Date toDate,
                                                                              Pageable pageable);

    List<DateOff> findBySpa_IdAndDateOffBetweenAndStatusDateOffOrderByDateOffAsc(Integer spaId,
                                                                                 Date startDate,
                                                                                 Date endDate,
                                                                                 StatusDateOff status);

    DateOff findByEmployee_IdAndDateOff(Integer employeeId, Date dateOff);

    List<DateOff> findByDateOff(Date dateOff);
}
