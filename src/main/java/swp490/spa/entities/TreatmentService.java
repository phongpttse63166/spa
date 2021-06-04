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
@Table(name = "treatment_service", schema = "public")
public class TreatmentService implements Serializable {
    @Id
    @Column(name = "id")
    private Integer id;
    @Column(name = "ordinal")
    private Integer ordinal;
    @ManyToOne
    @JoinColumn(name = "treatment_id")
    private SpaTreatment spaTreatment;
    @ManyToOne
    @JoinColumn(name = "spa_service_id")
    private SpaService spaService;
}
