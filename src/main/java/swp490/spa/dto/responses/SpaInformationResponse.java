package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaInformationResponse {
    @JsonProperty("count_employee")
    private Integer countEmployee;
    @JsonProperty("count_category")
    private Integer countCategory;
    @JsonProperty("count_package")
    private Integer countPackage;
    @JsonProperty("count_service")
    private Integer countService;
}
