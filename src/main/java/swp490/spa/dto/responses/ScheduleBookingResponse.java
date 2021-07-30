package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.BookingDetailStep;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleBookingResponse implements Serializable {
    private Date dateBooking;
    private List<BookingDetailStep> bookingDetailSteps;
}
