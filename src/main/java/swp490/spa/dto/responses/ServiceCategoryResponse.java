package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ServiceCategoryResponse implements Serializable {
    private Integer id;
    private String name;
    private String image;
    private String description;
    private Double price;
    private Status status;
    private Type type;
    private Integer durationMin;
}
