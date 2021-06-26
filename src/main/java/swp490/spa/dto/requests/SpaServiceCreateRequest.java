package swp490.spa.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpaServiceCreateRequest {
    private String name;
    private String description;
    private Double price;
    private Status status;
    private Type type;
    private Integer durationMin;
    private Integer createBy;
    private MultipartFile file;
}
