package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.DateOff;

@Repository
public interface DateOffRepository extends JpaRepository<DateOff, Integer> {
}
