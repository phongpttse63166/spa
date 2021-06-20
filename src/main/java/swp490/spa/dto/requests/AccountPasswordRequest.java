package swp490.spa.dto.requests;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountPasswordRequest implements Serializable {
    private Integer id;
    private String password;
}
