package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.requests.AuthRequest;
import swp490.spa.dto.responses.LoginResponse;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.entities.*;
import swp490.spa.jwt.JWTUtils;
import swp490.spa.services.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.services.SpaService;
import swp490.spa.utils.support.GenerationOTP;
import swp490.spa.utils.support.Notification;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/api/public")
@CrossOrigin
public class PublicController {
    Logger logger = LogManager.getLogger(PublicController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpaService spaService;
    @Autowired
    private SpaServiceService spaServiceService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private SpaPackageService spaPackageService;
    @Autowired
    private AccountRegisterService accountRegisterService;
    private Conversion conversion;
    @Autowired
    JWTUtils jwtUtils;

    public PublicController(UserService userService, CategoryService categoryService,
                            SpaService spaService, SpaServiceService spaServiceService,
                            CustomerService customerService, StaffService staffService,
                            ManagerService managerService, SpaTreatmentService spaTreatmentService,
                            SpaPackageService spaPackageService, AccountRegisterService accountRegisterService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.spaService = spaService;
        this.spaServiceService = spaServiceService;
        this.customerService = customerService;
        this.staffService = staffService;
        this.managerService = managerService;
        this.spaTreatmentService = spaTreatmentService;
        this.spaPackageService = spaPackageService;
        this.accountRegisterService = accountRegisterService;
        this.conversion = new Conversion();
    }

    @GetMapping("/category")
    public Response findCategoryByStatus(@RequestParam String status, Pageable pageable){
        Page<Category> categories = categoryService.findAllByStatus(status, pageable);
        if (!categories.hasContent() && !categories.isFirst()) {
            categories = categoryService.findAllByStatus(status,
                    PageRequest.of(categories.getTotalPages()-1, categories.getSize(), categories.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToCategoryResponse(categories));
    }

    @GetMapping("/user")
    public Response findUserByPhone(@RequestParam String phone){
        User user = userService.findByPhone(phone);
        return ResponseHelper.ok(user);
    }

    @GetMapping("/spaservice")
    public Response findSpaServiceBySpaId(@RequestParam Integer spaId, @RequestParam Status status,
                                          @RequestParam String search, Pageable pageable){
        Page<swp490.spa.entities.SpaService> spaServices =
                spaServiceService.findBySpaIdAndStatus(spaId, status, search, pageable);
        if(!spaServices.hasContent() && !spaServices.isFirst()){
            spaServices = spaServiceService.findBySpaIdAndStatus(spaId, status, search,
                    PageRequest.of(spaServices.getTotalPages()-1, spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToSpaServiceResponse(spaServices));
    }

    @GetMapping("/spatreatment")
    public Response findSpaTreatmentBySpaId(@RequestParam Integer spaId,
                                            @RequestParam String search, Pageable pageable){
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findTreatmentBySpaId(spaId, search, pageable);
        if(!spaTreatments.hasContent() && !spaTreatments.isFirst()){
            spaTreatments = spaTreatmentService.findTreatmentBySpaId(spaId, search,
                    PageRequest.of(spaTreatments.getTotalPages()-1, spaTreatments.getSize(), spaTreatments.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spapackage")
    public Response findSpaPackageBySpaId(@RequestParam Integer spaId, @RequestParam Status status,
                                          @RequestParam String search, Pageable pageable){
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageBySpaIdAndStatus(spaId, status, search, pageable);
        if(!spaPackages.hasContent() && !spaPackages.isFirst()){
            spaPackages = spaPackageService.findSpaPackageBySpaIdAndStatus(spaId, status, search,
                    PageRequest.of(spaPackages.getTotalPages()-1, spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToSpaPackageResponse(spaPackages));
    }

    @PostMapping("/register")
    public Response registerAccount(@RequestBody AccountRegister accountRegister){
        User userResult = userService.findByPhone(accountRegister.getPhone());
        if(Objects.nonNull(userResult)){
            return ResponseHelper.error(Notification.USER_EXISTED);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expireTime = currentTime.plusMinutes(15);
        Date createTime = Date.valueOf(currentTime.toLocalDate());
        Date expiredTime = Date.valueOf(expireTime.toLocalDate());
        AccountRegister result = accountRegisterService.findByPhone(accountRegister.getPhone());
        if(Objects.nonNull(result)){
            result.setOtpCode(GenerationOTP.generateOTPCode(0, 9999));
            result.setCreateTime(createTime);
            result.setExpiredTime(expiredTime);
            if(Objects.nonNull(accountRegisterService.updateAccountRegister(result))){
                return ResponseHelper.ok(result);
            }
            return ResponseHelper.error(Notification.SEND_OTP_FAIL);
        } else {
            User user = userService.findByPhone(accountRegister.getPhone());
            if (Objects.isNull(user)) {
                accountRegister.setOtpCode(GenerationOTP.generateOTPCode(0, 9999));
                accountRegister.setCreateTime(createTime);
                accountRegister.setExpiredTime(expiredTime);
                AccountRegister newAccountRegister = accountRegisterService
                        .insertNewAccountRegister(accountRegister);
                if (Objects.nonNull(newAccountRegister)) {
                    return ResponseHelper.ok(accountRegister);
                }
            } else {
                return ResponseHelper.error(Notification.USER_EXISTED);
            }
        }
        return ResponseHelper.error(Notification.REGISTER_FAIL);
    }

    @PostMapping("/verifyregister")
    public Response verifyRegister(@RequestBody AccountRegister accountRegister){
        AccountRegister result = accountRegisterService.findByPhone(accountRegister.getPhone());
        if(result!=null){
            Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
            if(currentTime.compareTo(result.getExpiredTime())==1){
                return ResponseHelper.error(Notification.EXPIRED_VERIFY);
            }
            if(!accountRegister.getOtpCode().equalsIgnoreCase(result.getOtpCode())){
                return ResponseHelper.error(Notification.OTP_NOT_MATCH);
            }
            User newUser = new User();
            newUser.setActive(true);
            newUser.setPhone(result.getPhone());
            newUser.setFullname(result.getFullname());
            newUser.setPassword(result.getPassword());
            if(Objects.nonNull(userService.insertNewUser(newUser))){
                User resultUser = userService.findByPhone(newUser.getPhone());
                if(Objects.nonNull(resultUser)){
                    accountRegisterService.deleteAccountRegister(result.getId());
                    Customer customer = new Customer();
                    customer.setUser(resultUser);
                    customer.setCustomType("Normal");
                    if(Objects.nonNull(customerService.insertNewCustomer(customer))){
                        return ResponseHelper.error(Notification.REGISTER_SUCCESS);
                    }
                } else {
                    return ResponseHelper.error(Notification.NO_DATA_USER);
                }
            } else {
                return ResponseHelper.error(Notification.INSERT_USER_FAIL);
            }


        }
        return ResponseHelper.error(Notification.VERIFY_FAIL);
    }

    @GetMapping("/getallspa")
    public Response getAllSpa(Pageable pageable){
        Page<Spa> spas = spaService.findAllSpaByStatusAvailable(pageable);
        if(!spas.hasContent() && !spas.isFirst()){
            spas = spaService.findAllSpaByStatusAvailable(
                    PageRequest.of(spas.getTotalPages()-1, spas.getSize(), spas.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToSpaResponse(spas));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest authRequest){
        String phone = authRequest.getPhone().trim();
        String password = authRequest.getPassword().trim();
        String role = authRequest.getRole().name();
        Integer spaId = null;

        if(phone.isEmpty() || password.isEmpty() || role.isEmpty()){
            return LoginResponse.createErrorResponse(LoginResponse.Error.BLANK_FIELD);
        }

        User user = userService.findByPhone(phone);
        if(user == null){
            return LoginResponse.createErrorResponse(LoginResponse.Error.USERNAME_NOT_FOUND);
        }
        if(!user.getPassword().equals(password)){
            return LoginResponse.createErrorResponse(LoginResponse.Error.WRONG_PASSWORD);
        }

        boolean isExisted = true;

        switch(role){
            case "CUSTOMER":
                Customer customer = customerService.findByUserId(user.getId());
                if(customer == null){
                    return LoginResponse.createErrorResponse(LoginResponse.Error.CUSTOMER_NOT_EXISTED);
                }
                break;
            case "STAFF":
                // tu staff lay qua
                break;
            case "MANAGER":
                Manager manager = managerService.findManagerById(user.getId());
                if(Objects.isNull(manager)){
                    return LoginResponse.createErrorResponse(LoginResponse.Error.MANAGER_NOT_EXISTED);
                }
                spaId = manager.getSpa().getId();
                break;
            case "ADMIN":
                break;
            default:
                isExisted = false;
                break;
        }

        if(!isExisted){
            return LoginResponse.createErrorResponse(LoginResponse.Error.ROLE_NOT_EXISTED);
        }

        String token = jwtUtils.generateToken(user.getPhone(), role);
        Integer userId = user.getId();
        return LoginResponse.createSuccessResponse(token,role,userId,spaId);
    }

    @GetMapping("/spaservice/findbyspapackageid")
    public Response findSpaServiceBySpaPackageId(@RequestParam Integer spaPackageId,
                                                 @RequestParam Integer spaId, Pageable pageable){
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaServiceBySpaPackageId(spaPackageId, spaId, pageable);
        if(!spaPackages.hasContent() && !spaPackages.isFirst()){
            spaPackages = spaPackageService.findSpaServiceBySpaPackageId(spaPackageId, spaId,
                    PageRequest.of(spaPackages.getTotalPages()-1, spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToSpaPackageResponse(spaPackages));
    }
}
