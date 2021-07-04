package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.Booking;
import swp490.spa.entities.StatusBooking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Page<Booking> findByStatusBookingAndSpa_IdOrderByCreateTimeAsc(StatusBooking status, Integer spaId, Pageable pageable);

    List<Booking> findByCustomer_User_Id(Integer customerId);
}
