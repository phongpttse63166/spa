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
@Table(name = "spa_treatment", schema = "public")
public class SpaTreatment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "create_by")
    private Integer createBy;
    @ManyToOne
    @JoinColumn(name = "spa_package_id")
    private SpaPackage spaPackage;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
}
