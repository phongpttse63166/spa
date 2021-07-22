package swp490.spa.rest;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.*;
import swp490.spa.dto.responses.*;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.entities.SpaService;
import swp490.spa.services.*;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.image.UploadImage;
import swp490.spa.utils.support.templates.MessageTemplate;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private ConsultationContentService consultationContentService;
    @Autowired
    private NotificationFireBaseService notificationFireBaseService;
    @Autowired
    private NotificationService notificationService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public ManagerController(ManagerService managerService, SpaServiceService spaServiceService,
                             SpaPackageService spaPackageService, SpaTreatmentService spaTreatmentService,
                             swp490.spa.services.SpaService spaService, UserService userService,
                             DateOffService dateOffService, BookingService bookingService,
                             BookingDetailService bookingDetailService, StaffService staffService,
                             TreatmentServiceService treatmentServiceService, ConsultantService consultantService,
                             BookingDetailStepService bookingDetailStepService,
                             ConsultationContentService consultationContentService,
                             NotificationFireBaseService notificationFireBaseService,
                             NotificationService notificationService) {
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
        this.consultantService = consultantService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.consultationContentService = consultationContentService;
        this.notificationFireBaseService = notificationFireBaseService;
        this.notificationService = notificationService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
    }

    @GetMapping("/spaPackage/findByStatus")
    public Response findSpaPackageBySpaId(@RequestParam Status status,
                                          @RequestParam String search,
                                          Pageable pageable) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageByStatus(status, search, pageable);
        if (!spaPackages.hasContent() && !spaPackages.isFirst()) {
            spaPackages =
                    spaPackageService.findSpaPackageByStatus(status, search,
                            PageRequest.of(spaPackages.getTotalPages() - 1,
                                    spaPackages.getSize(), spaPackages.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @GetMapping("/spaTreatment/findByPackageId")
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
        }
        Page<SpaTreatment> pageResult = new PageImpl<>(spaTreatmentList, pageable, totalItem);
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(pageResult));
    }

    @GetMapping("/spaService/findByStatus")
    public Response findSpaServiceByStatus(@RequestParam Status status,
                                           @RequestParam String search,
                                           Pageable pageable) {
        Page<swp490.spa.entities.SpaService> spaServices =
                spaServiceService.findByStatus(status, search, pageable);
        if (!spaServices.hasContent() && !spaServices.isFirst()) {
            spaServices =
                    spaServiceService.findByStatus(status, search,
                            PageRequest.of(spaServices.getTotalPages() - 1,
                                    spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @GetMapping("/spaPackage/findByServiceId")
    public Response findSpaPackageBySpaServiceId(@RequestParam Integer spaServiceId,
                                                 @RequestParam Integer page,
                                                 @RequestParam Integer size,
                                                 @RequestParam String search) {
        Page<SpaPackage> spaPackages =
                spaPackageService.findAllBySpaServiceId(spaServiceId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaPackageResponse(spaPackages));
    }

    @GetMapping("/spatreatment/findbyserviceId")
    public Response findSpaTreatmentBySpaServiceId(@RequestParam Integer spaServiceId,
                                                   @RequestParam Integer page,
                                                   @RequestParam Integer size,
                                                   @RequestParam String search) {
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findAllBySpaServiceId(spaServiceId, page, size, search);
        return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
    }

    @GetMapping("/spaPackageTreatment/findByStatus")
    public Response findSpaPackageTreatmentByStatus(@RequestParam String search,
                                                    Pageable pageable) {
        List<SpaPackageTreatmentResponse> result = new ArrayList<>();
        Page<SpaPackage> spaPackages =
                spaPackageService.findSpaPackageByStatus(Status.AVAILABLE, search, pageable);
        long totalItem =
                spaPackageService.findSpaPackageByStatus(Status.AVAILABLE,
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

    @GetMapping("/spaService/findByType")
    public Response findSpaServiceByType(@RequestParam Type type,
                                         @RequestParam String search,
                                         Pageable pageable) {
        Page<SpaService> spaServices = spaServiceService.findByType(type, search, pageable);
        if (!spaServices.hasContent() && !spaServices.isFirst()) {
            spaServices =
                    spaServiceService.findByType(type, search,
                            PageRequest.of(spaServices.getTotalPages() - 1,
                                    spaServices.getSize(), spaServices.getSort()));
        }
        return ResponseHelper.ok(conversion.convertToPageSpaServiceResponse(spaServices));
    }

    @GetMapping("/categorySpaPackages/findByStatus")
    public Response findCategorySpaPackagesByStatus(@RequestParam Status status,
                                                    @RequestParam String search,
                                                    Pageable pageable) {
        List<Category> categories;
        long totalItem = 0;
        if (search == "") {
            totalItem = categoryService.findAllByStatus(status,
                    PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                    .getContent().size();
            categories = categoryService.findAllByStatus(status, pageable)
                    .getContent();
        } else {
            totalItem =
                    categoryService.findCategoryByStatusAndName(status, search,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                            .getContent().size();
            categories =
                    categoryService.findCategoryByStatusAndName(status, search, pageable).getContent();
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

    @GetMapping("/getAllEmployeeBySpa/{spaId}")
    public Response findAllEmployeeBySpaId(@PathVariable Integer spaId,
                                           @RequestParam String search) {
        Map<String, List<User>> map = new HashMap<>();
        List<User> staffUser = new ArrayList<>();
        List<User> consultantUser = new ArrayList<>();
        List<Staff> staffs = staffService.findBySpaIdAndNameLike(spaId, search);
        List<Consultant> consultants = consultantService.findBySpaIdAndNameLike(spaId, search);
        for (Staff staff : staffs) {
            staffUser.add(staff.getUser());
        }
        map.put("Staff", staffUser);
        for (Consultant consultant : consultants) {
            consultantUser.add(consultant.getUser());
        }
        map.put("Consultant", consultantUser);
        return ResponseHelper.ok(map);
    }

    @GetMapping("/spaservices/findbyid/{packageId}")
    public Response findSpaServicesBySpaPackageId(@PathVariable Integer packageId) {
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(packageId);
        if (Objects.nonNull(spaPackage)) {
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
    public Response findByStatusBookingAndSpaId(@PathVariable Integer spaId) {
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByStatusAndSpaId(StatusBooking.PENDING, spaId);
        if (Objects.nonNull(bookingDetailSteps)) {
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
                if (booking.getStatusBooking().equals(StatusBooking.PENDING)) {
                    if (i == 0) {
                        bookings.add(booking);
                    } else {
                        if (!supportFunctions.checkBookingExistedInList(booking, bookings)) {
                            bookings.add(booking);
                        }
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

    @GetMapping("/getListStaffFree/{bookingDetailId}")
    public Response getListStaffsFree(@PathVariable Integer bookingDetailId) {
        List<Staff> staffListResult = new ArrayList<>();
        Date dateBooking = null;
        Time startTime = null;
        Time endTime = null;
        BookingDetail bookingDetail = bookingDetailService.findByBookingDetailId(bookingDetailId);
        if (Objects.nonNull(bookingDetail)) {
            Spa spa = bookingDetail.getBooking().getSpa();
            List<Staff> allStaffList =
                    staffService.findBySpaIdAndStatusAvailable(spa.getId());
            if (Objects.nonNull(allStaffList)) {
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByBookingDetail(bookingDetailId,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                                .getContent();
                if (bookingDetail.getType().equals(Type.ONESTEP)) {
                    dateBooking = bookingDetailSteps.get(0).getDateBooking();
                    startTime = bookingDetailSteps.get(0).getStartTime();
                    endTime = bookingDetailSteps.get(bookingDetailSteps.size() - 1).getEndTime();
                } else {
                    dateBooking = bookingDetailSteps.get(1).getDateBooking();
                    startTime = bookingDetailSteps.get(1).getStartTime();
                    endTime = bookingDetailSteps.get(1).getEndTime();
                }
                List<DateOff> dateOffs =
                        dateOffService.findByDateOffAndSpaAndStatusApprove(dateBooking, spa.getId());
                if (dateOffs.size() != 0) {
                    List<Staff> staffListNotDateOff = new ArrayList<>();
                    for (Staff staff : allStaffList) {
                        boolean checkDateOff = false;
                        for (DateOff dateOff : dateOffs) {
                            if (staff.getUser().equals(dateOff.getEmployee())) {
                                checkDateOff = true;
                            }
                        }
                        if (!checkDateOff) {
                            staffListNotDateOff.add(staff);
                        }
                    }
                    allStaffList = staffListNotDateOff;
                }
                for (Staff staff : allStaffList) {
                    List<BookingDetailStep> bookingDetailStepsCheck =
                            bookingDetailStepService.findByDateBookingAndStartEndTimeAndStaffId(dateBooking,
                                    startTime, endTime, staff.getUser().getId());
                    if (bookingDetailStepsCheck.size() == 0) {
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

    @GetMapping("/getlistconsultantfree/{bookingDetailId}")
    public Response getListConsultantFreeInOneDate(@PathVariable Integer bookingDetailId) {
        List<Consultant> consultantListResult = new ArrayList<>();
        BookingDetail bookingDetail = bookingDetailService.findByBookingDetailId(bookingDetailId);
        if (Objects.nonNull(bookingDetail)) {
            Spa spa = bookingDetail.getBooking().getSpa();
            List<Consultant> allConsultant =
                    consultantService.findBySpaIdAndStatusAvailable(spa.getId());
            if (Objects.nonNull(allConsultant)) {
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByBookingDetail(bookingDetailId,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                                .getContent();
                Date dateBooking = bookingDetailSteps.get(0).getDateBooking();
                Time startTime = bookingDetailSteps.get(0).getStartTime();
                Time endTime = bookingDetailSteps.get(bookingDetailSteps.size() - 1).getEndTime();
                for (Consultant consultant : allConsultant) {
                    List<BookingDetailStep> bookingDetailStepsCheck =
                            bookingDetailStepService.findByDateBookingAndStartEndTimeAndConsultantId(dateBooking,
                                    startTime, endTime, consultant.getUser().getId());
                    if (bookingDetailStepsCheck.size() == 0) {
                        consultantListResult.add(consultant);
                    }
                }
                return ResponseHelper.ok(consultantListResult);
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.LIST_CONSULTANT_FREE));
    }

    @GetMapping("/bookingDetail/findBySpaAndStaffIsNull")
    public Response findByBookingDetailStepHaveStaffNullAndSpa(@RequestParam Integer spaId) {
        List<BookingDetail> bookingDetails = new ArrayList<>();
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findBySpaAndStaffIsNull(spaId);
        for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
            BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
            if (bookingDetails.size() == 0) {
                bookingDetails.add(bookingDetailStep.getBookingDetail());
            } else {
                if (!supportFunctions.checkBookingDetailExistedInList(bookingDetail, bookingDetails)) {
                    bookingDetails.add(bookingDetail);
                }
            }
        }
        Page<BookingDetail> page = new PageImpl<>(bookingDetails,
                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                bookingDetails.size());
        return ResponseHelper.ok(page);
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
                SpaPackage spaPackageInsert = new SpaPackage();
                spaPackageInsert.setName(spaPackage.getName());
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
                    swp490.spa.entities.SpaService spaService = spaServiceService.findById(serviceId);
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
        int ordinal = 1;
        int totalTime = 0;
        double totalPrice = 0.0;
        for (int i = 0; i < spaTreatmentRequest.getListSpaServiceId().size(); i++) {
            swp490.spa.entities.SpaService spaService =
                    spaServiceService.findById(spaTreatmentRequest.getListSpaServiceId().get(i));
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
                spaPackage, treatmentServices, totalPrice);
        if (Objects.nonNull(spaTreatmentService.insertNewSpaTreatment(spaTreatmentInsert))) {
            for (TreatmentService treatmentService : treatmentServices) {
                treatmentService.setSpaTreatment(spaTreatmentInsert);
                TreatmentService treatmentServiceResult =
                        treatmentServiceService.insertNewTreatmentService(treatmentService);
                if (Objects.isNull(treatmentServiceResult)) {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.TREATMENT_SERVICE));
                    return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.TREATMENT_SERVICE));
                } else {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.TREATMENT_SERVICE));
                }
            }
            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.TREATMENT_SERVICE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.TREATMENT_SERVICE));
    }

    @PostMapping(value = "/category/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewCategory(CategoryRequest categoryRequest) {
        if (Objects.nonNull(categoryRequest.getFile())) {
            String imageLink = UploadImage.uploadImage(categoryRequest.getFile());
            if (imageLink != "") {
                Category categoryNew = new Category();
                categoryNew.setName(categoryRequest.getName());
                categoryNew.setIcon(imageLink);
                categoryNew.setDescription(categoryRequest.getDescription());
                categoryNew.setStatus(Status.AVAILABLE);
                categoryNew.setCreateBy(categoryRequest.getCreateBy());
                categoryNew.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                Category categoryResult = categoryService.insertNewCategory(categoryNew);
                if (Objects.nonNull(categoryResult)) {
                    LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.CATEGORY));
                    return ResponseHelper.ok(categoryResult);
                }
                LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.CATEGORY));
            } else {
                LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
            }
        } else {
            LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.CATEGORY));
    }

    @PostMapping(value = "/employee/insert",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response insertNewEmployee(EmployeeRequest employeeRequest) {
        User user = userService.findByPhone(employeeRequest.getPhone());
        Spa spa = spaService.findById(employeeRequest.getSpaId());
        String password = "";
        if (Objects.isNull(user)) {
            if (Objects.nonNull(employeeRequest.getFile())) {
                String imageLink = UploadImage.uploadImage(employeeRequest.getFile());
                user = new User();
                if (imageLink != "") {
                    password = RandomStringUtils.random(Constant.PASSWORD_LENGTH, true, true);
                    user.setFullname(employeeRequest.getFullname());
                    user.setPhone(employeeRequest.getPhone());
                    user.setGender(employeeRequest.getGender());
                    user.setBirthdate(employeeRequest.getBirthdate());
                    user.setAddress(employeeRequest.getAddress());
                    user.setEmail(employeeRequest.getEmail());
                    user.setActive(true);
                    user.setImage(imageLink);
                    user.setPassword(password);
                    User userResult = userService.insertNewUser(user);
                    if (Objects.nonNull(userResult)) {
                        if (employeeRequest.getRole().equals(Role.STAFF)) {
                            Staff staff = new Staff();
                            staff.setUser(userResult);
                            staff.setSpa(spa);
                            staff.setStatus(Status.AVAILABLE);
                            Staff staffResult = staffService.insertNewStaff(staff);
                            if (Objects.nonNull(staffResult)) {
                                return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.EMPLOYEE));
                            }
                        } else {
                            Consultant consultant = new Consultant();
                            consultant.setUser(userResult);
                            consultant.setSpa(spa);
                            consultant.setStatus(Status.AVAILABLE);
                            Consultant consultantResult = consultantService.insertNewConsultant(consultant);
                            if (Objects.nonNull(consultantResult)) {
                                return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.EMPLOYEE));
                            }
                        }
                    }
                }
            } else {
                LOGGER.info(LoggingTemplate.FILE_NOT_EXISTED);
                return ResponseHelper.error(LoggingTemplate.FILE_NOT_EXISTED);
            }
        } else {
            if (employeeRequest.getRole().equals(Role.STAFF)) {
                Consultant consultant = consultantService.findByConsultantId(user.getId());
                if (consultant == null) {
                    Staff staff = staffService.findByStaffId(user.getId());
                    if (Objects.isNull(staff)) {
                        staff = new Staff();
                        staff.setUser(user);
                        staff.setSpa(spa);
                        staff.setStatus(Status.AVAILABLE);
                        Staff staffResult = staffService.insertNewStaff(staff);
                        if (Objects.nonNull(staffResult)) {
                            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.EMPLOYEE));
                        }
                    }
                    return ResponseHelper.error(String.format(LoggingTemplate.EMPLOYEE_EXISTED));
                } else {
                    return ResponseHelper.error(String.format(LoggingTemplate.EMPLOYEE_HAS_OTHER_ROLE));
                }
            }
            if (employeeRequest.getRole().equals(Role.CONSULTANT)) {
                Staff staff = staffService.findByStaffId(user.getId());
                if (staff == null) {
                    Consultant consultant = consultantService.findByConsultantId(user.getId());
                    if (Objects.isNull(consultant)) {
                        consultant = new Consultant();
                        consultant.setUser(user);
                        consultant.setSpa(spa);
                        consultant.setStatus(Status.AVAILABLE);
                        Consultant consultantResult = consultantService.insertNewConsultant(consultant);
                        if (Objects.nonNull(consultantResult)) {
                            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.EMPLOYEE));
                        }
                    }
                    return ResponseHelper.error(String.format(LoggingTemplate.EMPLOYEE_EXISTED));
                } else {
                    return ResponseHelper.error(String.format(LoggingTemplate.EMPLOYEE_HAS_OTHER_ROLE));
                }
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.EMPLOYEE));
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
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS, Constant.CATEGORY));
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

    @PutMapping("/bookingDetailStep/addStaff/{bookingDetailId}/{staffId}")
    public Response addStaffIntoBookingDetail(@PathVariable Integer bookingDetailId,
                                              @PathVariable Integer staffId) throws FirebaseMessagingException {
        List<BookingDetailStep> bookingDetailStepEdited = new ArrayList<>();
        int count = 0;
        boolean check = true;
        Booking bookingEdited = null;
        BookingDetail bookingDetailEdited = null;
        BookingDetail bookingDetailGet =
                bookingDetailService.findByBookingDetailId(bookingDetailId);
        if (Objects.nonNull(bookingDetailGet)) {
            List<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByBookingDetail(bookingDetailId,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                            .getContent();
            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                if (bookingDetailStep.getStaff() != null) {
                    count++;
                }
            }
            if (count == bookingDetailSteps.size()) {
                return ResponseHelper.error(String.format(LoggingTemplate.EMPLOYEE_ASSIGNED));
            } else {
                if (bookingDetailSteps.size() != 0 || Objects.nonNull(bookingDetailSteps)) {
                    Staff staff = staffService.findByStaffId(staffId);
                    if (Objects.nonNull(staff)) {
                        if (bookingDetailGet.getType().equals(Type.ONESTEP)) {
                            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                                bookingDetailStep.setStaff(staff);
                                bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                            }
                            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                                BookingDetailStep bookingDetailStepResult =
                                        bookingDetailStepService.editBookingDetailStep(bookingDetailStep);
                                if (Objects.isNull(bookingDetailStepResult)) {
                                    check = false;
                                    LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL_STEP));
                                    bookingDetailStepEdited.add(bookingDetailStep);
                                } else {
                                    bookingDetailStepEdited.add(bookingDetailStepResult);
                                }
                            }
                        } else {
                            for (int i = 0; i < bookingDetailSteps.size(); i++) {
                                if (i != 0) {
                                    if (i == 1) {
                                        bookingDetailSteps.get(i).setStatusBooking(StatusBooking.BOOKING);
                                    }
                                    bookingDetailSteps.get(i).setStaff(staff);
                                }
                            }
                            for (int i = 0; i < bookingDetailSteps.size(); i++) {
                                if (i != 0) {
                                    BookingDetailStep bookingDetailStepResult =
                                            bookingDetailStepService.editBookingDetailStep(bookingDetailSteps.get(i));
                                    if (Objects.isNull(bookingDetailStepResult)) {
                                        check = false;
                                        LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL_STEP));
                                        bookingDetailStepEdited.add(bookingDetailSteps.get(i));
                                    } else {
                                        bookingDetailStepEdited.add(bookingDetailStepResult);
                                    }
                                } else {
                                    bookingDetailStepEdited.add(bookingDetailSteps.get(i));
                                }
                            }
                        }
                        if (bookingDetailSteps.size() == bookingDetailStepEdited.size()) {
                            bookingDetailGet.setStatusBooking(StatusBooking.BOOKING);
                            bookingDetailEdited =
                                    bookingDetailService.editBookingDetail(bookingDetailGet);
                            if (Objects.nonNull(bookingDetailEdited)) {
                                Integer countBookingDetail =
                                        bookingDetailService.findByBooking(bookingDetailGet.getBooking().getId(),
                                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                                                .getContent().size();
                                if (Objects.nonNull(countBookingDetail)) {
                                    if (countBookingDetail == 1) {
                                        Booking booking = bookingDetailEdited.getBooking();
                                        booking.setStatusBooking(StatusBooking.BOOKING);
                                        bookingEdited = bookingService.editBooking(booking);
                                        if (Objects.nonNull(bookingEdited)) {
                                            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.STAFF));
                                        } else {
                                            check = false;
                                        }
                                    } else {
                                        return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.STAFF));
                                    }
                                }
                            } else {
                                check = false;
                                LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
                            }
                        }
                    }
                } else {
                    LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                }
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        if (!check) {
            if (bookingEdited != null) {
                bookingEdited.setStatusBooking(StatusBooking.PENDING);
                bookingService.editBooking(bookingEdited);
            }
            if (bookingDetailEdited != null) {
                bookingDetailEdited.setStatusBooking(StatusBooking.PENDING);
                bookingDetailService.editBookingDetail(bookingDetailEdited);
            }
            for (BookingDetailStep bookingDetailStep : bookingDetailStepEdited) {
                if (bookingDetailStep.getIsConsultation() == IsConsultation.FALSE) {
                    bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
                    bookingDetailStepService.editBookingDetailStep(bookingDetailStep);
                }
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.STAFF));
    }

    @PutMapping("/bookingdetailstep/addconsultant/{bookingDetailId}/{consultantId}")
    public Response addConsultantIntoBookingDetail(@PathVariable Integer bookingDetailId,
                                                   @PathVariable Integer consultantId) {
        List<BookingDetailStep> bookingDetailStepEdited = new ArrayList<>();
        int count = 0;
        boolean check = true;
        Booking bookingEdited = null;
        BookingDetail bookingDetailEdited = null;
        BookingDetail bookingDetailGet =
                bookingDetailService.findByBookingDetailId(bookingDetailId);
        if (Objects.nonNull(bookingDetailGet)) {
            List<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByBookingDetail(bookingDetailId,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                            .getContent();
            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                if (bookingDetailStep.getConsultant() != null) {
                    count++;
                }
            }
            if (count == bookingDetailSteps.size()) {
                return ResponseHelper.error(String.format(LoggingTemplate.EMPLOYEE_ASSIGNED));
            } else {
                if (bookingDetailSteps.size() != 0 || Objects.nonNull(bookingDetailSteps)) {
                    Consultant consultant = consultantService.findByConsultantId(consultantId);
                    if (Objects.nonNull(consultant)) {
                        for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                            bookingDetailStep.setConsultant(consultant);
                            bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                        }
                        for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                            BookingDetailStep bookingDetailStepResult =
                                    bookingDetailStepService.editBookingDetailStep(bookingDetailStep);
                            if (Objects.isNull(bookingDetailStepResult)) {
                                check = false;
                                LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL_STEP));
                            } else {
                                bookingDetailStepEdited.add(bookingDetailStepResult);
                            }
                        }
                        if (bookingDetailSteps.size() == bookingDetailStepEdited.size()) {
                            bookingDetailGet.setStatusBooking(StatusBooking.BOOKING);
                            bookingDetailEdited =
                                    bookingDetailService.editBookingDetail(bookingDetailGet);
                            if (Objects.nonNull(bookingDetailEdited)) {
                                Integer countBookingDetail =
                                        bookingDetailService.findByBooking(bookingDetailGet.getBooking().getId(),
                                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                                                .getContent().size();
                                if (Objects.nonNull(countBookingDetail)) {
                                    if (countBookingDetail == 1) {
                                        Booking booking = bookingDetailEdited.getBooking();
                                        booking.setStatusBooking(StatusBooking.BOOKING);
                                        bookingEdited = bookingService.editBooking(booking);
                                        if (Objects.nonNull(bookingEdited)) {
                                            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.STAFF));
                                        } else {
                                            check = false;
                                        }
                                    } else {
                                        return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.STAFF));
                                    }
                                }
                            } else {
                                check = false;
                                LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
                            }
                        }
                    }
                } else {
                    LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                }
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        if (!check) {
            if (bookingEdited != null) {
                bookingEdited.setStatusBooking(StatusBooking.PENDING);
                bookingService.editBooking(bookingEdited);
            }
            if (bookingDetailEdited != null) {
                bookingDetailEdited.setStatusBooking(StatusBooking.PENDING);
                bookingDetailService.editBookingDetail(bookingDetailEdited);
            }
            for (BookingDetailStep bookingDetailStep : bookingDetailStepEdited) {
                bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
                bookingDetailStepService.editBookingDetailStep(bookingDetailStep);
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.CONSULTANT));
    }

    @PutMapping(value = "/editProfileEmployee/{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editProfileStaff(@PathVariable Integer userId,
                                     EmployeeRequest user) {
        User userResult = userService.findByUserId(userId);
        if (Objects.nonNull(userResult)) {
            if (Objects.nonNull(user.getFile())) {
                String imageLink = UploadImage.uploadImage(user.getFile());
                if (imageLink != "") {
                    userResult.setImage(imageLink);
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            }
            if (Objects.nonNull(user.getFullname())) {
                userResult.setFullname(user.getFullname());
            }
            if (Objects.nonNull(user.getEmail())) {
                userResult.setEmail(user.getEmail());
            }
            if (Objects.nonNull(user.getAddress())) {
                userResult.setAddress(user.getAddress());
            }
            if (Objects.nonNull(user.getBirthdate())) {
                userResult.setBirthdate(user.getBirthdate());
            }
            if (Objects.nonNull(user.getGender())) {
                userResult.setGender(user.getGender());
            }
            if (Objects.nonNull(userService.editUser(userResult))) {
                return ResponseHelper.ok(userResult);
            }
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.PROFILE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.USER));
    }

    @PutMapping("/employee/remove/{userId}")
    public Response removeEmployee(@PathVariable Integer userId,
                                   @RequestBody Role role) {
        if (role == Role.STAFF) {
            Staff staff = staffService.findByStaffId(userId);
            staff.setStatus(Status.DISABLE);
            Staff staffResult = staffService.editStaff(staff);
            if (staffResult != null) {
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS, Constant.STAFF));
            }
            return ResponseHelper.error(String.format(LoggingTemplate.REMOVE_FAILED, Constant.STAFF));
        } else {
            Consultant consultant = consultantService.findByConsultantId(userId);
            consultant.setStatus(Status.DISABLE);
            Consultant consultantResult = consultantService.editConsultant(consultant);
            if (consultantResult != null) {
                return ResponseHelper.ok(String.format(LoggingTemplate.REMOVE_SUCCESS, Constant.CONSULTANT));
            }
            return ResponseHelper.error(String.format(LoggingTemplate.REMOVE_FAILED, Constant.CONSULTANT));
        }
    }

    @GetMapping("/bookingDetail/findByStatusChangeStaff")
    public Response findBookingDetailByStatusChangeStaff(@RequestParam Integer spaId) {
        List<BookingDetail> bookingDetails =
                bookingDetailService.findBySpaAndStatusBookingChangeStaff(spaId,
                        StatusBooking.CHANGE_STAFF);
        if (Objects.nonNull(bookingDetails)) {
            Page<BookingDetail> bookingDetailPage =
                    new PageImpl<>(bookingDetails,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                            bookingDetails.size());
            return ResponseHelper.ok(conversion.convertToPageBookingDetailResponse(bookingDetailPage));
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
    }

    @GetMapping("/getListStaffChange")
    public Response getListStaffChange(@RequestParam Integer bookingDetailId) {
        List<Staff> staffResult = new ArrayList<>();
        BookingDetail bookingDetail = bookingDetailService.findByBookingDetailId(bookingDetailId);
        if (Objects.nonNull(bookingDetail)) {
            Spa spa = bookingDetail.getBooking().getSpa();
            List<Staff> staffList = staffService.findBySpaIdAndStatusAvailable(spa.getId());
            if (Objects.nonNull(staffList)) {
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByBookingDetail(bookingDetailId,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                                .getContent();
                Staff staffOld = bookingDetailSteps.get(1).getStaff();
                for (Staff staff : staffList) {
                    if (!staff.equals(staffOld)) {
                        staffResult.add(staff);
                    }
                }
                return ResponseHelper.ok(staffResult);
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
    }

    @PutMapping("/bookingDetailStep/changeStaff/{staffId}/{bookingDetailId}")
    public Response changeStaffIntoBookingDetailStep(@PathVariable Integer staffId,
                                                     @PathVariable Integer bookingDetailId) throws FirebaseMessagingException {
        List<BookingDetailStep> bookingDetailStepEdit = new ArrayList<>();
        Staff staff = staffService.findByStaffId(staffId);
        if (Objects.nonNull(staff)) {
            BookingDetail bookingDetail =
                    bookingDetailService.findByBookingDetailId(bookingDetailId);
            if (Objects.nonNull(bookingDetail)) {
                List<BookingDetailStep> bookingDetailSteps = bookingDetail.getBookingDetailSteps();
                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                    if (!bookingDetailStep.getIsConsultation().equals(IsConsultation.TRUE)) {
                        if (!bookingDetailStep.getStatusBooking().equals(StatusBooking.FINISH)) {
                            bookingDetailStep.setStaff(staff);
                            bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                            bookingDetailStep.setReason(null);
                        }
                    }
                    bookingDetailStepEdit.add(bookingDetailStep);
                }
                bookingDetail.setBookingDetailSteps(bookingDetailStepEdit);
                bookingDetail.setStatusBooking(StatusBooking.BOOKING);
                bookingDetailService.editBookingDetail(bookingDetail);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                Customer customer = bookingDetail.getBooking().getCustomer();
                Map<String, String> map = new HashMap<>();
                map.put(MessageTemplate.CHANGE_STAFF_STATUS, "- bookingDetailId "
                        + bookingDetail.getId().toString());
                if (notificationFireBaseService.notify(MessageTemplate.CHANGE_STAFF_TITLE,
                        String.format(MessageTemplate.CHANGE_STAFF_FINISH_MESSAGE,
                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                        map, customer.getUser().getId(), Role.CUSTOMER)) {
                    Notification notification = new Notification();
                    notification.setRole(Role.CUSTOMER);
                    notification.setTitle(MessageTemplate.CHANGE_STAFF_TITLE);
                    notification.setMessage(MessageTemplate.CHANGE_STAFF_FINISH_MESSAGE);
                    notification.setData(map.get(MessageTemplate.CHANGE_STAFF_STATUS));
                    notification.setType(Constant.CHANGE_STAFF_TYPE);
                    notificationService.insertNewNotification(notification);
                    return ResponseHelper.ok(LoggingTemplate.CHANGE_STAFF_SUCCESS);
                } else {
                    return ResponseHelper.ok(LoggingTemplate.CHANGE_STAFF_SUCCESS);
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
        }
        return ResponseHelper.error(LoggingTemplate.CHANGE_STAFF_FAILED);
    }

    @GetMapping("/getDateOffApprove/{spaId}")
    public Response getDateOffApprove(@PathVariable Integer spaId,
                                      @RequestParam String dateStart,
                                      @RequestParam String dateEnd) {
        List<SpaDateOffResponse> result = new ArrayList<>();
        List<User> staffs = new ArrayList<>();
        List<User> consultants = new ArrayList<>();
        Date startDate = Date.valueOf(dateStart);
        Date endDate = Date.valueOf(dateEnd);
        List<Staff> allStaffs = staffService.findBySpaIdAndStatusAvailable(spaId);
        List<Consultant> allConsultants = consultantService.findBySpaIdAndStatusAvailable(spaId);
        List<DateOff> dateOffs =
                dateOffService.findBySpaAndFromToDateAndStatus(spaId, startDate,
                        endDate, StatusDateOff.APPROVE);
        SpaDateOffResponse spaDateOffResponse = new SpaDateOffResponse();
        Date oldDate = Date.valueOf(Constant.DATE_DEFAULT);
        Date newDate = Date.valueOf(Constant.DATE_DEFAULT);
        for (DateOff dateOff : dateOffs) {
            oldDate = newDate;
            newDate = dateOff.getDateOff();
            if (newDate.compareTo(oldDate) == 0) {
                for (Staff staff : allStaffs) {
                    if (staff.getUser().equals(dateOff.getEmployee())) {
                        staffs.add(dateOff.getEmployee());
                    }
                }
                for (Consultant consultant : allConsultants) {
                    if (consultant.getUser().equals(dateOff.getEmployee())) {
                        consultants.add(dateOff.getEmployee());
                    }
                }
                if (dateOffs.get(dateOffs.size() - 1).equals(dateOff)) {
                    spaDateOffResponse.setStaffs(staffs);
                    spaDateOffResponse.setConsultants(consultants);
                    result.add(spaDateOffResponse);
                }
            } else {
                if (!dateOffs.get(dateOffs.size() - 1).equals(dateOff)) {
                    if (result.size() == 0) {
                        if (!dateOffs.get(0).equals(dateOff)) {
                            spaDateOffResponse.setStaffs(staffs);
                            spaDateOffResponse.setConsultants(consultants);
                            result.add(spaDateOffResponse);
                            staffs = new ArrayList<>();
                            consultants = new ArrayList<>();
                            spaDateOffResponse = new SpaDateOffResponse();
                        }
                        spaDateOffResponse.setDate(newDate);
                        for (Staff staff : allStaffs) {
                            if (staff.getUser().equals(dateOff.getEmployee())) {
                                staffs.add(dateOff.getEmployee());
                            }
                        }
                        for (Consultant consultant : allConsultants) {
                            if (consultant.getUser().equals(dateOff.getEmployee())) {
                                consultants.add(dateOff.getEmployee());
                            }
                        }
                    } else {
                        spaDateOffResponse.setStaffs(staffs);
                        spaDateOffResponse.setConsultants(consultants);
                        result.add(spaDateOffResponse);
                        staffs = new ArrayList<>();
                        consultants = new ArrayList<>();
                        spaDateOffResponse = new SpaDateOffResponse();
                        spaDateOffResponse.setDate(newDate);
                        for (Staff staff : allStaffs) {
                            if (staff.getUser().equals(dateOff.getEmployee())) {
                                staffs.add(dateOff.getEmployee());
                            }
                        }
                        for (Consultant consultant : allConsultants) {
                            if (consultant.getUser().equals(dateOff.getEmployee())) {
                                consultants.add(dateOff.getEmployee());
                            }
                        }
                    }
                } else {
                    spaDateOffResponse.setStaffs(staffs);
                    spaDateOffResponse.setConsultants(consultants);
                    result.add(spaDateOffResponse);
                    staffs = new ArrayList<>();
                    consultants = new ArrayList<>();
                    spaDateOffResponse = new SpaDateOffResponse();
                    spaDateOffResponse.setDate(newDate);
                    for (Staff staff : allStaffs) {
                        if (staff.getUser().equals(dateOff.getEmployee())) {
                            staffs.add(dateOff.getEmployee());
                        }
                    }
                    for (Consultant consultant : allConsultants) {
                        if (consultant.getUser().equals(dateOff.getEmployee())) {
                            consultants.add(dateOff.getEmployee());
                        }
                    }
                    spaDateOffResponse.setStaffs(staffs);
                    spaDateOffResponse.setConsultants(consultants);
                    result.add(spaDateOffResponse);
                }
            }
        }
        return ResponseHelper.ok(result);
    }

    @GetMapping("/getDateOffOfSpa/{spaId}")
    public Response getDateOffOfSpa(@PathVariable Integer spaId,
                                    @RequestParam String dateStart,
                                    @RequestParam String dateEnd) {
        SpaAllDateOffResponse result = new SpaAllDateOffResponse();
        List<DateOffByDate> dateOffByDates = new ArrayList<>();
        DateOffByDate dateOffByDate = new DateOffByDate();
        List<DateOff> staffDateOffList = new ArrayList<>();
        List<DateOff> consultantDateOffList = new ArrayList<>();
        Date startDate = Date.valueOf(dateStart);
        Date endDate = Date.valueOf(dateEnd);
        List<Staff> allStaffs = staffService.findBySpaIdAndStatusAvailable(spaId);
        List<Consultant> allConsultants = consultantService.findBySpaIdAndStatusAvailable(spaId);
        result.setTotalConsultant(allConsultants.size());
        result.setTotalStaff(allStaffs.size());
        List<DateOff> dateOffWaiting =
                dateOffService.findBySpaAndStatusInRangeDate(spaId, StatusDateOff.WAITING, startDate,
                        endDate, PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT,
                                Sort.unsorted())).getContent();
        List<Date> dateList = new ArrayList<>();
        for (DateOff dateOff : dateOffWaiting) {
            if(dateList.size() == 0){
                dateList.add(dateOff.getDateOff());
            } else {
                if(!supportFunctions.checkDateExitedInList(dateOff.getDateOff(), dateList)){
                    dateList.add(dateOff.getDateOff());
                }
            }
        }
        List<DateOff> dateOffs = new ArrayList<>();
        for (Date dateOff : dateList) {
            List<DateOff> dateOffsGet = dateOffService.findByDateOff(dateOff);
            dateOffs.addAll(dateOffsGet);
        }
        Date oldDate = Date.valueOf(Constant.DATE_DEFAULT);
        Date newDate = Date.valueOf(Constant.DATE_DEFAULT);
        for (DateOff dateOff : dateOffs) {
            oldDate = newDate;
            newDate = dateOff.getDateOff();
            if (newDate.compareTo(oldDate) == 0) {
                for (Staff staff : allStaffs) {
                    if(staff.getUser().equals(dateOff.getEmployee())){
                        staffDateOffList.add(dateOff);
                    }
                }
                for (Consultant consultant : allConsultants) {
                    if(consultant.getUser().equals(dateOff.getEmployee())){
                        consultantDateOffList.add(dateOff);
                    }
                }
                if (dateOffs.get(dateOffs.size() - 1).equals(dateOff)) {
                    dateOffByDate.setStaffDateOffList(staffDateOffList);
                    dateOffByDate.setConsultantDateOffList(consultantDateOffList);
                    dateOffByDates.add(dateOffByDate);
                }
            } else {
                if (!dateOffs.get(dateOffs.size() - 1).equals(dateOff)) {
                    if (dateOffByDates.size() == 0) {
                        if (!dateOffs.get(0).equals(dateOff)) {
                            dateOffByDate.setStaffDateOffList(staffDateOffList);
                            dateOffByDate.setConsultantDateOffList(consultantDateOffList);
                            dateOffByDates.add(dateOffByDate);
                            dateOffByDate = new DateOffByDate();
                            staffDateOffList = new ArrayList<>();
                            consultantDateOffList = new ArrayList<>();
                        }
                    } else {
                        dateOffByDate.setStaffDateOffList(staffDateOffList);
                        dateOffByDate.setConsultantDateOffList(consultantDateOffList);
                        dateOffByDates.add(dateOffByDate);
                        dateOffByDate = new DateOffByDate();
                        staffDateOffList = new ArrayList<>();
                        consultantDateOffList = new ArrayList<>();
                    }
                    dateOffByDate.setDateOff(newDate);
                    for (Staff staff : allStaffs) {
                        if(staff.getUser().equals(dateOff.getEmployee())){
                            staffDateOffList.add(dateOff);
                        }
                    }
                    for (Consultant consultant : allConsultants) {
                        if(consultant.getUser().equals(dateOff.getEmployee())){
                            consultantDateOffList.add(dateOff);
                        }
                    }
                } else {
                    dateOffByDate.setStaffDateOffList(staffDateOffList);
                    dateOffByDate.setConsultantDateOffList(consultantDateOffList);
                    dateOffByDates.add(dateOffByDate);
                    dateOffByDate = new DateOffByDate();
                    staffDateOffList = new ArrayList<>();
                    consultantDateOffList = new ArrayList<>();
                    for (Staff staff : allStaffs) {
                        if(staff.getUser().equals(dateOff.getEmployee())){
                            staffDateOffList.add(dateOff);
                        }
                    }
                    for (Consultant consultant : allConsultants) {
                        if(consultant.getUser().equals(dateOff.getEmployee())){
                            consultantDateOffList.add(dateOff);
                        }
                    }
                    dateOffByDate.setStaffDateOffList(staffDateOffList);
                    dateOffByDate.setConsultantDateOffList(consultantDateOffList);
                    dateOffByDate.setDateOff(newDate);
                    dateOffByDates.add(dateOffByDate);
                }
            }
        }
        result.setDateOffByDateList(dateOffByDates);
        return ResponseHelper.ok(result);
    }

    @GetMapping("/dateOff/findByIdAndDateOff")
    public Response findByIdAndDateOff(@RequestParam Integer userId,
                                       @RequestParam String dateOff) {
        Date dateOffSearch = Date.valueOf(dateOff);
        DateOff dateOffResult = dateOffService.findByEmployeeAndDateOff(userId, dateOffSearch);
        if (Objects.nonNull(dateOffResult)) {
            return ResponseHelper.ok(dateOffResult);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.DATE_OFF));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.DATE_OFF));
    }

    @PutMapping("/dateOff/approveDateOffRequest")
    public Response approveDateOffRequest(@RequestBody DateOff dateOffRequest){
        if(dateOffRequest.getId() != null){
            DateOff dateOffEdit = dateOffService.findDateOffById(dateOffRequest.getId());
            if(Objects.nonNull(dateOffEdit)){
                dateOffEdit.setStatusDateOff(StatusDateOff.APPROVE);
                DateOff dateOffResult = dateOffService.editDateOff(dateOffEdit);
                if(Objects.nonNull(dateOffResult)){
                    return ResponseHelper.ok(LoggingTemplate.APPROVE_SUCCESS);
                } else {
                    LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.DATE_OFF));
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.DATE_OFF));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.ID_NOT_EXISTED));
        }
        return ResponseHelper.error(LoggingTemplate.APPROVE_FAILED);
    }

    @PutMapping("/dateOff/cancelDateOffRequest")
    public Response cancelDateOffRequest(@RequestBody DateOff dateOffRequest){
        if(dateOffRequest.getId() != null && dateOffRequest.getReasonCancel() != null){
            DateOff dateOffEdit = dateOffService.findDateOffById(dateOffRequest.getId());
            if(Objects.nonNull(dateOffEdit)){
                dateOffEdit.setStatusDateOff(StatusDateOff.CANCEL);
                dateOffEdit.setReasonCancel(dateOffRequest.getReasonCancel());
                DateOff dateOffResult = dateOffService.editDateOff(dateOffEdit);
                if(Objects.nonNull(dateOffResult)){
                    return ResponseHelper.ok(LoggingTemplate.CANCEL_SUCCESS);
                } else {
                    LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.DATE_OFF));
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.DATE_OFF));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.DATA_MISSING));
        }
        return ResponseHelper.error(LoggingTemplate.CANCEL_FAILED);
    }
}
