package swp490.spa.dto.model;

import lombok.*;
import swp490.spa.entities.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthRequest {
    private String phone;
    private String password;
    private Role role;
}
