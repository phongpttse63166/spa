package swp490.spa.dto.responses;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaInformationResponse {
    private Integer countEmployee;
    private Integer countCategory;
    private Integer countPackage;
    private Integer countService;
}
