package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.*;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDetailStepResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("date_booking")
    private Date dateBooking;
    @JsonProperty("start_time")
    private Time startTime;
    @JsonProperty("end_time")
    private Time endTime;
    @JsonProperty("status_booking")
    private StatusBooking statusBooking;
    @JsonProperty("reason_cancel")
    private String reasonCancel;
    @JsonProperty("treatment_service")
    private TreatmentService treatmentService;
    @JsonProperty("staff")
    private Staff staff;
    @JsonProperty("consultant")
    private Consultant consultant;
    @JsonProperty("booking_detail")
    private BookingDetail bookingDetail;
}
