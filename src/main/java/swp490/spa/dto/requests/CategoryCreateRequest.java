package swp490.spa.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryCreateRequest {
    private String name;
    private String icon;
    private String description;
    private Integer createBy;
    private Integer spaId;
    private MultipartFile file;
}
