package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("type")
    private String type;
    @JsonProperty("data_id")
    private Integer data;
    @JsonProperty("message")
    private String message;
    @JsonProperty("customer")
    private User user;
}
