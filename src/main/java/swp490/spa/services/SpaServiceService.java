package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;
import swp490.spa.entities.Type;
import swp490.spa.repositories.SpaServiceRepository;

import java.util.Objects;

@Service
public class SpaServiceService {
    @Autowired
    private SpaServiceRepository spaServiceRepository;

    public Page<SpaService> findByStatus(Status status, String search, Pageable pageable){
        return this.spaServiceRepository.findByStatus(status, search, pageable);
    }

    public SpaService insertNewSpaService(SpaService spaService) {
        return this.spaServiceRepository.saveAndFlush(spaService);
    }

    public SpaService findById(Integer serviceId) {
        return this.spaServiceRepository.findById(serviceId).get();
    }

    public Page<SpaService> findByType(Type type, String search, Pageable pageable) {
        return this.spaServiceRepository
                .findByTypeAndNameContainingAndStatusOrderById(type, search, Status.AVAILABLE, pageable);
    }

    public SpaService editBySpaService(SpaService spaService) {
        return this.spaServiceRepository.save(spaService);
    }

    public SpaService findBySpaServiceId(Integer spaServiceId) {
        return this.spaServiceRepository.findById(spaServiceId).get();
    }

    public boolean removeService(SpaService spaService) {
        if(Objects.nonNull(this.spaServiceRepository.save(spaService))){
            return true;
        }
        return false;
    }
}
