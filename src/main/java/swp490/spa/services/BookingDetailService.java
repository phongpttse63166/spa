package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.BookingDetail;
import swp490.spa.repositories.BookingDetailRepository;

@Service
public class BookingDetailService {
    @Autowired
    BookingDetailRepository bookingDetailRepository;

    public BookingDetail insertBookingDetail(BookingDetail bookingDetail) {
        return this.bookingDetailRepository.save(bookingDetail);
    }
}
