package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaTreatment;
import swp490.spa.repositories.SpaTreatmentRepository;

@Service
public class SpaTreatmentService {
    @Autowired
    private SpaTreatmentRepository spaTreatmentRepository;

    public Page<SpaTreatment> findTreatmentBySpaId(Integer spaId, String search, Pageable pageable){
        return this.spaTreatmentRepository.findTreatmentBySpaId(spaId, search, pageable);
    }

    public Page<SpaTreatment> findByPackageId(Integer packageId, String search, Pageable pageable){
        return this.spaTreatmentRepository.findByPackageId(packageId, search, pageable);
    }
}
