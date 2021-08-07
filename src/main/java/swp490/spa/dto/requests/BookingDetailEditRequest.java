package swp490.spa.dto.requests;

import lombok.*;
import swp490.spa.entities.BookingDetail;
import swp490.spa.entities.SpaTreatment;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDetailEditRequest {
    private Integer bookingDetailId;
    private Integer spaTreatmentId;
    private String timeBooking;
    private String dateBooking;
    private Integer consultantId;
    private Integer staffId;
}
