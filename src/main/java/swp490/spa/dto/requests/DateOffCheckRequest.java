package swp490.spa.dto.requests;

import lombok.*;
import swp490.spa.entities.Role;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DateOffCheckRequest implements Serializable {
    private Integer dateOffId;
    private Integer managerId;
    private String reasonCancel;
    private Role role;
}
