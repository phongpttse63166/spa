package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Manager;
import swp490.spa.entities.Spa;
import swp490.spa.entities.SpaService;
import swp490.spa.entities.User;
import swp490.spa.services.ManagerService;
import swp490.spa.services.SpaServiceService;
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
    private Conversion conversion;

    public ManagerController(ManagerService managerService, SpaServiceService spaServiceService){
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
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
}
