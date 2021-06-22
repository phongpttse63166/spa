package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Booking;
import swp490.spa.entities.StatusBooking;
import swp490.spa.repositories.BookingRepository;

@Service
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;

    public Booking insertNewBooking(Booking booking) {
        return bookingRepository.save(booking);
    }

    public Page<Booking> findByBookingStatusAndSpa(StatusBooking statusBooking,
                                                   Integer spaId,
                                                   Integer customerId,
                                                   Pageable pageable) {
        return this.bookingRepository
                .findByStatusBookingAndSpa_IdAndCustomer_Id(statusBooking, spaId,
                        customerId, pageable);
    }
}
