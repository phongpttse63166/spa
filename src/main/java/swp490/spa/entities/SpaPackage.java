package swp490.spa.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.util.*;

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
    @JsonBackReference
    @ManyToMany(cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinTable(
            name = "spapackage_spaservice",
            joinColumns = @JoinColumn(name = "spa_package_id"),
            inverseJoinColumns = @JoinColumn(name = "spa_service_id"))
    private List<SpaService> spaServices = new ArrayList<>();

    public void addListService(List<SpaService> spaServices) {
        this.spaServices.addAll(spaServices);
    }
}
