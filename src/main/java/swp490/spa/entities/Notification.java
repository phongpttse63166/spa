package swp490.spa.entities;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification {
    private String title;
    private String message;
    private Map<String,String> data;
}
