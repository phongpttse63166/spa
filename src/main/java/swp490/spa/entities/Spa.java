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
@Table(name = "spa")
public class Spa implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Column(name = "image")
    private String image;
    @Column(name = "spaAddress")
    private SpaAddress spaAddress;
    @Column(name = "createBy")
    private User createBy;
    @Column(name = "createTime")
    private String createTime;
    @Column(name = "modifiedBy")
    private User modifiedBy;
    @Column(name = "lastModified")
    private String lastModified;
    @Column(name = "status")
    private String status;
}
