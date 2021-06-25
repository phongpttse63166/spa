package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.Booking;
import swp490.spa.entities.BookingDetail;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingBookingDetailResponse {
    private Booking booking;
    private List<BookingDetail> bookingDetailList;
}
