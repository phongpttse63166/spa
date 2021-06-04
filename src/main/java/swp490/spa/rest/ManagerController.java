package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Manager;
import swp490.spa.services.ManagerService;

@RequestMapping("/api/manager")
@RestController
@CrossOrigin
public class ManagerController {
    @Autowired
    private ManagerService managerService;
    private Conversion conversion;

    public ManagerController(ManagerService managerService){
        this.managerService = managerService;
        this.conversion = new Conversion();
    }

    @GetMapping(value = "/test")
    public String testJWT(){
        return "Thành công";
    }

    @GetMapping("/search/{userId}")
    public Response findManagerById(@PathVariable Integer userId){
        Manager manager = managerService.findManagerById(userId);
        return ResponseHelper.ok(manager);
    }
}
