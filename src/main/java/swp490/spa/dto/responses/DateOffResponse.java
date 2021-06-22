package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.Manager;
import swp490.spa.entities.Spa;
import swp490.spa.entities.StatusDateOff;
import swp490.spa.entities.User;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DateOffResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("date_off")
    private Date dateOff;
    @JsonProperty("status_date_off")
    private StatusDateOff statusDateOff;
    @JsonProperty("reason_date_off")
    private String reasonDateOff;
    @JsonProperty("reason_cancel")
    private String reasonCancel;
    @JsonProperty("manager")
    private Manager manager;
    @JsonProperty("employee")
    private User employee;
    @JsonProperty("spa")
    private Spa spa;
}
