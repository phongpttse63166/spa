package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.entities.IsConsultation;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface BookingDetailStepRepository extends JpaRepository<BookingDetailStep, Integer> {
    List<BookingDetailStep> findByDateBookingOrderByStaffAscStartTimeAsc(Date dateBooking);

    @Query("FROM BookingDetailStep b WHERE b.dateBooking = ?3 AND " +
            "((b.startTime < ?1 AND b.endTime > ?1) " +
            "OR (b.startTime < ?2 AND b.endTime > ?2) " +
            "OR (b.startTime > ?1 AND b.endTime < ?2))" +
            "ORDER BY b.staff.user.id ASC, b.consultant.user.id ASC ")
    List<BookingDetailStep> findByStartTimeAndEndTimeAndDateBooking(Time startTime,
                                                                    Time endTime,
                                                                    Date dateBooking);

    Page<BookingDetailStep> findByBookingDetail_IdOrderById(Integer bookingDetailId, Pageable pageable);

    Page<BookingDetailStep> findByDateBookingAndIsConsultationOrderByBookingDetailAscStartTimeAsc(Date dateBooking,
                                                                                                  IsConsultation isConsultation,
                                                                                                  Pageable pageable);


}
