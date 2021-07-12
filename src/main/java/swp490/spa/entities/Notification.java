package swp490.spa.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Notification implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "type")
    private String type;
    @Column(name = "data_id")
    private Integer data;
    @Column(name = "message", length = 65535)
    private String message;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;
}
