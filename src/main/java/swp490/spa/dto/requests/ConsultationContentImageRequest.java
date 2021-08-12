package swp490.spa.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConsultationContentImageRequest {
    private Integer consultationContentId;
    private MultipartFile file;
}
