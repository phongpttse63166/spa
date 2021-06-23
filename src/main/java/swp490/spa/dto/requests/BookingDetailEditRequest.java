package swp490.spa.dto.requests;

import lombok.*;
import swp490.spa.entities.BookingDetail;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDetailEditRequest {
    private BookingDetail bookingDetail;
    private String timeBooking;
    private String dateBooking;
    private Integer consultantId;
}
