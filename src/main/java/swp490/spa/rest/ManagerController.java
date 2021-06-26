package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.SpaPackageCreateRequest;
import swp490.spa.dto.requests.SpaServiceCreateRequest;
import swp490.spa.dto.requests.SpaTreatmentCreateRequest;
import swp490.spa.dto.responses.BookingBookingDetailResponse;
import swp490.spa.dto.responses.CategorySpaPackageResponse;
import swp490.spa.dto.responses.SpaPackageTreatmentResponse;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.entities.SpaService;
import swp490.spa.services.*;
import swp490.spa.utils.support.Constant;
import swp490.spa.utils.support.Notification;
import swp490.spa.utils.support.UploadImage;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequestMapping("/api/manager")
@RestController
@CrossOrigin
public class ManagerController {
    Logger LOGGER = LogManager.getLogger(ManagerController.class);
    @Autowired
    private ManagerService managerService;
    @Autowired
    private SpaServiceService spaServiceService;
    @Autowired
    private SpaPackageService spaPackageService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private swp490.spa.services.SpaService spaService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private DateOffService dateOffService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingDetailService bookingDetailService;
    private Conversion conversion;

    public ManagerController(ManagerService managerService, SpaServiceService spaServiceService,
                             SpaPackageService spaPackageService, SpaTreatmentService spaTreatmentService,
                             swp490.spa.services.SpaService spaService, UserService userService,
                             DateOffService dateOffService, BookingService bookingService,
                             BookingDetailService bookingDetailService) {
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
        this.spaPackageService = spaPackageService;
        this.spaTreatmentService = spaTreatmentService;
        this.spaService = spaService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.bookingDetailService = bookingDetailService;
        this.conversion = new Conversion();
    }

    @GetMapping("/search/{userId}")
    public Response findManagerById(@PathVariable Integer userId) {
        Manager manager = managerService.findManagerById(userId);
        return ResponseHelper.ok(manager);
    }

