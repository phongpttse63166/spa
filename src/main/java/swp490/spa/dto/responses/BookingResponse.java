package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.Consultant;
import swp490.spa.entities.Customer;
import swp490.spa.entities.Spa;
import swp490.spa.entities.StatusBooking;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("total_price")
    private Double totalPrice;
    @JsonProperty("total_time")
    private Integer totalTime;
    @JsonProperty("status_booking")
    private StatusBooking statusBooking;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("customer")
    private Customer customer;
    @JsonProperty("spa")
    private Spa spa;
}
