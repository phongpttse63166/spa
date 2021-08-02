package swp490.spa.dto.responses;

import lombok.*;
import swp490.spa.entities.Category;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryPackageTreatmentServiceResponse implements Serializable {
    private Category category;
    private List<PackageResponse> packages;

    public static class PackageResponse {
        private Integer id;
        private String name;
        private String description;
        private String image;
        private Type type;
        private Status status;
        private List<TreatmentResponse> treatments;

        public PackageResponse() {
        }

        public PackageResponse(Integer id, String name, String description, String image,
                               Type type, Status status, List<TreatmentResponse> treatments) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.image = image;
            this.type = type;
            this.status = status;
            this.treatments = treatments;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public List<TreatmentResponse> getTreatments() {
            return treatments;
        }

        public void setTreatments(List<TreatmentResponse> treatments) {
            this.treatments = treatments;
        }
    }

    public static class TreatmentResponse {
        private Integer id;
        private String name;
        private String description;
        private Double totalPrice;
        private Integer totalTime;
        private List<SpaService> spaServices;

        public TreatmentResponse() {
        }

        public TreatmentResponse(Integer id, String name, String description,
                                 Double totalPrice, Integer totalTime, List<SpaService> spaServices) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.totalPrice = totalPrice;
            this.totalTime = totalTime;
            this.spaServices = spaServices;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Double getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(Double totalPrice) {
            this.totalPrice = totalPrice;
        }

        public Integer getTotalTime() {
            return totalTime;
        }

        public void setTotalTime(Integer totalTime) {
            this.totalTime = totalTime;
        }

        public List<SpaService> getSpaServices() {
            return spaServices;
        }

        public void setSpaServices(List<SpaService> spaServices) {
            this.spaServices = spaServices;
        }
    }
}
