package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import swp490.spa.entities.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpaServiceResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Status status;
    private String creatTime;
    private String createBy;
}
