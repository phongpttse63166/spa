package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.services.SpaService;
import swp490.spa.utils.support.Constant;
import swp490.spa.utils.support.Notification;
import swp490.spa.utils.support.SupportFunctions;

import java.sql.Date;
import java.util.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerController {
    @Autowired
    private CustomerService customerService;
    @Autowired
    private UserLocationService userLocationService;
    @Autowired
    private AccountRegisterService accountRegisterService;
    @Autowired
    private SpaPackageService spaPackageService;
    @Autowired
    private SpaService spaService;
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private UserService userService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public CustomerController(CustomerService customerService, UserLocationService userLocationService,
                              AccountRegisterService accountRegisterService, UserService userService,
                              SpaPackageService spaPackageService, SpaService spaService,
                              BookingDetailStepService bookingDetailStepService, StaffService staffService,
                              SpaTreatmentService spaTreatmentService) {
        this.customerService = customerService;
        this.userLocationService = userLocationService;
        this.accountRegisterService = accountRegisterService;
        this.userService = userService;
        this.spaPackageService = spaPackageService;
        this.spaService = spaService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.staffService = staffService;
        this.spaTreatmentService = spaTreatmentService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions(this.bookingDetailStepService);
    }

    @GetMapping("/search/{userId}")
    public Response findCustomerById(@PathVariable Integer userId){
        Customer customer = customerService.findByUserId(userId);
        return ResponseHelper.ok(customer);
    }

    @PostMapping("/userlocation/create")
    public Response createNewUserLocation(@RequestBody UserLocation userLocation){
        UserLocation newUserLocation = userLocationService.insertNewUserLocation(userLocation);
        return ResponseHelper.ok(userLocation);
    }

    @PutMapping("/userlocation/edit")
    public Response editUserLocation(@RequestBody UserLocation userLocationUpdate){
        UserLocation userLocation = userLocationService.findUserLocationByUserId(userLocationUpdate.getId());
        if(userLocation!=null){
            UserLocation result = userLocationService.editUserLocation(userLocationUpdate);
            if(Objects.nonNull(result)){
                return ResponseHelper.ok(userLocationUpdate);
            }
            return ResponseHelper.error(Notification.EDIT_USER_LOCATION_FAIL);
        }
        return ResponseHelper.error(Notification.USER_EXISTED);
    }

    @PutMapping("/user/edit")
    public Response editUserProfile(@RequestBody User user){
        User userResult = userService.findByPhone(user.getPhone());
        if(Objects.nonNull(userResult)){
            userResult.setFullname(user.getFullname());
            userResult.setAddress(user.getAddress());
            userResult.setEmail(user.getEmail());
            if(Objects.nonNull(userService.editUser(userResult))){
                return ResponseHelper.ok(userResult);
            }
        }
        return ResponseHelper.error(Notification.EDIT_PROFILE_FAIL);
    }

    @GetMapping("/getprofile")
    public Response getUserProfile(@RequestParam String userId){
        Customer customer = customerService.findByUserId(Integer.parseInt(userId));
        if (Objects.nonNull(customer)) {
            return ResponseHelper.ok(customer);
        }
        return ResponseHelper.error(Notification.CUSTOMER_NOT_EXISTED);
    }

    @GetMapping("/getlisttimebook")
    public Response getListTimeToBook(@RequestParam Integer spaPackageId,
                                      @RequestParam String dateBooking){
        SpaTreatment spaTreatment
                = spaTreatmentService.findTreatmentBySpaPackageIdWithTypeOneStep(spaPackageId);
        List<Staff> staffList = staffService.findBySpaId(spaTreatment.getSpa().getId());
        List<String> timeBookingList =
                supportFunctions.getBookTime(spaTreatment.getTotalTime(),
                        staffList, dateBooking);
        Page<String> page = new PageImpl<>(timeBookingList,
                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()),
                        timeBookingList.size());
        return ResponseHelper.ok(page);
    }

//    @PostMapping("/bookingonestep/create")
//    public Response insertBookingOneStep(){
//
//    }
}
