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
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "modifier_time")
    private Date modifier_time;
}
