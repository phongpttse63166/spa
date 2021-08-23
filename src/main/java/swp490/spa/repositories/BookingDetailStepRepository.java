package swp490.spa.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.entities.IsConsultation;
import swp490.spa.entities.Staff;
import swp490.spa.entities.StatusBooking;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Repository
public interface BookingDetailStepRepository extends JpaRepository<BookingDetailStep, Integer> {
    List<BookingDetailStep> findByDateBookingOrderByStaffAscStartTimeAsc(Date dateBooking);

    @Query("FROM BookingDetailStep b WHERE b.dateBooking = ?3 AND " +
            "((b.startTime <= ?1 AND b.endTime >= ?1) " +
            "OR (b.startTime <= ?2 AND b.endTime >= ?2) " +
            "OR (b.startTime >= ?1 AND b.endTime <= ?2) " +
            "OR (b.startTime <= ?1 AND b.endTime >= ?2))" +
            "ORDER BY b.staff.user.id ASC, b.consultant.user.id ASC ")
    List<BookingDetailStep> findByStartTimeAndEndTimeAndDateBooking(Time startTime,
                                                                    Time endTime,
                                                                    Date dateBooking);

    Page<BookingDetailStep> findByBookingDetail_IdOrderById(Integer bookingDetailId, Pageable pageable);

    @Query("FROM BookingDetailStep b WHERE b.dateBooking = ?1 AND b.isConsultation = ?2 " +
            "AND b.bookingDetail.booking.spa.id = ?3 " +
            "ORDER BY b.bookingDetail.id ASC, b.startTime ASC")
    List<BookingDetailStep> findByDateBookingAndIsConsultationAndSpa(Date dateBooking,
                                                                     IsConsultation isConsultation,
                                                                     Integer spaId);

    Page<BookingDetailStep> findByStaff_IdAndDateBookingOrderByStartTimeAsc(Integer staffId,
                                                                            Date dateBooking,
                                                                            Pageable pageable);

    Page<BookingDetailStep> findByConsultant_IdAndDateBookingOrderByStartTimeAsc(Integer staffId,
                                                                                 Date dateBooking,
                                                                                 Pageable pageable);

    @Query("FROM BookingDetailStep s WHERE s.statusBooking = ?1 AND s.bookingDetail.booking.spa.id = ?2 " +
            "ORDER BY s.dateBooking ASC, s.startTime ASC")
    List<BookingDetailStep> findByStatusAndSpa(StatusBooking status, Integer spaId);

    List<BookingDetailStep> findByBookingDetail_IdAndAndDateBookingOrderByStartTimeAsc(Integer bookingDetailId,
                                                                                       Date dateBooking);

    @Query("FROM BookingDetailStep b WHERE b.dateBooking = ?1 AND " +
            "((b.startTime <= ?2 AND b.endTime >= ?2) " +
            "OR (b.startTime <= ?3 AND b.endTime >= ?3) " +
            "OR (b.startTime >= ?2 AND b.endTime <= ?3) " +
            "OR (b.startTime <= ?2 AND b.endTime >= ?3)) AND b.staff.user.id = ?4 " +
            "ORDER BY b.consultant.user.id ASC")
    List<BookingDetailStep> findByDateBookingAndStartEndTimeAndStaffId(Date dateBooking, Time startTime,
                                                                       Time endTime, Integer staffId);

    @Query("FROM BookingDetailStep b WHERE b.dateBooking = ?1 AND " +
            "((b.startTime <= ?2 AND b.endTime >= ?2) " +
            "OR (b.startTime <= ?3 AND b.endTime >= ?3) " +
            "OR (b.startTime >= ?2 AND b.endTime <= ?3) " +
            "OR (b.startTime <= ?2 AND b.endTime >= ?3)) AND b.consultant.user.id = ?4 " +
            "ORDER BY b.consultant.user.id ASC")
    List<BookingDetailStep> findByDateBookingAndStartEndTimeAndConsultantId(Date dateBooking,
                                                                            Time startTime,
                                                                            Time endTime,
                                                                            Integer consultantId);

    @Query("FROM BookingDetailStep b WHERE b.consultant.user.id = ?1 " +
            "AND (b.statusBooking = ?2 OR b.statusBooking = ?3 OR b.statusBooking = ?4) ORDER BY b.bookingDetail.id ASC")
    List<BookingDetailStep> findByConsultantAndStatusBooking(Integer consultantId,
                                                             StatusBooking status1,
                                                             StatusBooking status2,
                                                             StatusBooking status3);

    List<BookingDetailStep> findByDateBookingAndConsultant_User_IdOrderByStartTime(Date dateBooking,
                                                                                   Integer consultantId);

    List<BookingDetailStep> findByDateBookingAndStaff_User_Id(Date dateBooking, Integer staffId);

    @Query("FROM BookingDetailStep b WHERE b.dateBooking = ?1 AND " +
            "((b.startTime <= ?2 AND b.endTime >= ?2) " +
            "OR (b.startTime >= ?2 AND b.endTime <= ?3) " +
            "OR (b.startTime <= ?2 AND b.endTime >= ?3)) AND " +
            "b.bookingDetail.booking.spa.id = ?4 AND b.isConsultation = ?5 ORDER BY b.id")
    List<BookingDetailStep> findByDateBookingAndStartEndTimeAndSpa(Date dateBooking, Time startTime,
                                                                   Time endTime, Integer spaId,
                                                                   IsConsultation value);

    @Query("FROM BookingDetailStep b WHERE b.bookingDetail.booking.spa.id = ?1 " +
            "AND b.staff.id IS NULL AND b.isConsultation = ?2 " +
            "AND b.bookingDetail.type = 1 " +
            "ORDER BY b.bookingDetail.id ASC")
    List<BookingDetailStep> findBySpaAndStaffIsNullAndIsConsultation(Integer spaId,
                                                                     IsConsultation isConsultation);

    List<BookingDetailStep> findByStaff_IdOrderByRatingDesc(Integer staffId);

    List<BookingDetailStep> findByIsConsultationAndStatusBookingAndDateBooking(IsConsultation isConsultation,
                                                                               StatusBooking status,
                                                                               Date dateBooking);
    @Query("FROM BookingDetailStep b WHERE b.bookingDetail.booking.customer.user.id = ?1 " +
            "AND b.dateBooking BETWEEN ?2 AND ?3 ORDER BY b.dateBooking")
    List<BookingDetailStep> findByCustomerAndFromToDateBooking(Integer customerId,
                                                               Date startDate,
                                                               Date endDate);

    List<BookingDetailStep> findByStaff_IdAndAndIsConsultationAndStatusBooking(Integer staffId,
                                                                               IsConsultation isConsultation,
                                                                               StatusBooking status);

    @Query("FROM BookingDetailStep b WHERE b.statusBooking = ?1 AND " +
            "b.bookingDetail.booking.spa.id = ?2 AND b.isConsultation = ?3 " +
            "AND b.dateBooking BETWEEN ?4 AND ?5")
    List<BookingDetailStep> findByStatusBookingAndSpaIdAndIsConsultationAndFromToDate(StatusBooking status,
                                                                                      Integer spaId,
                                                                                      IsConsultation isConsultation,
                                                                                      Date firstDate,
                                                                                      Date lastDate);

    @Query("FROM BookingDetailStep b WHERE b.bookingDetail.booking.spa.id = ?1 " +
            "AND NOT(b.rating.id IS NULL)")
    List<BookingDetailStep> findBySpaAndRatingNotNull(Integer spaId);
}
