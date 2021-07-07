package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Booking;
import swp490.spa.entities.StatusBooking;
import swp490.spa.repositories.BookingRepository;

import java.util.List;

@Service
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;

    public Booking insertNewBooking(Booking booking) {
        return bookingRepository.saveAndFlush(booking);
    }

    public Page<Booking> findByBookingStatusAndSpa(StatusBooking statusBooking,
                                                   Integer spaId,
                                                   Pageable pageable) {
        return this.bookingRepository
                .findByStatusBookingAndSpa_IdOrderByCreateTimeAsc(statusBooking, spaId, pageable);
    }

    public Booking findByBookingId(Integer bookingId) {
        return this.bookingRepository.findById(bookingId).get();
    }

    public Booking editBooking(Booking booking) {
        return this.bookingRepository.save(booking);
    }

    public void removeDB(Integer bookingId) {
        this.bookingRepository.deleteById(bookingId);
    }

    public List<Booking> findByCustomerId(Integer customerId) {
        return this.bookingRepository.findByCustomer_User_Id(customerId);
    }
}
