package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.TreatmentService;
import swp490.spa.repositories.TreatmentServiceRepository;

@Service
public class TreatmentServiceService {
    @Autowired
    private TreatmentServiceRepository treatmentServiceRepository;

    public Page<TreatmentService> findBySpaTreatmentId(Integer spaTreatmentId, Pageable pageable){
        return this.treatmentServiceRepository.findBySpaTreatmentId(spaTreatmentId, pageable);
    }
}
