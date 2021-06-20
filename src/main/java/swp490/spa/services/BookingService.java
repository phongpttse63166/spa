package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Booking;
import swp490.spa.repositories.BookingRepository;

@Service
public class BookingService {
    @Autowired
    BookingRepository bookingRepository;

    public Booking insertNewBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
}
