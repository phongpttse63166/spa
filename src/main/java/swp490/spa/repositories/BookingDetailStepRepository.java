package swp490.spa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.BookingDetailStep;

import java.sql.Date;
import java.util.List;

@Repository
public interface BookingDetailStepRepository extends JpaRepository<BookingDetailStep, Integer> {
    List<BookingDetailStep> findByDateBookingOrderByStaffAscStartTimeAsc(Date dateBooking);
}
