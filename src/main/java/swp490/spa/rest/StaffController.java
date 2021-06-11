package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.Staff;
import swp490.spa.entities.TreatmentService;
import swp490.spa.services.StaffService;
import swp490.spa.services.TreatmentServiceService;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffController {
    @Autowired
    private StaffService staffService;
    @Autowired
    private TreatmentServiceService treatmentServiceService;
    private Conversion conversion;

    public StaffController(StaffService staffService, TreatmentServiceService treatmentServiceService) {
        this.staffService = staffService;
        this.treatmentServiceService = treatmentServiceService;
        this.conversion = new Conversion();
    }

    @GetMapping("/search/{userId}")
    public Response findStaffById(@PathVariable Integer userId){
        Staff staff = staffService.findByStaffId(userId);
        return ResponseHelper.ok(staff);
    }

    @GetMapping("/treatmentservice")
    public Response findBySpaTreatmentId(@RequestParam Integer spaTreatmentId, Pageable pageable){
        Page<TreatmentService> treatmentServices =
                treatmentServiceService.findBySpaTreatmentId(spaTreatmentId, pageable);
        if(!treatmentServices.hasContent() && !treatmentServices.isFirst()){
            treatmentServices = treatmentServiceService.findBySpaTreatmentId(spaTreatmentId,
                    PageRequest.of(treatmentServices.getTotalPages()-1,
                            treatmentServices.getSize(), treatmentServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageTreatmentServiceResponse(treatmentServices));

    }
}
