package swp490.spa.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL)
    private Set<TreatmentService> treatmentServices;

    public SpaTreatment(String name, String description,
                        Date createTime , Integer createBy,
                        SpaPackage spaPackage, Spa spa,
                        List<TreatmentService> treatmentServices){
        this.name = name;
        this.description = description;
        this.createTime = createTime;
        this.createBy = createBy;
        this.spaPackage = spaPackage;
        this.spa = spa;
        for(TreatmentService treatmentService : treatmentServices) treatmentService.setSpaTreatment(this);
        this.treatmentServices = new HashSet<>(treatmentServices);
    }
}
