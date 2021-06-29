package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.repositories.BookingDetailStepRepository;

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
}
