package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.Spa;
import swp490.spa.entities.Status;
import swp490.spa.repositories.SpaRepository;

@Service
public class SpaService {
    @Autowired
    private SpaRepository spaRepository;

    public Page<Spa> findAllSpaByStatusAvailable(Pageable pageable){
        return this.spaRepository.findByStatus(Status.AVAILABLE, pageable);
    }

    public Spa insertNewSpa(Spa spa){
        return this.spaRepository.save(spa);
    }

    public Spa findById(Integer spaId) {
        return this.spaRepository.findBySpaId(spaId);
    }

    public Page<Spa> findAllWithSearch(String search, Pageable pageable) {
        return this.spaRepository.findWithSearch(search,pageable);
    }

    public Spa editSpa(Spa spaEdit) {
        return this.spaRepository.save(spaEdit);
    }
}
