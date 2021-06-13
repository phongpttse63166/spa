package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.SpaTreatment;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TreatmentServiceResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("ordinal")
    private Integer ordinal;
    @JsonProperty("service")
    private SpaService spaService;
    @JsonProperty("treatment")
    @JsonBackReference
    private SpaTreatment spaTreatment;
}
