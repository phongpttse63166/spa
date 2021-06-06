package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "customer", schema = "public")
public class Customer implements Serializable {
    @Id
    @Column(name = "user_id")
    private Integer id;
    @Column(name = "custom_type")
    private String customType;
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
