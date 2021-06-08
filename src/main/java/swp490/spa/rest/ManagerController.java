package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.ManagerService;
import swp490.spa.services.SpaPackageService;
import swp490.spa.services.SpaServiceService;
import swp490.spa.services.SpaTreatmentService;
import swp490.spa.utils.support.Notification;

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
    private Conversion conversion;

    public ManagerController(ManagerService managerService, SpaServiceService spaServiceService,
                             SpaPackageService spaPackageService, SpaTreatmentService spaTreatmentService){
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
        this.spaPackageService = spaPackageService;
        this.spaTreatmentService = spaTreatmentService;
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
        return ResponseHelper.ok(conversion.convertToSpaPackageResponse(spaPackages));
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
        return ResponseHelper.ok(conversion.convertToSpaTreatmentResponse(spaTreatments));
    }
}
