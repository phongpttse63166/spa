package swp490.spa.dto.requests;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDetailStepRequest {
    private Integer bookingDetailStepId;
    private String timeBooking;
    private String dateBooking;
}
