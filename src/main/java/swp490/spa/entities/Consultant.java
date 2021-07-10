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
@Table(name = "consultant", schema = "public")
public class Consultant implements Serializable {
    @Id
    @Column(name = "user_id")
    private Integer id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
    @Column(name = "token_fcm", length = 65355)
    private String tokenFCM;
}
