package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.SpaTreatment;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TreatmentServiceResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ordinal")
    private Integer ordinal;
    @JsonProperty("spa_treatment")
    private SpaTreatment spaTreatment;
    @JsonProperty("spa_service")
    private SpaService spaService;
}
