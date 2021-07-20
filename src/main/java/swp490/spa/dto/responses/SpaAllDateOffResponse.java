package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaAllDateOffResponse implements Serializable {
    @JsonProperty("total_staff")
    private Integer totalStaff;
    @JsonProperty("total_consultant")
    private Integer totalConsultant;
    @JsonProperty("date_offs")
    private List<SpaDateOffResponse> spaDateOffResponses;
}
