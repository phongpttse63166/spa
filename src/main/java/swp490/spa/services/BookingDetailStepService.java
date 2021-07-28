package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.entities.IsConsultation;
import swp490.spa.entities.Staff;
import swp490.spa.entities.StatusBooking;
import swp490.spa.repositories.BookingDetailStepRepository;

import java.net.ContentHandler;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingDetailStepService {
    @Autowired
    private BookingDetailStepRepository bookingDetailStepRepository;

    public List<BookingDetailStep> getAllByCurrentDateBooking(Date dateBooking) {
        return this.bookingDetailStepRepository.findByDateBookingOrderByStaffAscStartTimeAsc(dateBooking);
    }

    public BookingDetailStep insertBookingDetailStep(BookingDetailStep bookingDetailStep) {
        return this.bookingDetailStepRepository.save(bookingDetailStep);
    }

    public List<BookingDetailStep> findByStartTimeAndEndTimeAndDateBooking(Time startTime,
                                                                           Time endTime,
                                                                           Date dateBooking) {
        return this.bookingDetailStepRepository
                .findByStartTimeAndEndTimeAndDateBooking(startTime, endTime, dateBooking);
    }

    public Page<BookingDetailStep> findByBookingDetail(Integer bookingDetailId, Pageable pageable) {
        return this.bookingDetailStepRepository.findByBookingDetail_IdOrderById(bookingDetailId, pageable);
    }

    public void removeDB(Integer bookingDetailStepId) {
        this.bookingDetailStepRepository.deleteById(bookingDetailStepId);
    }

    public List<BookingDetailStep> findByDateBookingAndIsConsultationAndSpa(Date dateBooking,
                                                                            IsConsultation isConsultation,
                                                                            Integer spaId) {
        return this.bookingDetailStepRepository
                .findByDateBookingAndIsConsultationAndSpa(dateBooking, isConsultation, spaId);
    }

    public Page<BookingDetailStep> findByStaffIdAndDateBooking(Integer staffId,
                                                               Date dateBooking,
                                                               Pageable pageable) {
        return this.bookingDetailStepRepository
                .findByStaff_IdAndDateBookingOrderByStartTimeAsc(staffId, dateBooking, pageable);
    }

    public Page<BookingDetailStep> findByConsultantIdAndDateBooking(Integer consultantId,
                                                               Date dateBooking,
                                                               Pageable pageable) {
        return this.bookingDetailStepRepository
                .findByConsultant_IdAndDateBookingOrderByStartTimeAsc(consultantId, dateBooking, pageable);
    }

    public List<BookingDetailStep> findByStatusAndSpaId(StatusBooking statusBooking, Integer spaId) {
        return this.bookingDetailStepRepository.findByStatusAndSpa(statusBooking, spaId);
    }

    public List<BookingDetailStep> findByBookingDetailIdAndDateBooking(Integer bookingDetailId,
                                                                       Date dateBooking) {
        return this.bookingDetailStepRepository
                .findByBookingDetail_IdAndAndDateBookingOrderByStartTimeAsc(bookingDetailId,
                        dateBooking);
    }

    public List<BookingDetailStep> findByDateBookingAndStartEndTimeAndStaffId(Date dateBooking,
                                                                              Time startTime,
                                                                              Time endTime,
                                                                              Integer staffId){
        return this.bookingDetailStepRepository
                .findByDateBookingAndStartEndTimeAndStaffId(dateBooking,startTime,endTime,staffId);
    }

    public BookingDetailStep editBookingDetailStep(BookingDetailStep bookingDetailStep) {
        return this.bookingDetailStepRepository.save(bookingDetailStep);
    }

    public List<BookingDetailStep> findByDateBookingAndStartEndTimeAndConsultantId(Date dateBooking,
                                                                                   Time startTime,
                                                                                   Time endTime,
                                                                                   Integer consultantId) {
        return this.bookingDetailStepRepository
                .findByDateBookingAndStartEndTimeAndConsultantId(dateBooking,startTime,endTime,consultantId);
    }

    public List<BookingDetailStep> findByConsultantIdAndStatusBookingPendingBooking(Integer consultantId) {
        return this.bookingDetailStepRepository.findByConsultantAndStatusBooking(consultantId,
                StatusBooking.PENDING,StatusBooking.BOOKING);
    }

    public List<BookingDetailStep> findByDateBookingAndConsultant(Date dateBooking, Integer consultantId) {
        return this.bookingDetailStepRepository
                .findByDateBookingAndConsultant_User_IdOrderByStartTime(dateBooking, consultantId);
    }

    public List<BookingDetailStep> findByDateBookingAndStaff(Date dateBooking, Integer staffId) {
        return this.bookingDetailStepRepository
                .findByDateBookingAndStaff_User_Id(dateBooking, staffId);
    }

    public List<BookingDetailStep> findByDateBookingAndStartEndTimeAndSpa(Date dateBooking,
                                                                          Time startTime,
                                                                          Time endTime,
                                                                          Integer spaId) {
        return this.bookingDetailStepRepository
                .findByDateBookingAndStartEndTimeAndSpa(dateBooking,startTime,
                        endTime,spaId,IsConsultation.FALSE);
    }


    public List<BookingDetailStep> findBySpaAndStaffIsNull(Integer spaId) {
        return this.bookingDetailStepRepository.findBySpaAndStaffIsNull(spaId);
    }

    public BookingDetailStep findById(Integer bookingDetailStepId) {
        return this.bookingDetailStepRepository.findById(bookingDetailStepId).get();
    }

    public List<BookingDetailStep> findByStaff(Integer staffId) {
        return this.bookingDetailStepRepository.findByStaff_IdOrderByRatingDesc(staffId);
    }

    public List<BookingDetailStep> findAllByDateAndIsConsultation(IsConsultation isConsultation,
                                                                  StatusBooking statusBooking,
                                                                  Date dateBooking) {
        return this.bookingDetailStepRepository
                .findByIsConsultationAndStatusBookingAndDateBooking(isConsultation,
                        statusBooking, dateBooking);
    }
}
