package swp490.spa.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Getter
@Setter
@Table(name = "spa_package", schema = "public")
public class SpaPackage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "image")
    private String image;
    @Column(name = "type")
    private Type type;
    @Column(name = "total_slot")
    private Integer totalSlot;
    @Column(name = "status")
    private Status status;
    @Column(name = "create_time")
    private Date createTime;
    @Column(name = "create_by")
    private Integer create_by;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "spa_id")
    private Spa spa;
    @ManyToMany
    @JoinTable(
            name = "spapackage_spaservice",
            joinColumns = @JoinColumn(name = "spa_package_id"),
            inverseJoinColumns = @JoinColumn(name = "spa_service_id"))
    private Set<SpaService> spaServices = new HashSet<>();

    public void addListService(List<SpaService> spaServices) {
        this.spaServices.addAll(spaServices);
    }
}
