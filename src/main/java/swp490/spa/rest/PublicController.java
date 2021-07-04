package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.dto.requests.AuthRequest;
import swp490.spa.dto.responses.LoginResponse;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.responses.SpaPackageGetAllResponse;
import swp490.spa.entities.*;
import swp490.spa.jwt.JWTUtils;
import swp490.spa.services.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.support.Response;
import swp490.spa.services.SpaService;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.image.UploadImage;
import swp490.spa.utils.support.otp.GenerationOTP;
import swp490.spa.utils.support.templates.LoggingTemplate;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public")
@CrossOrigin
public class PublicController {
    Logger LOGGER = LogManager.getLogger(PublicController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private ConsultantService consultantService;
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
                            AdminService adminService, ConsultantService consultantService,
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

    @GetMapping("/category/findall")
    public Response findCategoryByStatus(@RequestParam Status status, Pageable pageable) {
        Page<Category> categories = categoryService.findAllByStatus(status, pageable);
        if (!categories.hasContent() && !categories.isFirst()) {
            categories = categoryService.findAllByStatus(status,
                    PageRequest.of(categories.getTotalPages() - 1, categories.getSize(), categories.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageCategoryResponse(categories));
    }

    @GetMapping("/user/findbyphone")
    public Response findUserByPhone(@RequestParam String phone) {
        User user = userService.findByPhone(phone);
        return ResponseHelper.ok(user);
    }

    @GetMapping("/spaservice/findbyspaid")
    public Response findSpaServiceBySpaId(@RequestParam Integer spaId, @RequestParam Status status,
                                          @RequestParam String search, Pageable pageable) {
        Page<swp490.spa.entities.SpaService> spaServices =
                spaServiceService.findBySpaIdAndStatus(spaId, status, search, pageable);
        if (!spaServices.hasContent() && !spaServices.isFirst()) {
            spaServices = spaServiceService.findBySpaIdAndStatus(spaId, status, search,
                    PageRequest.of(spaServices.getTotalPages() - 1, spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @GetMapping("/spatreatment/findbyspaid")
    public Response findSpaTreatmentBySpaId(@RequestParam Integer spaId,
                                            @RequestParam String search, Pageable pageable) {
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findTreatmentBySpaId(spaId, search, pageable);
        if (!spaTreatments.hasContent() && !spaTreatments.isFirst()) {
            spaTreatments = spaTreatmentService.findTreatmentBySpaId(spaId, search,
                    PageRequest.of(spaTreatments.getTotalPages() - 1, spaTreatments.getSize(), spaTreatments.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spapackage/findbyspaid")
    public Response findSpaPackageBySpaId(@RequestParam Integer spaId, @RequestParam Status status,
                                          @RequestParam String search, Pageable pageable) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageBySpaIdAndStatus(spaId, status, search, pageable);
        if (!spaPackages.hasContent() && !spaPackages.isFirst()) {
            spaPackages = spaPackageService.findSpaPackageBySpaIdAndStatus(spaId, status, search,
                    PageRequest.of(spaPackages.getTotalPages() - 1, spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @PostMapping("/register")
    public Response registerAccount(@RequestBody AccountRegister accountRegister) {
        User userResult = userService.findByPhone(accountRegister.getPhone());
        if (Objects.nonNull(userResult)) {
            return ResponseHelper.error(LoggingTemplate.USER_EXISTED);
        }
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime expireTime = currentTime.plusMinutes(15);
        Date createTime = Date.valueOf(currentTime.toLocalDate());
        Date expiredTime = Date.valueOf(expireTime.toLocalDate());
        AccountRegister result = accountRegisterService.findByPhone(accountRegister.getPhone());
        if (Objects.nonNull(result)) {
            result.setOtpCode(GenerationOTP.generateOTPCode(0, 9999));
            result.setCreateTime(createTime);
            result.setExpiredTime(expiredTime);
            if (Objects.nonNull(accountRegisterService.updateAccountRegister(result))) {
                return ResponseHelper.ok(result);
            }
            return ResponseHelper.error(LoggingTemplate.SEND_OTP_FAILED);
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
                return ResponseHelper.error(LoggingTemplate.USER_EXISTED);
            }
        }
        return ResponseHelper.error(LoggingTemplate.REGISTER_FAILED);
    }

    @PostMapping("/verifyregister")
    public Response verifyRegister(@RequestBody AccountRegister accountRegister) {
        AccountRegister result = accountRegisterService.findByPhone(accountRegister.getPhone());
        if (result != null) {
            Date currentTime = Date.valueOf(LocalDateTime.now().toLocalDate());
            if (currentTime.compareTo(result.getExpiredTime()) == 1) {
                return ResponseHelper.error(LoggingTemplate.EXPIRED_VERIFY);
            }
            if (!accountRegister.getOtpCode().equalsIgnoreCase(result.getOtpCode())) {
                return ResponseHelper.error(LoggingTemplate.OTP_NOT_MATCH);
            }
            User newUser = new User();
            newUser.setActive(true);
            newUser.setPhone(result.getPhone());
            newUser.setFullname(result.getFullname());
            newUser.setPassword(result.getPassword());
            if (Objects.nonNull(userService.insertNewUser(newUser))) {
                User resultUser = userService.findByPhone(newUser.getPhone());
                if (Objects.nonNull(resultUser)) {
                    accountRegisterService.deleteAccountRegister(result.getId());
                    Customer customer = new Customer();
                    customer.setUser(resultUser);
                    customer.setCustomType("Normal");
                    if (Objects.nonNull(customerService.insertNewCustomer(customer))) {
                        return ResponseHelper.error(LoggingTemplate.REGISTER_SUCCESS);
                    }
                } else {
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.USER));
                }
            } else {
                return ResponseHelper.error(String.format(LoggingTemplate.INSERT_SUCCESS, newUser.getFullname()));
            }


        }
        return ResponseHelper.error(LoggingTemplate.VERIFY_FAILED);
    }

    @GetMapping("/spa/findall")
    public Response getAllSpa(Pageable pageable) {
        Page<Spa> spas = spaService.findAllSpaByStatusAvailable(pageable);
        if (!spas.hasContent() && !spas.isFirst()) {
            spas = spaService.findAllSpaByStatusAvailable(
                    PageRequest.of(spas.getTotalPages() - 1, spas.getSize(), spas.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaResponse(spas));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody AuthRequest authRequest) {
        String phone = authRequest.getPhone().trim();
        String password = authRequest.getPassword().trim();
        String role = authRequest.getRole().name();
        Integer spaId = null;

        if (phone.isEmpty() || password.isEmpty() || role.isEmpty()) {
            return LoginResponse.createErrorResponse(LoginResponse.Error.BLANK_FIELD);
        }

        User user = userService.findByPhone(phone);
        if (user == null) {
            return LoginResponse.createErrorResponse(LoginResponse.Error.USERNAME_NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            return LoginResponse.createErrorResponse(LoginResponse.Error.WRONG_PASSWORD);
        }

        boolean isExisted = true;

        switch (role) {
            case "CUSTOMER":
                Customer customer = customerService.findByUserId(user.getId());
                if (customer == null) {
                    return LoginResponse.createErrorResponse(LoginResponse.Error.CUSTOMER_NOT_EXISTED);
                }
                break;
            case "STAFF":
                Staff staff = staffService.findByStaffId(user.getId());
                if (Objects.isNull(staff)) {
                    return LoginResponse.createErrorResponse(LoginResponse.Error.STAFF_NOT_EXISTED);
                }
                spaId = staff.getSpa().getId();
                break;
            case "MANAGER":
                Manager manager = managerService.findManagerById(user.getId());
                if (Objects.isNull(manager)) {
                    return LoginResponse.createErrorResponse(LoginResponse.Error.MANAGER_NOT_EXISTED);
                }
                spaId = manager.getSpa().getId();
                break;
            case "ADMIN":
                break;
            case "CONSULTANT":
                Consultant consultant = consultantService.findByConsultantId(user.getId());
                if (Objects.isNull(consultant)) {
                    return LoginResponse.createErrorResponse(LoginResponse.Error.CONSULTANT_NOT_EXISTED);
                }
                spaId = consultant.getSpa().getId();
                break;
            default:
                isExisted = false;
                break;
        }

        if (!isExisted) {
            return LoginResponse.createErrorResponse(LoginResponse.Error.ROLE_NOT_EXISTED);
        }

        String token = jwtUtils.generateToken(user.getPhone(), role);
        Integer userId = user.getId();
        return LoginResponse.createSuccessResponse(token, role, userId, spaId);
    }

    @GetMapping("/spaservice/findbyspapackageid")
    public Response findSpaServiceBySpaPackageId(@RequestParam Integer spaPackageId) {
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaPackageId);
        return ResponseHelper.ok(conversion.convertToSpaPackageResponse(spaPackage));
    }

    @GetMapping("/getallspapackage")
    public Response getAllSpaPackage(Pageable pageable) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findAllStatusAvailable(pageable);
        if (!spaPackages.hasContent() && !spaPackages.isFirst()) {
            spaPackages = spaPackageService
                    .findAllStatusAvailable(PageRequest.of(spaPackages.getTotalPages() - 1,
                            spaPackages.getSize(), spaPackages.getSort()));
        }
        long totalElement = spaPackages.getTotalElements();
        List<SpaPackageGetAllResponse> sprList = spaPackages.getContent().stream()
                .map(spaPackage -> new SpaPackageGetAllResponse(spaPackage.getId(),
                        spaPackage.getName(),
                        spaPackage.getDescription(),
                        spaPackage.getImage(),
                        spaPackage.getType(),
                        spaPackage.getStatus(),
                        spaPackage.getCreateTime(),
                        spaPackage.getCreate_by(),
                        spaPackage.getCategory(),
                        spaPackage.getSpa(),
                        Constant.TOTAL_TIME_DEFAULT,
                        spaPackage.getSpaServices()))
                .collect(Collectors.toList());
        for (SpaPackageGetAllResponse spaPackage : sprList) {
            if(spaPackage.getType().equals(Type.ONESTEP)) {
                SpaTreatment spaTreatment =
                        spaTreatmentService.findByPackageIdAndTypeOneStep(spaPackage.getId());
                spaPackage.setTotalTime(spaTreatment.getTotalTime());
            } else {
                spaPackage.setTotalTime(Constant.DURATION_OF_CONSULTATION);
            }
        }
        Page<SpaPackageGetAllResponse> page = new PageImpl<>(sprList, pageable, totalElement);
        return ResponseHelper.ok(page);
    }

    @GetMapping("/spapackage/findbycategoryId")
    public Response findSpaPackageByCategoryId(@RequestParam Integer categoryId,
                                               Pageable pageable) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findByCategoryIdOrderByDate(categoryId, pageable);
        if (!spaPackages.hasContent() && !spaPackages.isFirst()) {
            spaPackages = spaPackageService
                    .findByCategoryIdOrderByDate(categoryId,
                            PageRequest.of(spaPackages.getTotalPages() - 1,
                                    spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @PostMapping("/image/upload")
    public String testUpdateImage(@RequestParam("file") MultipartFile file) {
        return UploadImage.uploadImage(file);
    }
}
