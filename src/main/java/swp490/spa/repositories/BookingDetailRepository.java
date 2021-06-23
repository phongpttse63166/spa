package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.BookingDetail;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {
    Page<BookingDetail> findByBooking_Id(Integer bookingId, Pageable pageable);
}
