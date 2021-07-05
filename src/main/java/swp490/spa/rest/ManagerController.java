package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.*;
import swp490.spa.dto.responses.BookingBookingDetailResponse;
import swp490.spa.dto.responses.CategorySpaPackageResponse;
import swp490.spa.dto.responses.SpaPackageTreatmentResponse;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.entities.SpaService;
import swp490.spa.services.*;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.image.UploadImage;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.*;

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
    @Autowired
    private TreatmentServiceService treatmentServiceService;
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    @Autowired
    private StaffService staffService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public ManagerController(ManagerService managerService, SpaServiceService spaServiceService,
                             SpaPackageService spaPackageService, SpaTreatmentService spaTreatmentService,
                             swp490.spa.services.SpaService spaService, UserService userService,
                             DateOffService dateOffService, BookingService bookingService,
                             BookingDetailService bookingDetailService, StaffService staffService,
                             TreatmentServiceService treatmentServiceService,
                             BookingDetailStepService bookingDetailStepService) {
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
        this.spaPackageService = spaPackageService;
        this.spaTreatmentService = spaTreatmentService;
        this.spaService = spaService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.bookingDetailService = bookingDetailService;
        this.staffService = staffService;
        this.treatmentServiceService = treatmentServiceService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
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
        long totalItem = spaTreatmentService.findByPackageId(packageId, Constant.SEARCH_NO_CONTENT,
                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                .getTotalElements();
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(packageId, search, pageable);
        if (!spaTreatments.hasContent() && !spaTreatments.isFirst()) {
            spaTreatments = spaTreatmentService.findByPackageId(packageId, search,
                    PageRequest.of(spaTreatments.getTotalPages() - 1, spaTreatments.getSize(), spaTreatments.getSort()));
        }
        List<SpaTreatment> spaTreatmentList = new ArrayList<>();
        List<SpaTreatment> spaTreatmentCheckList = spaTreatments.getContent();
        for (int i = 0; i < spaTreatmentCheckList.size(); i++) {
            SpaTreatment spaTreatmentCheck = spaTreatmentCheckList.get(i);
            List<TreatmentService> treatmentServices =
                    new ArrayList<>(spaTreatmentCheck.getTreatmentServices());
            spaTreatmentCheck.setTreatmentServices(new TreeSet<>());
            Collections.sort(treatmentServices);
            for (TreatmentService treatmentService : treatmentServices) {
                spaTreatmentCheck.getTreatmentServices().add(treatmentService);
            }
            spaTreatmentList.add(spaTreatmentCheck);
            System.out.println("aaaaaaaaaaaaa");
        }
        Page<SpaTreatment> pageResult = new PageImpl<>(spaTreatmentList, pageable, totalItem);
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(pageResult));
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
        long totalItem =
                spaPackageService.findSpaPackageBySpaIdAndStatusAvailable(spaId,
                        Constant.SEARCH_NO_CONTENT,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                        .getTotalElements();
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
                                                   @RequestParam String search,
                                                   Pageable pageable) {
        List<Category> categories;
        long totalItem = 0;
        if (search == "") {
            totalItem = categoryService.findBySpaIdAndStatusNoSearch(spaId, status,
                    PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                    .getContent().size();
            categories = categoryService.findBySpaIdAndStatusNoSearch(spaId, status, pageable)
                    .getContent();
        } else {
            totalItem =
                    categoryService.findCategoryBySpaId(spaId, status, search,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                            .getContent().size();
            categories = categoryService.findCategoryBySpaId(spaId, status, search, pageable).getContent();
        }
        if (Objects.nonNull(categories)) {
            List<CategorySpaPackageResponse> categorySpaPackageResponses = new ArrayList<>();
            for (Category category : categories) {
                List<SpaPackage> spaPackages = spaPackageService.findByCategoryId(category.getId());
                CategorySpaPackageResponse cspr = new CategorySpaPackageResponse(category, spaPackages);
                categorySpaPackageResponses.add(cspr);
            }
            Page<CategorySpaPackageResponse> pageReturn =
                    new PageImpl<>(categorySpaPackageResponses, pageable,
                            totalItem);
            return ResponseHelper.ok(pageReturn);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CATEGORY));
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
                    LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
                }
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING));
        }
        Page<BookingBookingDetailResponse> page =
                new PageImpl<>(bookingResponses, pageable, totalElements);
        return ResponseHelper.ok(page);
    }

    @GetMapping("/dateoff/getalldateoffinrangedate")
    public Response findDateOffBySpaAndStatusInRangeDate(@RequestParam Integer spaId,
                                                         @RequestParam String fromDate,
                                                         @RequestParam String toDate,
                                                         @RequestParam StatusDateOff statusDateOff,
                                                         Pageable pageable) {
        Page<DateOff> dateOffs = dateOffService.findBySpaAndStatusInRangeDate(spaId,
                statusDateOff, Date.valueOf(fromDate), Date.valueOf(toDate), pageable);
        if (Objects.nonNull(dateOffs)) {
            LOGGER.info(String.format(LoggingTemplate.GET_SUCCESS, Constant.DATE_OFF));
            return ResponseHelper.ok(conversion.convertToPageDateOffResponse(dateOffs));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.DATE_OFF));
    }

    @GetMapping("/staff/findbyspa")
    public Response findStaffBySpaId(@RequestParam Integer spaId,
                                     @RequestParam String search,
                                     Pageable pageable) {
        Page<Staff> staffs = staffService.findBySpaIdAndNameLike(spaId, search, pageable);
        if (!staffs.hasContent() && !staffs.isFirst()) {
            staffs = staffService.findBySpaIdAndNameLike(spaId, search,
                    PageRequest.of(staffs.getTotalPages() - 1, staffs.getSize(), staffs.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageStaffResponse(staffs));
    }

    @GetMapping("/spaservices/findbyid/{packageId}")
    public Response findSpaServicesBySpaPackageId(@PathVariable Integer packageId){
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(packageId);
        if(Objects.nonNull(spaPackage)){
            List<SpaService> spaServices = spaPackage.getSpaServices();
            Page<SpaService> spaServicePage =
                    new PageImpl<>(spaServices,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                            spaServices.size());
            return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServicePage));
        }
        LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICES));
    }

    @GetMapping("/bookings/findbystatusandspaid/{spaId}")
    public Response findByStatusBookingAndSpaId(@PathVariable Integer spaId){
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByStatusAndSpaId(StatusBooking.BOOKING, spaId);
        if(Objects.nonNull(bookingDetailSteps)) {
            List<BookingDetail> bookingDetails = new ArrayList<>();
            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                BookingDetail bookingDetailCheck = bookingDetailStep.getBookingDetail();
                if (bookingDetails.size() == 0) {
                    bookingDetails.add(bookingDetailStep.getBookingDetail());
                } else {
                    if (!supportFunctions
                            .checkBookingDetailExistedInList(bookingDetailCheck, bookingDetails)) {
                        bookingDetails.add(bookingDetailStep.getBookingDetail());
                    }
                }
            }
            List<Booking> bookings = new ArrayList<>();
            for (int i = 0; i < bookingDetails.size(); i++) {
                Booking booking = bookingDetails.get(i).getBooking();
                if (i == 0) {
                    bookings.add(booking);
                } else {
                    if (!supportFunctions.checkBookingExistedInList(booking, bookings)) {
                        bookings.add(booking);
                    }
                }
            }
            Page<Booking> bookingPage =
                    new PageImpl<>(bookings,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()),
                            bookings.size());
            return ResponseHelper.ok(conversion.convertToPageBookingResponse(bookingPage));
        }
        LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING));
    }

    @GetMapping("/getstafflistfree/{bookingDetailId}")
    public Response getListStaffsFreeInOneDate(@PathVariable Integer bookingDetailId){
        List<Staff> staffListResult = new ArrayList<>();
        BookingDetail bookingDetail = bookingDetailService.findByBookingDetailId(bookingDetailId);
        if(Objects.nonNull(bookingDetail)){
            List<Staff> allStaffList =
                    staffService.findBySpaId(bookingDetail.getSpaPackage().getSpa().getId());
            if(Objects.nonNull(allStaffList)){
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByBookingDetail(bookingDetailId,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                        .getContent();
                Date dateBooking = bookingDetailSteps.get(0).getDateBooking();
                Time startTime = bookingDetailSteps.get(0).getStartTime();
                Time endTime = bookingDetailSteps.get(bookingDetailSteps.size()-1).getEndTime();
                for (Staff staff : allStaffList) {
                    List<BookingDetailStep> bookingDetailStepsCheck =
                            bookingDetailStepService.findByDateBookingAndStartEndTimeAndStaffId(dateBooking,
                                    startTime,endTime,staff.getUser().getId());
                    if(bookingDetailStepsCheck.size() == 0){
                        staffListResult.add(staff);
                    }
                }
                return ResponseHelper.ok(staffListResult);
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.LIST_STAFF_FREE));
    }

    @PostMapping(value = "/spaservice/insert", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response createNewSpaService(SpaServiceRequest spaServiceRequest) {
        if (Objects.nonNull(spaServiceRequest.getFile())) {
            String imageLink = UploadImage.uploadImage(spaServiceRequest.getFile());
            if (imageLink != "") {
                Manager manager = managerService.findManagerById(spaServiceRequest.getCreateBy());
                if (Objects.isNull(manager)) {
                    LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.MANAGER));
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.MANAGER));
                }
                Spa spa = manager.getSpa();
                SpaService spaService = new SpaService();
                spaService.setName(spaServiceRequest.getName());
                spaService.setDescription(spaServiceRequest.getDescription());
                spaService.setPrice(spaServiceRequest.getPrice());
                spaService.setStatus(spaServiceRequest.getStatus());
                spaService.setType(spaServiceRequest.getType());
                spaService.setDurationMin(spaServiceRequest.getDurationMin());
                spaService.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                spaService.setCreateBy(spaServiceRequest.getCreateBy().toString());
                spaService.setImage(imageLink);
                spaService.setSpa(spa);
                SpaService serviceResult = spaServiceService.insertNewSpaService(spaService);
                if (Objects.nonNull(serviceResult)) {
                    return ResponseHelper.ok(serviceResult);
                }
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.SERVICE));
    }

    @PostMapping(value = "/spapackageservices/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewSpaPackageWithServices(SpaPackageRequest spaPackage) {
        if (Objects.nonNull(spaPackage.getFile())) {
            String imageLink = UploadImage.uploadImage(spaPackage.getFile());
            if (imageLink != "") {
                List<SpaService> spaServices = new ArrayList<>();
                Category category = categoryService.findById(spaPackage.getCategoryId());
                if (Objects.isNull(category)) {
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CATEGORY));
                }
                Spa spa = spaService.findById(spaPackage.getSpaId());
                if (Objects.isNull(spa)) {
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA));
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
                    return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.SPA_PACKAGE_SERVICE));
                }
                for (Integer serviceId : spaPackage.getListSpaServiceId()) {
                    swp490.spa.entities.SpaService spaService = spaServiceService.findBySpaId(serviceId);
                    if (Objects.isNull(spaService)) {
                        ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
                    }
                    spaServices.add(spaService);
                }
                spaPackageResult.addListService(spaServices);
                if (Objects.nonNull(spaPackageService.insertNewSpaPackage(spaPackageResult))) {
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.SPA_PACKAGE_SERVICE));
                }
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.SPA_PACKAGE_SERVICE));
    }

    @PostMapping("/spatreatmentservices/insert")
    public Response insertNewSpaPackageWithServices(@RequestBody SpaTreatmentRequest spaTreatmentRequest) {
        List<TreatmentService> treatmentServices = new ArrayList<>();
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaTreatmentRequest.getPackageId());
        if (Objects.isNull(spaPackage)) {
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        }
        Spa spa = spaService.findById(spaTreatmentRequest.getSpaId());
        if (Objects.isNull(spa)) {
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA));
        }
        int ordinal = 1;
        int totalTime = 0;
        double totalPrice = 0.0;
        for (int i = 0; i < spaTreatmentRequest.getListSpaServiceId().size(); i++) {
            swp490.spa.entities.SpaService spaService =
                    spaServiceService.findBySpaId(spaTreatmentRequest.getListSpaServiceId().get(i));
            if (Objects.isNull(spaService)) {
                ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
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
            for (TreatmentService treatmentService : treatmentServices) {
                treatmentService.setSpaTreatment(spaTreatmentInsert);
                TreatmentService treatmentServiceResult =
                        treatmentServiceService.insertNewTreatmentService(treatmentService);
                if (Objects.isNull(treatmentServiceResult)) {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED,Constant.TREATMENT_SERVICE));
                    return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED,Constant.TREATMENT_SERVICE));
                } else {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS,Constant.TREATMENT_SERVICE));
                }
            }
            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,Constant.TREATMENT_SERVICE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED,Constant.TREATMENT_SERVICE));
    }

    @PostMapping(value = "/category/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewCategory(CategoryRequest categoryRequest) {
        if (Objects.nonNull(categoryRequest.getFile())) {
            String imageLink = UploadImage.uploadImage(categoryRequest.getFile());
            if (imageLink != "") {
                Spa spa = spaService.findById(categoryRequest.getSpaId());
                if (Objects.nonNull(spa)) {
                    Category categoryNew = new Category();
                    categoryNew.setName(categoryRequest.getName());
                    categoryNew.setIcon(imageLink);
                    categoryNew.setDescription(categoryRequest.getDescription());
                    categoryNew.setStatus(Status.AVAILABLE);
                    categoryNew.setCreateBy(categoryRequest.getCreateBy());
                    categoryNew.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                    categoryNew.setSpa(spa);
                    Category categoryResult = categoryService.insertNewCategory(categoryNew);
                    if (Objects.nonNull(categoryResult)) {
                        LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.CATEGORY));
                        return ResponseHelper.ok(categoryResult);
                    }
                    LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.CATEGORY));
                } else {
                    LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SPA));
                }
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.CATEGORY));
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
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.PASSWORD));
        }
    }

    @PutMapping("/dateoff/edit")
    public Response verifyDateBook(@RequestBody List<DateOff> dateOffList) {
        boolean isError = false;
        for (DateOff dateOff : dateOffList) {
            DateOff dateOffResult = dateOffService.findDateOffById(dateOff.getId());
            if (Objects.isNull(dateOffResult)) {
                LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.DATE_OFF));
                isError = true;
            } else {
                dateOffResult.setStatusDateOff(dateOff.getStatusDateOff());
                if (dateOff.getReasonCancel() != "" || dateOff.getReasonCancel().isEmpty()) {
                    dateOffResult.setReasonCancel(dateOff.getReasonCancel());
                }
            }
            if (Objects.isNull(dateOffService.editDateOff(dateOffResult))) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.DATE_OFF));
                isError = true;
            }
        }
        if (isError) {
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.DATE_OFF));
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.DATE_OFF));
    }

    @PutMapping(value = "/category/edit/{categoryId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editCategory(@PathVariable Integer categoryId,
                                 CategoryRequest category) {
        Category categoryEdit = categoryService.findById(categoryId);
        if (Objects.nonNull(categoryEdit)) {
            if (Objects.nonNull(category.getFile())) {
                String iconLink = UploadImage.uploadImage(category.getFile());
                if (iconLink != "") {
                    categoryEdit.setIcon(iconLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(category.getName())) {
                categoryEdit.setName(category.getName());
            }
            if (Objects.nonNull(category.getDescription())) {
                categoryEdit.setDescription(category.getDescription());
            }
            Category categoryResult = categoryService.editByCategoryId(categoryEdit);
            if (Objects.nonNull(categoryResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.CATEGORY));
                return ResponseHelper.ok(categoryResult);
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.CATEGORY));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CATEGORY));
    }

    @PutMapping(value = "/spapackage/edit/{packageId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editSpaPackage(@PathVariable Integer packageId,
                                   SpaPackageRequest spaPackage) {
        SpaPackage spaPackageEdit = spaPackageService.findBySpaPackageId(packageId);
        if (Objects.nonNull(spaPackageEdit)) {
            if (Objects.nonNull(spaPackage.getFile())) {
                String imageLink = UploadImage.uploadImage(spaPackage.getFile());
                if (imageLink != "") {
                    spaPackageEdit.setImage(imageLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(spaPackage.getName())) {
                spaPackageEdit.setName(spaPackage.getName());
            }
            if (Objects.nonNull(spaPackage.getDescription())) {
                spaPackageEdit.setDescription(spaPackage.getDescription());
            }
            SpaPackage spaPackageResult = spaPackageService.editBySpaPackageId(spaPackageEdit);
            if (Objects.nonNull(spaPackageResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.SPA_PACKAGE));
                return ResponseHelper.ok(spaPackageResult);
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.SPA_PACKAGE));
    }

    @PutMapping(value = "/spaservice/edit/{spaServiceId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editSpaService(@PathVariable Integer spaServiceId, SpaServiceRequest spaService) {
        SpaService spaServiceEdit = spaServiceService.findBySpaServiceId(spaServiceId);
        if (Objects.nonNull(spaServiceEdit)) {
            if (Objects.nonNull(spaService.getFile())) {
                String imageLink = UploadImage.uploadImage(spaService.getFile());
                if (imageLink != "") {
                    spaServiceEdit.setImage(imageLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(spaService.getName())) {
                spaServiceEdit.setName(spaService.getName());
            }
            if (Objects.nonNull(spaService.getDescription())) {
                spaServiceEdit.setDescription(spaService.getDescription());
            }
            if (Objects.nonNull(spaService.getDurationMin())) {
                spaServiceEdit.setDurationMin(spaService.getDurationMin());
            }
            if (Objects.nonNull(spaService.getPrice())) {
                spaServiceEdit.setPrice(spaService.getPrice());
            }
            SpaService spaServiceResult = spaServiceService.editBySpaService(spaServiceEdit);
            if (Objects.nonNull(spaServiceResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.SERVICE));
                return ResponseHelper.ok(spaServiceResult);
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.SERVICE));
    }

    @PutMapping("/spatreatment/edit/{spaTreatmentId}")
    public Response editSpaTreatment(@PathVariable Integer spaTreatmentId,
                                     @RequestBody SpaTreatmentRequest spaTreatment) {
        SpaTreatment spaTreatmentEdit = spaTreatmentService.findByTreatmentId(spaTreatmentId);
        if (Objects.nonNull(spaTreatmentEdit)) {
            if (Objects.nonNull(spaTreatment.getName())) {
                spaTreatmentEdit.setName(spaTreatment.getName());
            }
            if (Objects.nonNull(spaTreatment.getDescription())) {
                spaTreatmentEdit.setDescription(spaTreatment.getDescription());
            }
            SpaTreatment spaTreatmentResult = spaTreatmentService.editBySpaTreatment(spaTreatmentEdit);
            if (Objects.nonNull(spaTreatmentResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.SPA_TREATMENT));
                return ResponseHelper.ok(spaTreatmentResult);
            }
        }
        LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.SPA_TREATMENT));
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.SPA_TREATMENT));
    }

    @PutMapping("/category/delete/{categoryId}")
    public Response removeCategory(@PathVariable Integer categoryId) {
        Category categoryResult = categoryService.findById(categoryId);
        if (Objects.nonNull(categoryResult)) {
            categoryResult.setStatus(Status.DISABLE);
            if (categoryService.removeCategory(categoryResult)) {
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS,Constant.CATEGORY));
            }
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_FAILED, Constant.CATEGORY));
    }

    @PutMapping("/spapackage/delete/{packageId}")
    public Response removeSpaPackage(@PathVariable Integer packageId) {
        SpaPackage spaPackageResult = spaPackageService.findBySpaPackageId(packageId);
        if (Objects.nonNull(spaPackageResult)) {
            spaPackageResult.setStatus(Status.DISABLE);
            if (spaPackageService.removeCategory(spaPackageResult)) {
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS, Constant.SPA_PACKAGE));
            }
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_FAILED, Constant.SPA_PACKAGE));
    }

    @PutMapping("/spaservice/delete/{spaServiceId}")
    public Response removeSpaService(@PathVariable Integer spaServiceId) {
        SpaService spaServiceResult = spaServiceService.findBySpaServiceId(spaServiceId);
        if (Objects.nonNull(spaServiceResult)) {
            spaServiceResult.setStatus(Status.DISABLE);
            if (spaServiceService.removeService(spaServiceResult)) {
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS, Constant.SERVICE));
            }
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_FAILED, Constant.SERVICE));
    }

    @PutMapping("/bookingdetailstep/editstafftypeonestep/{bookingDetailId}/{staffId}")
    public Response addStaffIntoBookingDetailTypeOneStep(@PathVariable Integer bookingDetailId,
                                                         @PathVariable Integer staffId){
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByBookingDetail(bookingDetailId,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                .getContent();
        List<BookingDetailStep> bookingDetailStepEdited = new ArrayList<>();
        if(bookingDetailSteps.size()!= 0 || Objects.nonNull(bookingDetailSteps)){
            Staff staff = staffService.findByStaffId(staffId);
            if(Objects.nonNull(staff)){
                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                    bookingDetailStep.setStaff(staff);
                    bookingDetailStep.setStatusBooking(StatusBooking.START);
                }
                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                    BookingDetailStep bookingDetailStepResult =
                            bookingDetailStepService.editBookingDetailStep(bookingDetailStep);
                    if(Objects.isNull(bookingDetailStepResult)){
                       LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL_STEP));
                    } else {
                        bookingDetailStepEdited.add(bookingDetailStepResult);
                    }
                }
                if(bookingDetailSteps.size() == bookingDetailStepEdited.size()){
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.STAFF));
                }
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.STAFF));
    }
}
