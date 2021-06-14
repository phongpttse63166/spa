package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.SpaTreatment;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaPackageTreatmentResponse {
    @JsonProperty
    private SpaPackage spaPackage;
    @JsonProperty
    private List<SpaTreatment> spaTreatments;
}
