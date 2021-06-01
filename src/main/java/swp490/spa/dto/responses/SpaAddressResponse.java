package swp490.spa.dto.responses;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import swp490.spa.entities.Spa;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaAddressResponse {
    private Integer id;
    private String street;
    private String district;
    private String city;
    private Spa spa;
}
