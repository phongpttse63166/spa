package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.BookingDetailStep;
import swp490.spa.entities.Customer;
import swp490.spa.entities.StatusRating;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RatingResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("rate")
    private Integer rate;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("create_time")
    private Date createTime;
    @JsonProperty("expired_time")
    private Date expireTime;
    @JsonProperty("status_rating")
    private StatusRating statusRating;
    @JsonProperty("customer")
    private Customer customer;
    @JsonProperty("booking_detail_step")
    @JsonIgnore
    private BookingDetailStep bookingDetailStep;
}
