package swp490.spa.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import swp490.spa.entities.Role;
import swp490.spa.entities.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationResponse {
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("type")
    private String type;
    @JsonProperty("data_id")
    private Integer data;
    @JsonProperty("message")
    private String message;
    @JsonIgnore
    @JsonProperty("role")
    private Role role;
    @JsonProperty("customer")
    private User user;
}
