package swp490.spa.services;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import swp490.spa.entities.SpaTreatment;
import swp490.spa.entities.TreatmentService;
import swp490.spa.repositories.SpaTreatmentRepository;
import swp490.spa.utils.support.Constant;

import java.util.ArrayList;
import java.util.List;

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

    public SpaTreatment insertNewSpaTreatment(SpaTreatment spaTreatmentInsert) {
        return this.spaTreatmentRepository.save(spaTreatmentInsert);
    }

    public Page<SpaTreatment> findAllBySpaServiceId(Integer spaServiceId, Integer spaId,
                                                    Integer page, Integer size, String search) {
        Pageable pageableDefault =
                PageRequest.of(Constant.PAGE_DEFAULT,Constant.SIZE_DEFAULT, Sort.by("name"));
        List<SpaTreatment> spaTreatments =
                this.spaTreatmentRepository.findTreatmentBySpaId(spaId,search,pageableDefault).toList();
        List<SpaTreatment> result = new ArrayList<>();
        for (SpaTreatment spaTreatment: spaTreatments) {
            List<TreatmentService> treatmentServices = Lists.newArrayList(spaTreatment.getTreatmentServices());
            for (TreatmentService treatmentService : treatmentServices) {
                if(treatmentService.getSpaService().getId().equals(spaServiceId)){
                    result.add(spaTreatment);
                }
            }
        }
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("name"));
        int start = (int) pageRequest.getOffset();
        int end = (start + pageRequest.getPageSize()) > result.size() ? result.size() : (start + pageRequest.getPageSize());
        Page<SpaTreatment> pageToReturn = new PageImpl<>(result.subList(start, end), pageRequest, result.size());
        return pageToReturn;
    }

    public SpaTreatment findTreatmentBySpaPackageIdWithTypeOneStep(Integer spaPackageId) {
        return this.spaTreatmentRepository.findBySpaPackage_Id(spaPackageId);
    }

    public SpaTreatment editBySpaTreatment(SpaTreatment spaTreatment) {
        return this.spaTreatmentRepository.save(spaTreatment);
    }

    public SpaTreatment findByTreatmentId(Integer spaTreatmentId) {
        return this.spaTreatmentRepository.findById(spaTreatmentId).get();
    }

    public SpaTreatment findByPackageIdAndTypeOneStep(Integer spaPackageId) {
        return this.spaTreatmentRepository.findBySpaPackage_Id(spaPackageId);
    }
}
