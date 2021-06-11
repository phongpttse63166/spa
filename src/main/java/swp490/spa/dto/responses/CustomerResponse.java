package swp490.spa.dto.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import swp490.spa.entities.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CustomerResponse {
    @JsonProperty("user")
    private User user;
    @JsonProperty("custom_type")
    private String customType;
}
