package swp490.spa.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.services.SpaService;
import swp490.spa.utils.support.Notification;

import java.util.Objects;

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
    private UserService userService;
    private Conversion conversion;

    public CustomerController(CustomerService customerService, UserLocationService userLocationService,
                              AccountRegisterService accountRegisterService, UserService userService,
                              SpaPackageService spaPackageService, SpaService spaService) {
        this.customerService = customerService;
        this.userLocationService = userLocationService;
        this.accountRegisterService = accountRegisterService;
        this.userService = userService;
        this.spaPackageService = spaPackageService;
        this.spaService = spaService;
        this.conversion = new Conversion();
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
            return ResponseHelper.ok(userLocationUpdate);
        }
        return ResponseHelper.ok("");
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

    @GetMapping("/getallspapackage")
    public Response getAllSpaPackage(Pageable pageable){
        Page<SpaPackage> spaPackages =
                spaPackageService.findAllStatusAvailable(pageable);
        if(!spaPackages.hasContent() && !spaPackages.isFirst()){
            spaPackages = spaPackageService
                    .findAllStatusAvailable(PageRequest.of(spaPackages.getTotalPages()-1,
                            spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToSpaPackageResponse(spaPackages));
    }
}
