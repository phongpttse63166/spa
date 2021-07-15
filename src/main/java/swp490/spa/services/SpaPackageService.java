package swp490.spa.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaPackage;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.Status;
import swp490.spa.repositories.SpaPackageRepository;
import swp490.spa.utils.support.templates.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SpaPackageService {
    @Autowired
    private SpaPackageRepository spaPackageRepository;

    public Page<SpaPackage> findSpaPackageByStatus(Status status, String search, Pageable pageable){
        return this.spaPackageRepository.findSpaPackageByStatus(status, search ,pageable);
    }

    public Page<SpaPackage> findAllStatusAvailable(Pageable pageable){
        return this.spaPackageRepository.findAllByStatusOrderById(Status.AVAILABLE, pageable);
    }

    public SpaPackage insertNewSpaPackage(SpaPackage spaPackage) {
        return this.spaPackageRepository.saveAndFlush(spaPackage);
    }

    public Page<SpaPackage> findAllBySpaServiceId(Integer spaServiceId, Integer page,
                                                  Integer size, String search){

        Pageable pageableDefault = PageRequest.of(Constant.PAGE_DEFAULT,Constant.SIZE_DEFAULT, Sort.by("name"));
        List<SpaPackage> spaPackages =
                this.spaPackageRepository.findSpaPackageByStatus(Status.AVAILABLE,
                        search, pageableDefault)
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
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        int start = (int) pageRequest.getOffset();
        int end = (start + pageRequest.getPageSize()) > result.size() ? result.size() : (start + pageRequest.getPageSize());
        Page<SpaPackage> pageToReturn = new PageImpl<>(result.subList(start, end), pageRequest, result.size());
        return pageToReturn;
    }

    public SpaPackage findBySpaPackageId(Integer spaPackageId) {
        return this.spaPackageRepository.findById(spaPackageId).get();
    }

    public Page<SpaPackage> findByCategoryIdOrderByDate(Integer categoryId, Pageable pageable) {
        return this.spaPackageRepository.findByCategory_IdOrderByCreateTimeDesc(categoryId, pageable);
    }

    public List<SpaPackage> findByCategoryId(Integer categoryId) {
        return this.spaPackageRepository.findByCategory_IdOrderById(categoryId);
    }

    public SpaPackage editBySpaPackageId(SpaPackage spaPackage) {
        return this.spaPackageRepository.save(spaPackage);
    }

    public boolean removeCategory(SpaPackage spaPackageResult) {
        if(Objects.nonNull(this.spaPackageRepository.save(spaPackageResult))){
            return true;
        }
        return false;
    }
}
