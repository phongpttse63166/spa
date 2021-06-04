package swp490.spa.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "user_location", schema = "public")
public class UserLocation implements Serializable {
    @Id
    @Column(name = "user_id")
    private Integer id;
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "locationx")
    private String locationX;
    @Column(name = "locationy")
    private String locationY;
    @Column(name = "modifier_time")
    private Date modifier_time;
}
