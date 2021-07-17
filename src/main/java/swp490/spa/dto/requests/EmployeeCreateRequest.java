package swp490.spa.dto.requests;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.entities.Role;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EmployeeCreateRequest implements Serializable {
    private String fullname;
    private String phone;
    private String gender;
    private String birthdate;
    private String email;
    private MultipartFile file;
    private String address;
    private Role role;
    private Integer spaId;
}
