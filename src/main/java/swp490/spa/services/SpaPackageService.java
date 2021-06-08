package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.Status;
import swp490.spa.repositories.SpaPackageRepository;

@Service
public class SpaPackageService {
    @Autowired
    private SpaPackageRepository spaPackageRepository;

    public Page<SpaPackage> findSpaPackageBySpaIdAndStatus(Integer spaId, Status status,
                                                           String search, Pageable pageable){
        return this.spaPackageRepository.findSpaPackageBySpaIdAndStatus(spaId, status, search ,pageable);
    }

    public Page<SpaPackage> findAllStatusAvailable(Pageable pageable){
        return this.spaPackageRepository.findAllByStatus(Status.AVAILABLE, pageable);
    }

    public Page<SpaPackage> findSpaPackageBySpaIdAndStatusAvailable(Integer spaId,
                                                                    String search, Pageable pageable){
        return this.spaPackageRepository
                .findSpaPackageBySpaIdAndStatus(spaId, Status.AVAILABLE, search ,pageable);
    }
}
