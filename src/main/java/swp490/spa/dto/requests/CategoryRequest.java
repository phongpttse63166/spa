package swp490.spa.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryRequest {
    private String name;
    private String description;
    private Integer createBy;
    private MultipartFile file;
}
