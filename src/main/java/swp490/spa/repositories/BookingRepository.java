package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
}
