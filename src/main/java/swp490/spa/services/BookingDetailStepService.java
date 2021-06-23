package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.repositories.BookingDetailStepRepository;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

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
}
