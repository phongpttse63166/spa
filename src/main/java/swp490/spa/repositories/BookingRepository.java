package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Booking;
import swp490.spa.entities.StatusBooking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByStatusBookingAndSpa_Id(StatusBooking status, Integer spaId, Pageable pageable);
}
