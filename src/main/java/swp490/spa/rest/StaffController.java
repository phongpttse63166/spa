package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;
import swp490.spa.utils.support.SupportFunctions;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffController {
    private Logger LOGGER = LogManager.getLogger(StaffController.class);
    @Autowired
    private StaffService staffService;
    @Autowired
    private UserService userService;
    @Autowired
    private DateOffService dateOffService;
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public StaffController(StaffService staffService, UserService userService,
                           DateOffService dateOffService,
                           BookingDetailStepService bookingDetailStepService) {
        this.staffService = staffService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
    }

    @GetMapping("/findbyId")
    public Response findStaffById(@RequestParam Integer userId){
        Staff staff = staffService.findByStaffId(userId);
        return ResponseHelper.ok(staff);
    }

    @PutMapping("/editprofile")
    public Response editProfileStaff(@RequestBody User user){
        Staff staffResult = staffService.findByStaffId(user.getId());
        if(Objects.nonNull(staffResult)){
            User userResult = staffResult.getUser();
            userResult.setFullname(user.getFullname());
            userResult.setEmail(user.getEmail());
            userResult.setAddress(user.getAddress());
            userResult.setBirthdate(user.getBirthdate());
            userResult.setGender(user.getGender());
            if(Objects.nonNull(userService.editUser(user))){
                return ResponseHelper.ok(userResult);
            }
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.PROFILE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account){
        Staff staff = staffService.findByStaffId(account.getId());
        User oldUser = staff.getUser();
        User updateUser = staff.getUser();
        updateUser.setPassword(account.getPassword());
        if(Objects.nonNull(userService.editUser(updateUser))){
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }

    @PostMapping("/dateoff/create/{staffId}")
    public Response insertNewDateOff(@PathVariable Integer staffId,
                                     @RequestBody DateOff dateOff) {
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByDateBookingAndStaff(dateOff.getDateOff(),
                        staffId);
        if (bookingDetailSteps.size() == 0) {
            DateOff dateOffResult = dateOffService.insertNewDateOff(dateOff);
            if(Objects.nonNull(dateOffResult)){
                return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.DATE_OFF));
            }
        } else {
            return ResponseHelper.error(String.format(LoggingTemplate.BOOKING_SERVICE_EXISTED));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.DATE_OFF));
    }

    @GetMapping("/workingofstaff/findbydatechosen/{staffId}")
    public Response findWorkingOfStaffByDateChosen(@PathVariable Integer staffId,
                                                    @RequestParam String dateChosen){
        Staff staff = staffService.findByStaffId(staffId);
        if(Objects.nonNull(staff)){
            Page<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByStaffIdAndDateBooking(staffId, Date.valueOf(dateChosen),
                            PageRequest.of(Constant.PAGE_DEFAULT,Constant.SIZE_DEFAULT, Sort.unsorted()));
            if(Objects.nonNull(bookingDetailSteps)){
                return ResponseHelper.ok(conversion.convertToPageBookingDetailStepResponse(bookingDetailSteps));
            } else {
                LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
        }
    }

//    @PutMapping("/bookingDetailStep/confirmFinishAStep/{bookingDetailStepId}")
//    public Response confirmFinishAStep(@PathVariable Integer bookingDetailStepId,
//                                       @RequestBody ConsultationContent consultationContent){
//        boolean checkFinishAll = false;
//        BookingDetailStep bookingDetailStep =
//                bookingDetailStepService.findById(bookingDetailStepId);
//        if(Objects.nonNull(bookingDetailStep)){
//            ConsultationContent consultationContentGet = bookingDetailStep.getConsultationContent();
//            consultationContentGet.setResult(consultationContent.getResult());
//        } else {
//            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
//        }
//        return ResponseHelper.error(LoggingTemplate.CONFIRM_FINISH_FAILED);
//    }
}
