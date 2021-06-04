package swp490.spa.dto.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {
    private String phone;
    private String password;
    private String role;
}
