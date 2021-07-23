package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swp490.spa.entities.User;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserLocationResponse {
    @JsonProperty("user")
    private User user;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("modifier_time")
    private Date modifier_time;
}
