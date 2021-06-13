package swp490.spa.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "treatment_service", schema = "public")
public class TreatmentService implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "ordinal")
    private Integer ordinal;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "treatment_id")
    @JsonBackReference
    private SpaTreatment spaTreatment;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spa_service_id")
    @JsonIgnoreProperties(value = {"treatment_service", "hibernateLazyInitializer"})
    private SpaService spaService;
    public TreatmentService(SpaService spaService, Integer ordinal) {
        this.spaService = spaService;
        this.ordinal = ordinal;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof TreatmentService)) return false;
        TreatmentService that = (TreatmentService) object;
        return Objects.equals(spaTreatment.getName(), that.spaTreatment.getName()) &&
                Objects.equals(spaService.getName(), that.spaService.getName()) &&
                Objects.equals(ordinal, that.ordinal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spaTreatment.getName(), spaService.getName(), ordinal);
    }
}
