package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<BookingDetail> findByBooking(Integer bookingId, Pageable pageable) {
        return this.bookingDetailRepository.findByBooking_Id(bookingId,pageable);
    }

    public BookingDetail editBookingDetail(BookingDetail bookingDetail) {
        return this.bookingDetailRepository.save(bookingDetail);
    }

    public void removeDB(Integer bookingDetailId) {
        this.bookingDetailRepository.deleteById(bookingDetailId);
    }
}