    @GetMapping("/spapackage/findbyspaid")
    public Response getSpaPackageBySpaId(@RequestParam Integer spaId,
                                         @RequestParam String search,
                                         Pageable pageable) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageBySpaIdAndStatusAvailable(spaId, search, pageable);
        if (!spaPackages.hasContent() && !spaPackages.isFirst()) {
            spaPackages = spaPackageService
                    .findSpaPackageBySpaIdAndStatusAvailable(spaId, search,
                            PageRequest.of(spaPackages.getTotalPages() - 1,
                                    spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @GetMapping("/spatreatment/findbypackageId")
    public Response findSpaTreatmentByPackageId(@RequestParam Integer packageId,
                                                @RequestParam String search, Pageable pageable) {
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(packageId, search, pageable);
        if (!spaTreatments.hasContent() && !spaTreatments.isFirst()) {
            spaTreatments = spaTreatmentService.findByPackageId(packageId, search,
                    PageRequest.of(spaTreatments.getTotalPages() - 1, spaTreatments.getSize(), spaTreatments.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spaservice/findbyspaId")
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

    @GetMapping("/spapackage/findbyserviceId")
    public Response findSpaPackageBySpaServiceId(@RequestParam Integer spaServiceId,
                                                 @RequestParam Integer spaId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer size,
                                                 @RequestParam String search) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findAllBySpaServiceId(spaServiceId, spaId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @GetMapping("/spatreatment/findbyserviceId")
    public Response findSpaTreatmentBySpaServiceId(@RequestParam Integer spaServiceId,
                                                   @RequestParam Integer spaId,
                                                   @RequestParam Integer page,
                                                   @RequestParam Integer size,
                                                   @RequestParam String search) {
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findAllBySpaServiceId(spaServiceId, spaId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spapackagetreatment/findbyspaId")
    public Response findSpaPackageTreatmentBySpaId(@RequestParam Integer spaId,
                                                   @RequestParam String search,
                                                   Pageable pageable) {
        List<SpaPackageTreatmentResponse> result = new ArrayList<>();
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageBySpaIdAndStatusAvailable(spaId, search, pageable);
        int totalItem = (int) spaPackages.getTotalElements();
        if (spaPackages.getContent().size() != 0 && !spaPackages.getContent().isEmpty()) {
            for (SpaPackage spaPackage : spaPackages.getContent()) {
                List<SpaTreatment> spaTreatments = new ArrayList<>();
                SpaPackageTreatmentResponse sptr = new SpaPackageTreatmentResponse();
                sptr.setSpaPackage(spaPackage);
                spaTreatments =
                        spaTreatmentService.findByPackageId(spaPackage.getId(),
                                Constant.SEARCH_NO_CONTENT,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT)).toList();
                sptr.setSpaTreatments(spaTreatments);
                result.add(sptr);
            }
        }
        Page<SpaPackageTreatmentResponse> page = new PageImpl<>(result, pageable, totalItem);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageTreatmentResponse(page));
    }

    @GetMapping("/spaservice/findbyspaidandtype")
    public Response findSpaServiceBySpaIdAndType(@RequestParam Integer spaId,
                                                 @RequestParam Type type,
                                                 @RequestParam String search,
                                                 Pageable pageable) {
        Page<SpaService> spaServices = spaServiceService.findBySpaIdAndType(spaId, type, search, pageable);
        if (!spaServices.hasContent() && !spaServices.isFirst()) {
            spaServices = spaServiceService.findBySpaIdAndType(spaId, type, search,
                    PageRequest.of(spaServices.getTotalPages() - 1, spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @GetMapping("/categoryspapackages/findbyspaId")
    public Response findCategorySpaPackagesBySpaId(@RequestParam Integer spaId,
                                                   @RequestParam Status status,
                                                   Pageable pageable) {
        List<Category> categories =
                categoryService.findCategoryBySpaId(spaId, status, pageable).getContent();
        if (Objects.nonNull(categories)) {
            List<CategorySpaPackageResponse> categorySpaPackageResponses = new ArrayList<>();
            for (Category category : categories) {
                List<SpaPackage> spaPackages = spaPackageService.findByCategoryId(category.getId());
                CategorySpaPackageResponse cspr = new CategorySpaPackageResponse(category, spaPackages);
                categorySpaPackageResponses.add(cspr);
            }
            Page<CategorySpaPackageResponse> pageReturn =
                    new PageImpl<>(categorySpaPackageResponses, pageable,
                            categorySpaPackageResponses.size());
            return ResponseHelper.ok(pageReturn);
        }
        return ResponseHelper.error(Notification.CATEGORY_NOT_EXISTED);
    }

    @GetMapping("/booking/findbookingwithbookingdetails")
    public Response findBookingWithBookingDetailsBySpa(@RequestParam StatusBooking statusBooking,
                                                       @RequestParam Integer spaId,
                                                       Pageable pageable) {
        Page<Booking> bookingPage =
                bookingService.findByBookingStatusAndSpa(statusBooking, spaId, pageable);
        List<Booking> bookings = bookingPage.getContent();
        long totalElements = bookingPage.getTotalElements();
        List<BookingBookingDetailResponse> bookingResponses = new ArrayList<>();
        if (Objects.nonNull(bookings)) {
            for (Booking booking : bookings) {
                BookingBookingDetailResponse bookingInsert = new BookingBookingDetailResponse();
                List<BookingDetail> bookingDetails
                        = bookingDetailService.findByBooking(booking.getId(), pageable).getContent();
                if (Objects.nonNull(bookingDetails)) {
                    bookingInsert.setBooking(booking);
                    bookingInsert.setBookingDetailList(bookingDetails);
                    bookingResponses.add(bookingInsert);
                } else {
                    LOGGER.info(Notification.BOOKING_DETAIL_NOT_EXISTED);
                }
            }
        } else {
            LOGGER.info(Notification.BOOKING_NOT_EXISTED);
            return ResponseHelper.error(Notification.BOOKING_NOT_EXISTED);
        }
        Page<BookingBookingDetailResponse> page =
                new PageImpl<>(bookingResponses, pageable, totalElements);
        return ResponseHelper.ok(page);
    }

    @PostMapping(value = "/spaservice/insert", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response createNewSpaService(SpaServiceCreateRequest spaServiceCreateRequest) {
        if (Objects.nonNull(spaServiceCreateRequest.getFile())) {
            String imageLink = UploadImage.uploadImage(spaServiceCreateRequest.getFile());
            if (imageLink != "") {
                Manager manager = managerService.findManagerById(spaServiceCreateRequest.getCreateBy());
                if (Objects.isNull(manager)) {
                    LOGGER.info(Notification.MANAGER_NOT_EXISTED);
                    return ResponseHelper.error(Notification.MANAGER_NOT_EXISTED);
                }
                Spa spa = manager.getSpa();
                SpaService spaService = new SpaService();
                spaService.setName(spaServiceCreateRequest.getName());
                spaService.setDescription(spaServiceCreateRequest.getDescription());
                spaService.setPrice(spaServiceCreateRequest.getPrice());
                spaService.setStatus(spaServiceCreateRequest.getStatus());
                spaService.setType(spaServiceCreateRequest.getType());
                spaService.setDurationMin(spaServiceCreateRequest.getDurationMin());
                spaService.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                spaService.setCreateBy(spaServiceCreateRequest.getCreateBy().toString());
                spaService.setImage(imageLink);
                spaService.setSpa(spa);
                SpaService serviceResult = spaServiceService.insertNewSpaService(spaService);
                if (Objects.nonNull(serviceResult)) {
                    return ResponseHelper.ok(serviceResult);
                }
            } else {
                LOGGER.info(Notification.SAVE_IMAGE_FAILED);
            }
        }
        return ResponseHelper.error(Notification.SERVICE_CREATE_FAIL);
    }

    @PostMapping(value = "/spapackageservices/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewSpaPackageWithServices(SpaPackageCreateRequest spaPackage) {
        if (Objects.nonNull(spaPackage.getFile())) {
            String imageLink = UploadImage.uploadImage(spaPackage.getFile());
            if (imageLink != "") {
                List<SpaService> spaServices = new ArrayList<>();
                Category category = categoryService.findById(spaPackage.getCategoryId());
                if (Objects.isNull(category)) {
                    return ResponseHelper.error(Notification.CATEGORY_NOT_EXISTED);
                }
                Spa spa = spaService.findById(spaPackage.getSpaId());
                if (Objects.isNull(spa)) {
                    return ResponseHelper.error(Notification.SPA_NOT_EXISTED);
                }
                SpaPackage spaPackageInsert = new SpaPackage();
                spaPackageInsert.setName(spaPackage.getName());
                spaPackageInsert.setSpa(spa);
                spaPackageInsert.setCategory(category);
                spaPackageInsert.setCreate_by(spaPackage.getCreateBy());
                spaPackageInsert.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                spaPackageInsert.setDescription(spaPackage.getDescription());
                spaPackageInsert.setImage(imageLink);
                spaPackageInsert.setType(spaPackage.getType());
                spaPackageInsert.setStatus(spaPackage.getStatus());
                SpaPackage spaPackageResult = spaPackageService.insertNewSpaPackage(spaPackageInsert);
                if (Objects.isNull(spaPackageResult)) {
                    return ResponseHelper.error(Notification.SPA_PACKAGE_CREATE_FAIL);
                }
                for (Integer serviceId : spaPackage.getListSpaServiceId()) {
                    swp490.spa.entities.SpaService spaService = spaServiceService.findBySpaId(serviceId);
                    if (Objects.isNull(spaService)) {
                        ResponseHelper.error(Notification.SPA_SERVICE_NOT_EXISTED);
                    }
                    spaServices.add(spaService);
                }
                spaPackageResult.addListService(spaServices);
                if (Objects.nonNull(spaPackageService.insertNewSpaPackage(spaPackageResult))) {
                    return ResponseHelper.ok(Notification.SPA_PACKAGE_SERVICE_INSERT_SUCCESS);
                }
            }
        }
        return ResponseHelper.error(Notification.SPA_PACKAGE_SERVICE_INSERT_FAIL);
    }

    @PostMapping("/spatreatmentservices/insert")
    public Response insertNewSpaPackageWithServices(@RequestBody SpaTreatmentCreateRequest spaTreatmentRequest) {
        List<TreatmentService> treatmentServices = new ArrayList<>();
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaTreatmentRequest.getPackageId());
        if (Objects.isNull(spaPackage)) {
            return ResponseHelper.error(Notification.SPA_PACKAGE_NOT_EXISTED);
        }
        Spa spa = spaService.findById(spaTreatmentRequest.getSpaId());
        if (Objects.isNull(spa)) {
            return ResponseHelper.error(Notification.SPA_NOT_EXISTED);
        }
        int ordinal = 1;
        int totalTime = 0;
        double totalPrice = 0.0;
        for (int i = 0; i < spaTreatmentRequest.getListSpaServiceId().size(); i++) {
            swp490.spa.entities.SpaService spaService =
                    spaServiceService.findBySpaId(spaTreatmentRequest.getListSpaServiceId().get(i));
            if (Objects.isNull(spaService)) {
                ResponseHelper.error(Notification.SPA_SERVICE_NOT_EXISTED);
            }
            TreatmentService treatmentService = new TreatmentService(spaService, ordinal);
            ordinal++;
            treatmentServices.add(treatmentService);
            totalTime = totalTime + spaService.getDurationMin();
            totalPrice = totalPrice + spaService.getPrice();
        }
        SpaTreatment spaTreatmentInsert = new SpaTreatment(spaTreatmentRequest.getName(),
                spaTreatmentRequest.getDescription(), totalTime,
                Date.valueOf(LocalDateTime.now().toLocalDate()),
                spaTreatmentRequest.getCreateBy(),
                spaPackage, spa, treatmentServices, totalPrice);
        if (Objects.nonNull(spaTreatmentService.insertNewSpaTreatment(spaTreatmentInsert))) {
            return ResponseHelper.ok(Notification.SPA_TREATMENT_SERVICE_INSERT_SUCCESS);
        }
        return ResponseHelper.error(Notification.SPA_TREATMENT_SERVICE_INSERT_FAIL);
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account) {
        Manager manager = managerService.findManagerById(account.getId());
        User oldUser = manager.getUser();
        User updateUser = manager.getUser();
        updateUser.setPassword(account.getPassword());
        if (Objects.nonNull(userService.editUser(updateUser))) {
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }

    }

    @PutMapping("/dateoff/edit")
    public Response verifyDateBook(@RequestBody List<DateOff> dateOffList) {
        boolean isError = false;
        for (DateOff dateOff : dateOffList) {
            DateOff dateOffResult = dateOffService.findDateOffById(dateOff.getId());
            if (Objects.isNull(dateOffResult)) {
                LOGGER.info(dateOff.getId() + "is not existed");
                isError = true;
            } else {
                dateOffResult.setStatusDateOff(dateOff.getStatusDateOff());
                if (dateOff.getReasonCancel() != "" || dateOff.getReasonCancel().isEmpty()) {
                    dateOffResult.setReasonCancel(dateOff.getReasonCancel());
                }
            }
            if (Objects.isNull(dateOffService.editDateOff(dateOffResult))) {
                LOGGER.info(dateOff.getId() + "edit failed");
                isError = true;
            }
        }
        if (isError) {
            return ResponseHelper.error(Notification.DATEOFF_EDIT_FAILED);
        }
        return ResponseHelper.ok(Notification.DATEOFF_EDIT_SUCCESS);
    }

    @PutMapping("/category/edit")
    public Response editCategory(@RequestBody Category category) {
        Category categoryEdit = categoryService.findById(category.getId());
        if(Objects.nonNull(categoryEdit)){
            if(Objects.nonNull(category.getName())){
                categoryEdit.setName(category.getName());
            }
            if(Objects.nonNull(category.getStatus())){
                categoryEdit.setStatus(category.getStatus());
            }
            if(Objects.nonNull(category.getDescription())){
                categoryEdit.setDescription(category.getDescription());
            }
            if(Objects.nonNull(category.getIcon())){
                categoryEdit.setIcon(category.getIcon());
            }
            if(Objects.nonNull(category.getSpa())){
                categoryEdit.setSpa(category.getSpa());
            }
            Category categoryResult = categoryService.editByCategoryId(categoryEdit);
            if (Objects.nonNull(categoryResult)) {
                LOGGER.info(categoryEdit + " " + Notification.EDIT_CATEGORY_SUCCESS);
                return ResponseHelper.ok(categoryResult);
            }
        }
        LOGGER.info(categoryEdit + " " + Notification.EDIT_CATEGORY_FAILED);
        return ResponseHelper.error(Notification.EDIT_CATEGORY_FAILED);
    }

    @PutMapping("/spapackage/edit")
    public Response editSpaPackage(@RequestBody SpaPackage spaPackage) {
        SpaPackage spaPackageEdit = spaPackageService.findBySpaPackageId(spaPackage.getId());{
            if(Objects.nonNull(spaPackageEdit)){
                if(Objects.nonNull(spaPackage.getName())){
                    spaPackageEdit.setName(spaPackage.getName());
                }
                if(Objects.nonNull(spaPackage.getStatus())){
                    spaPackageEdit.setStatus(spaPackage.getStatus());
                }
                if(Objects.nonNull(spaPackage.getDescription())){
                    spaPackageEdit.setDescription(spaPackage.getDescription());
                }
                if(Objects.nonNull(spaPackage.getCategory())){
                    spaPackageEdit.setCategory(spaPackage.getCategory());
                }
                if(Objects.nonNull(spaPackage.getImage())){
                    spaPackageEdit.setImage(spaPackage.getImage());
                }
                if(Objects.nonNull(spaPackage.getSpaServices())){
                    spaPackageEdit.setSpaServices(spaPackage.getSpaServices());
                }
                if(Objects.nonNull(spaPackage.getSpa())){
                    spaPackageEdit.setSpa(spaPackage.getSpa());
                }
                if(Objects.nonNull(spaPackage.getType())){
                    spaPackageEdit.setType(spaPackage.getType());
                }
                SpaPackage spaPackageResult = spaPackageService.editBySpaPackageId(spaPackageEdit);
                if (Objects.nonNull(spaPackageResult)) {
                    LOGGER.info(spaPackageEdit + " " + Notification.EDIT_PACKAGE_SUCCESS);
                    return ResponseHelper.ok(spaPackageResult);
                }
            }
        }

        LOGGER.info(spaPackageEdit + " " + Notification.EDIT_PACKAGE_FAILED);
        return ResponseHelper.error(Notification.EDIT_PACKAGE_FAILED);
    }

    @PutMapping("/spaservice/edit")
    public Response editSpaService(@RequestBody SpaService spaService) {
        SpaService spaServiceEdit = spaServiceService.findBySpaServiceId(spaService.getId());
        if(Objects.nonNull(spaServiceEdit)){
            if(Objects.nonNull(spaService.getName())){
                spaServiceEdit.setName(spaService.getName());
            }
            if(Objects.nonNull(spaService.getDescription())){
                spaServiceEdit.setDescription(spaService.getDescription());
            }
            if(Objects.nonNull(spaService.getDurationMin())){
                spaServiceEdit.setDurationMin(spaService.getDurationMin());
            }
            if(Objects.nonNull(spaService.getPrice())){
                spaServiceEdit.setPrice(spaService.getPrice());
            }
            if(Objects.nonNull(spaService.getImage())){
                spaServiceEdit.setImage(spaService.getImage());
            }
            if(Objects.nonNull(spaService.getType())){
                spaServiceEdit.setType(spaService.getType());
            }
            if(Objects.nonNull(spaService.getStatus())){
                spaServiceEdit.setStatus(spaService.getStatus());
            }
            if(Objects.nonNull(spaService.getSpaPackages())){
                spaServiceEdit.setSpaPackages(spaService.getSpaPackages());
            }
            if(Objects.nonNull(spaService.getSpa())){
                spaServiceEdit.setSpa(spaService.getSpa());
            }
            SpaService spaServiceResult = spaServiceService.editBySpaService(spaServiceEdit);
            if (Objects.nonNull(spaServiceResult)) {
                LOGGER.info(spaService + " " + Notification.EDIT_SERVICE_SUCCESS);
                return ResponseHelper.ok(spaServiceResult);
            }
        } else {
            LOGGER.info(Notification.SPA_SERVICE_NOT_EXISTED);
        }
        LOGGER.info(spaService + " " + Notification.EDIT_SERVICE_FAILED);
        return ResponseHelper.error(Notification.EDIT_SERVICE_FAILED);
    }

    @PutMapping("/spatreatment/edit")
    public Response editSpaTreatment(@RequestBody SpaTreatment spaTreatment) {
        SpaTreatment spaTreatmentEdit = spaTreatmentService.findByTreatmentId(spaTreatment.getId());
        if(Objects.nonNull(spaTreatmentEdit)){
            Double totalPrice = 0.0;
            Integer totalTime = 0;
            if(Objects.nonNull(spaTreatment.getName())){
                spaTreatmentEdit.setName(spaTreatment.getName());
            }
            if(Objects.nonNull(spaTreatment.getDescription())){
                spaTreatmentEdit.setDescription(spaTreatment.getDescription());
            }
            if(Objects.nonNull(spaTreatment.getTreatmentServices())){
                for (TreatmentService treatmentService : spaTreatment.getTreatmentServices()) {
                    totalPrice+=treatmentService.getSpaService().getPrice();
                    totalTime+=treatmentService.getSpaService().getDurationMin();
                }
                spaTreatmentEdit.setTotalPrice(totalPrice);
                spaTreatmentEdit.setTotalTime(totalTime);
            }
            SpaTreatment spaTreatmentResult = spaTreatmentService.editBySpaTreatment(spaTreatment);
            if (Objects.nonNull(spaTreatmentResult)) {
                LOGGER.info(spaTreatment + " " + Notification.EDIT_TREATMENT_SUCCESS);
                return ResponseHelper.ok(spaTreatmentResult);
            }
        }

        LOGGER.info(spaTreatment + " " + Notification.EDIT_TREATMENT_FAILED);
        return ResponseHelper.error(Notification.EDIT_TREATMENT_FAILED);
    }

    @PutMapping("/category/delete")
    public Response removeCategory(@RequestParam Integer categoryId) {
        Category categoryResult = categoryService.findById(categoryId);
        if (Objects.nonNull(categoryResult)) {
            categoryResult.setStatus(Status.DISABLE);
            if (categoryService.removeCategory(categoryResult)) {
                return ResponseHelper.ok(Notification.REMOVE_CATEGORY_SUCCESS);
            }
        }
        return ResponseHelper.ok(Notification.REMOVE_CATEGORY_FAILED);
    }

    @PutMapping("/spapackage/delete")
    public Response removeSpaPackage(@RequestParam Integer packageId) {
        SpaPackage spaPackageResult = spaPackageService.findBySpaPackageId(packageId);
        if (Objects.nonNull(spaPackageResult)) {
            spaPackageResult.setStatus(Status.DISABLE);
            if (spaPackageService.removeCategory(spaPackageResult)) {
                return ResponseHelper.ok(Notification.REMOVE_PACKAGE_SUCCESS);
            }
        }
        return ResponseHelper.ok(Notification.REMOVE_PACKAGE_FAILED);
    }

    @PutMapping("/spaservice/delete")
    public Response removeSpaService(@RequestParam Integer spaServiceId) {
        SpaService spaServiceResult = spaServiceService.findBySpaServiceId(spaServiceId);
        if (Objects.nonNull(spaServiceResult)) {
            spaServiceResult.setStatus(Status.DISABLE);
            if (spaServiceService.removeService(spaServiceResult)) {
                return ResponseHelper.ok(Notification.REMOVE_SERVICE_SUCCESS);
            }
        }
        return ResponseHelper.ok(Notification.REMOVE_SERVICE_FAILED);
    }
}
