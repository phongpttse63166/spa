package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;
import swp490.spa.repositories.SpaServiceRepository;

@Service
public class SpaServiceService {
    @Autowired
    private SpaServiceRepository spaServiceRepository;

    public Page<SpaService> findBySpaIdAndStatus(Integer spaId, Status status, String search, Pageable pageable){
        return this.spaServiceRepository.findBySpaIdAndStatus(spaId, status, search, pageable);
    }

    public SpaService insertNewSpaService(SpaService spaService) {
        return this.spaServiceRepository.saveAndFlush(spaService);
    }

    public SpaService findBySpaId(Integer serviceId) {
        return this.spaServiceRepository.findBySpaId(serviceId);
    }
}
