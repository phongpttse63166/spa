package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.SpaPackageCreateRequest;
import swp490.spa.dto.requests.SpaTreatmentCreateRequest;
import swp490.spa.dto.responses.SpaPackageTreatmentResponse;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.entities.SpaService;
import swp490.spa.services.*;
import swp490.spa.utils.support.Constant;
import swp490.spa.utils.support.Notification;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequestMapping("/api/manager")
@RestController
@CrossOrigin
public class ManagerController {
    @Autowired
    private ManagerService managerService;
    @Autowired
    private SpaServiceService spaServiceService;
    @Autowired
    private SpaPackageService spaPackageService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private swp490.spa.services.SpaService spaService;
    @Autowired
    private CategoryService categoryService;
    private Conversion conversion;

    public ManagerController(ManagerService managerService, SpaServiceService spaServiceService,
                             SpaPackageService spaPackageService, SpaTreatmentService spaTreatmentService,
                             swp490.spa.services.SpaService spaService){
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
        this.spaPackageService = spaPackageService;
        this.spaTreatmentService = spaTreatmentService;
        this.spaService = spaService;
        this.conversion = new Conversion();
    }

    @GetMapping("/search/{userId}")
    public Response findManagerById(@PathVariable Integer userId){
        Manager manager = managerService.findManagerById(userId);
        return ResponseHelper.ok(manager);
    }

    @PutMapping("/spaservice/create")
    public Response createNewSpaService(@RequestBody SpaService spaService){
        Manager manager = managerService.findManagerById(Integer.parseInt(spaService.getCreateBy()));
        if(Objects.isNull(manager)){
            return ResponseHelper.error(Notification.MANAGER_NOT_EXISTED);
        }
        Spa spa = manager.getSpa();
        spaService.setSpa(spa);
        spaService.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
        SpaService serviceResult = spaServiceService.insertNewSpaService(spaService);
        if(Objects.nonNull(serviceResult)){
            return ResponseHelper.ok(serviceResult);
        }
        return ResponseHelper.error(Notification.SERVICE_CREATE_FAIL);
    }

