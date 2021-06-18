package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminResponse {
    @JsonProperty("user")
    private User user;
}
