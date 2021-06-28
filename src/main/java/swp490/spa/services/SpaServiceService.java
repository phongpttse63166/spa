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

    public Page<SpaService> findBySpaIdAndStatus(Integer spaId, Status status, String search, Pageable pageable){
        return this.spaServiceRepository.findBySpaIdAndStatus(spaId, status, search, pageable);
    }

    public SpaService insertNewSpaService(SpaService spaService) {
        return this.spaServiceRepository.saveAndFlush(spaService);
    }

    public SpaService findBySpaId(Integer serviceId) {
        return this.spaServiceRepository.findBySpaId(serviceId);
    }

    public Page<SpaService> findBySpaIdAndType(Integer spaId, Type type, String search, Pageable pageable) {
        return this.spaServiceRepository
                .findBySpa_IdAndTypeAndNameContainingAndStatusOrderById(spaId, type,
                        search, Status.AVAILABLE, pageable);
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