    @GetMapping("/spapackage/findbyspaid")
    public Response getSpaPackageBySpaId(@RequestParam Integer spaId,
                                         @RequestParam String search ,
                                         Pageable pageable){
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageBySpaIdAndStatusAvailable(spaId, search ,pageable);
        if(!spaPackages.hasContent() && !spaPackages.isFirst()){
            spaPackages = spaPackageService
                    .findSpaPackageBySpaIdAndStatusAvailable(spaId, search,
                            PageRequest.of(spaPackages.getTotalPages()-1,
                            spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @GetMapping("/spatreatment/findbypackageId")
    public Response findSpaTreatmentByPackageId(@RequestParam Integer packageId,
                                            @RequestParam String search, Pageable pageable){
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(packageId, search, pageable);
        if(!spaTreatments.hasContent() && !spaTreatments.isFirst()){
            spaTreatments = spaTreatmentService.findByPackageId(packageId, search,
                    PageRequest.of(spaTreatments.getTotalPages()-1, spaTreatments.getSize(), spaTreatments.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spaservice/findbyspaId")
    public Response findSpaServiceBySpaId(@RequestParam Integer spaId, @RequestParam Status status,
                                          @RequestParam String search, Pageable pageable){
        Page<swp490.spa.entities.SpaService> spaServices =
                spaServiceService.findBySpaIdAndStatus(spaId, status, search, pageable);
        if(!spaServices.hasContent() && !spaServices.isFirst()){
            spaServices = spaServiceService.findBySpaIdAndStatus(spaId, status, search,
                    PageRequest.of(spaServices.getTotalPages()-1, spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @PutMapping("/spapackageservices/insert")
    public Response insertNewSpaPackageWithServices(@RequestBody SpaPackageCreateRequest spaPackage){
        List<SpaService> spaServices = new ArrayList<>();
        Category category = categoryService.findById(spaPackage.getCategoryId());
        if(Objects.isNull(category)){
            return ResponseHelper.error(Notification.CATEGORY_NOT_EXISTED);
        }
        Spa spa = spaService.findById(spaPackage.getSpaId());
        if(Objects.isNull(spa)){
            return ResponseHelper.error(Notification.SPA_NOT_EXISTED);
        }
        SpaPackage spaPackageInsert = new SpaPackage();
        spaPackageInsert.setName(spaPackage.getName());
        spaPackageInsert.setSpa(spa);
        spaPackageInsert.setCategory(category);
        spaPackageInsert.setCreate_by(spaPackage.getCreateBy());
        spaPackageInsert.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
        spaPackageInsert.setDescription(spaPackage.getDescription());
        spaPackageInsert.setImage(spaPackage.getImage());
        spaPackageInsert.setStatus(spaPackage.getStatus());
        SpaPackage spaPackageResult = spaPackageService.insertNewSpaPackage(spaPackageInsert);
        if(Objects.isNull(spaPackageResult)){
            return ResponseHelper.error(Notification.SPA_PACKAGE_CREATE_FAIL);
        }
        for (Integer serviceId : spaPackage.getListSpaServiceId()) {
            swp490.spa.entities.SpaService spaService = spaServiceService.findBySpaId(serviceId);
            if(Objects.isNull(spaService)){
                ResponseHelper.error(Notification.SPA_SERVICE_NOT_EXISTED);
            }
            spaServices.add(spaService);
        }
        spaPackageResult.addListService(spaServices);
        if(Objects.nonNull(spaPackageService.insertNewSpaPackage(spaPackageResult))){
            return ResponseHelper.ok(Notification.SPA_PACKAGE_SERVICE_INSERT_SUCCESS);
        }
        return ResponseHelper.error(Notification.SPA_PACKAGE_SERVICE_INSERT_FAIL);
    }

    @GetMapping("/spapackage/findbyserviceId")
    public Response findSpaPackageBySpaServiceId(@RequestParam Integer spaServiceId ,
                                                 @RequestParam Integer spaId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer size,
                                                 @RequestParam String search){
        Page<SpaPackage> spaPackages =
                spaPackageService.findAllBySpaServiceId(spaServiceId, spaId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @PutMapping("/spatreatmentservices/insert")
    public Response insertNewSpaPackageWithServices(@RequestBody SpaTreatmentCreateRequest spaTreatmentRequest){
        List<TreatmentService> treatmentServices = new ArrayList<>();
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaTreatmentRequest.getPackageId());
        if(Objects.isNull(spaPackage)){
            return ResponseHelper.error(Notification.SPA_PACKAGE_NOT_EXISTED);
        }
        Spa spa = spaService.findById(spaTreatmentRequest.getSpaId());
        if(Objects.isNull(spa)){
            return ResponseHelper.error(Notification.SPA_NOT_EXISTED);
        }
        int ordinal = 1;
        for (int i = 0; i < spaTreatmentRequest.getListSpaServiceId().size(); i++) {
            swp490.spa.entities.SpaService spaService =
                    spaServiceService.findBySpaId(spaTreatmentRequest.getListSpaServiceId().get(i));
            if(Objects.isNull(spaService)){
                ResponseHelper.error(Notification.SPA_SERVICE_NOT_EXISTED);
            }
            TreatmentService treatmentService = new TreatmentService(spaService,ordinal);
            ordinal++;
            treatmentServices.add(treatmentService);
        }
        SpaTreatment spaTreatmentInsert = new SpaTreatment(spaTreatmentRequest.getName(),
                        spaTreatmentRequest.getDescription(),
                        Date.valueOf(LocalDateTime.now().toLocalDate()),
                        spaTreatmentRequest.getCreateBy(),
                        spaPackage, spa, treatmentServices);
        if(Objects.nonNull(spaTreatmentService.insertNewSpaTreatment(spaTreatmentInsert))){
            return ResponseHelper.ok(Notification.SPA_TREATMENT_SERVICE_INSERT_SUCCESS);
        }
        return ResponseHelper.error(Notification.SPA_TREATMENT_SERVICE_INSERT_FAIL);
    }

    @GetMapping("/spatreatment/findbyserviceId")
    public Response findSpaTreatmentBySpaServiceId(@RequestParam Integer spaServiceId,
                                                   @RequestParam Integer spaId,
                                                   @RequestParam Integer page,
                                                   @RequestParam Integer size,
                                                   @RequestParam String search){
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findAllBySpaServiceId(spaServiceId, spaId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spapackagetreatment/findbyspaId")
    public Response findSpaPackageTreatmentBySpaId(@RequestParam Integer spaId,
                                                   @RequestParam String search,
                                                   Pageable pageable){
        boolean isNotFirst = false;
        List<SpaPackageTreatmentResponse> result = new ArrayList<>();
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageBySpaIdAndStatusAvailable(spaId, search ,pageable);
        int totalItem = spaPackages.getContent().size();
        if(!spaPackages.hasContent() && !spaPackages.isFirst()){
            spaPackages = spaPackageService
                    .findSpaPackageBySpaIdAndStatusAvailable(spaId, search,
                            PageRequest.of(spaPackages.getTotalPages()-1,
                                    spaPackages.getSize(), spaPackages.getSort()));
            isNotFirst = true;
        }
        if(spaPackages.getContent().size()!=0 && !spaPackages.getContent().isEmpty()){
            for (SpaPackage spaPackage : spaPackages.getContent()) {
                List<SpaTreatment> spaTreatments = new ArrayList<>();
                SpaPackageTreatmentResponse sptr = new SpaPackageTreatmentResponse();
                sptr.setSpaPackage(spaPackage);
                spaTreatments =
                        spaTreatmentService.findByPackageId(spaPackage.getId(),
                                Constant.SEARCH_NO_CONTENT,
                                PageRequest.of(Constant.PAGE_DEFAULT,Constant.SIZE_DEFAULT)).toList();
                sptr.setSpaTreatments(spaTreatments);
                result.add(sptr);
            }
        }
        Page<SpaPackageTreatmentResponse> page = null;
        if(isNotFirst){
             page = new PageImpl<>(result,
                    PageRequest.of(spaPackages.getTotalPages()-1,
                            spaPackages.getSize(), spaPackages.getSort()),totalItem);
        }
        page = new PageImpl<>(result,pageable,totalItem);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageTreatmentResponse(page));
    }
}
