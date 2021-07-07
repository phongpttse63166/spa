package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.BookingDetail;
import swp490.spa.entities.Type;
import swp490.spa.repositories.BookingDetailRepository;

import java.net.ContentHandler;
import java.util.List;

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

    public Page<BookingDetail> findByTypeMoreStepAndCustomerId(Type type,
                                                               Integer customerId,
                                                               Pageable pageable) {
        return this.bookingDetailRepository.
                findByTypeAndBooking_Customer_User_IdOrderByBookingAsc(type,customerId, pageable);
    }

    public List<BookingDetail> findByCustomer(Integer customerId) {
        return this.bookingDetailRepository.findByBooking_Customer_User_Id(customerId);
    }

    public BookingDetail findByBookingDetailId(Integer bookingDetailId) {
        return this.bookingDetailRepository.findById(bookingDetailId).get();
    }
}
