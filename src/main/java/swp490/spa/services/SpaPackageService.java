package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;
import swp490.spa.repositories.SpaPackageRepository;

import java.util.ArrayList;
import java.util.List;

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

    public SpaPackage insertNewSpaPackage(SpaPackage spaPackage) {
        return this.spaPackageRepository.saveAndFlush(spaPackage);
    }

    public Page<SpaPackage> findAllBySpaServiceId(Integer spaServiceId, Integer spaId , Pageable pageable){
        List<SpaPackage> spaPackages =
                this.spaPackageRepository.findSpaPackageBySpaIdAndStatus(spaId, Status.AVAILABLE,
                        "", pageable)
                        .toList();
        List<SpaPackage> result = new ArrayList<>();
        for (SpaPackage spaPackage : spaPackages) {
            List<SpaService> spaServices = spaPackage.getSpaServices();
            for (SpaService spaService: spaServices) {
                if(spaService.getId().equals(spaServiceId)){
                    result.add(spaPackage);
                }
            }
        }
        Page<SpaPackage> page = new PageImpl<>(result);
        return page;
    }

    public Page<SpaPackage> findSpaServiceBySpaPackageId(Integer spaPackageId, Integer spaId ,
                                                         Pageable pageable) {
        List<SpaPackage> spaPackages =
                this.spaPackageRepository.findSpaPackageBySpaIdAndStatus(spaId, Status.AVAILABLE,
                        "", pageable)
                        .toList();
        List<SpaPackage> result = new ArrayList<>();
        for (SpaPackage spaPackage : spaPackages) {
            if (spaPackage.getId().equals(spaPackageId)){
                result.add(spaPackage);
            }
        }
        Page<SpaPackage> page = new PageImpl<>(result);
        return page;
    }
}
