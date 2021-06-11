package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Staff;
import swp490.spa.services.StaffService;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffController {
    @Autowired
    private StaffService staffService;
    private Conversion conversion;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
        this.conversion = new Conversion();
    }

    @GetMapping("/search/{userId}")
    public Response findStaffById(@PathVariable Integer userId){
        Staff staff = staffService.findByStaffId(userId);
        return ResponseHelper.ok(staff);
    }

}
