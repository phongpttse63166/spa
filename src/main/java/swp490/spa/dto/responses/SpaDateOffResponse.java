package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.User;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaDateOffResponse implements Serializable {
    @JsonProperty("date")
    private Date date;
    @JsonProperty("staffs")
    private List<User> staffs;
    @JsonProperty("consultants")
    private List<User> consultants;
}
