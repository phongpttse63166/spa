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
@Table(name = "spa_service", schema = "public")
public class SpaService implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private Double price;
    @Column(name = "status")
    private Status status;
    @Column(name = "create_time")
    private String creatTime;
    @Column(name = "create_by")
    private String createBy;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
}
