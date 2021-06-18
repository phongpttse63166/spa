package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Apointment;

@Repository
public interface ApointmentRepository extends JpaRepository<Apointment, Integer> {
}
