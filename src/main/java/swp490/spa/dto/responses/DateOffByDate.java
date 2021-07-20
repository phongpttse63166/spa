package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.DateOff;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DateOffByDate implements Serializable {
    @JsonProperty("date_off")
    private Date dateOff;
    @JsonProperty("staff_date_offs")
    private List<DateOff> staffDateOffList;
    @JsonProperty("consultant_date_offs")
    private List<DateOff> consultantDateOffList;
}
