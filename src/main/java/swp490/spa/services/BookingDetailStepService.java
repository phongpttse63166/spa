package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.repositories.BookingDetailStepRepository;

@Service
public class BookingDetailStepService {
    @Autowired
    private BookingDetailStepRepository bookingDetailStepRepository;
}
