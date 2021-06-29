package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import swp490.spa.entities.TreatmentService;
import swp490.spa.repositories.TreatmentServiceRepository;

@Service
public class TreatmentServiceService {
    @Autowired
    TreatmentServiceRepository treatmentServiceRepository;

    public TreatmentService insertNewTreatmentService(TreatmentService treatmentService) {
        return this.treatmentServiceRepository.save(treatmentService);
    }
}
