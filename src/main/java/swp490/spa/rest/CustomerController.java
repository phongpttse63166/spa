package swp490.spa.rest;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.BookingRequest;
import swp490.spa.dto.requests.RatingRequest;
import swp490.spa.dto.responses.ScheduleBookingResponse;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.services.SpaService;
import swp490.spa.utils.support.image.UploadImage;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.templates.MessageTemplate;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class CustomerController {
    private static final Logger LOGGER = LogManager.getLogger(CustomerController.class);
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
    private ConsultantService consultantService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private BookingDetailService bookingDetailService;
    @Autowired
    private DateOffService dateOffService;
    @Autowired
    private NotificationFireBaseService notificationFireBaseService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private ManagerService managerService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;


    public CustomerController(CustomerService customerService, UserLocationService userLocationService,
                              AccountRegisterService accountRegisterService, UserService userService,
                              SpaPackageService spaPackageService, SpaService spaService,
                              BookingDetailStepService bookingDetailStepService, StaffService staffService,
                              SpaTreatmentService spaTreatmentService, ConsultantService consultantService,
                              BookingService bookingService, BookingDetailService bookingDetailService,
                              DateOffService dateOffService, NotificationFireBaseService notificationFireBaseService,
                              ManagerService managerService, RatingService ratingService,
                              NotificationService notificationService) {
        this.customerService = customerService;
        this.userLocationService = userLocationService;
        this.accountRegisterService = accountRegisterService;
        this.userService = userService;
        this.spaPackageService = spaPackageService;
        this.spaService = spaService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.staffService = staffService;
        this.spaTreatmentService = spaTreatmentService;
        this.consultantService = consultantService;
        this.bookingService = bookingService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.bookingDetailService = bookingDetailService;
        this.dateOffService = dateOffService;
        this.notificationFireBaseService = notificationFireBaseService;
        this.notificationService = notificationService;
        this.managerService = managerService;
        this.ratingService = ratingService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions(bookingDetailStepService, bookingDetailService);
    }

    @GetMapping("/getprofile")
    public Response getUserProfile(@RequestParam String userId) {
        Customer customer = customerService.findByUserId(Integer.parseInt(userId));
        if (Objects.nonNull(customer)) {
            return ResponseHelper.ok(customer);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CUSTOMER));
    }

    @GetMapping("/bookingdetail/{customerId}")
    public Response getBookingDetailMoreStepByCustomerId(@PathVariable Integer customerId,
                                                         Pageable pageable) {
        Page<BookingDetail> bookingDetailPage =
                bookingDetailService.findByTypeMoreStepAndCustomerId(Type.MORESTEP,
                        customerId, pageable);
        if (Objects.nonNull(bookingDetailPage)) {
            long totalElements = bookingDetailPage.getTotalElements();
            List<BookingDetail> bookingDetails = bookingDetailPage.getContent();
            for (BookingDetail bookingDetail : bookingDetails) {
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByBookingDetail(bookingDetail.getId(),
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()))
                                .getContent();
                if (Objects.nonNull(bookingDetailSteps)) {
                    bookingDetail.setBookingDetailSteps(bookingDetailSteps);
                } else {
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED,
                            Constant.BOOKING_DETAIL_STEP));
                }
            }
            bookingDetailPage = new PageImpl<>(bookingDetails, pageable, totalElements);
            return ResponseHelper.ok(conversion.convertToPageBookingDetailResponse(bookingDetailPage));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
    }

    @GetMapping("/getlisttimebook")
    public Response getListTimeBookingForCustomer(@RequestParam Integer spaPackageId,
                                                  @RequestParam String dateBooking,
                                                  @RequestParam Integer customerId,
                                                  @RequestParam Integer spaId) {
        int countEmployee = 0;
        List<DateOff> dateOffs = null;
        List<Staff> staffs = null;
        List<Consultant> consultants = null;
        List<BookingDetailStep> bookingDetailSteps = null;
        // Check list dateOff and get List Staff or List Consultant and All Booking Detail Step List
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaPackageId);
        if (Objects.nonNull(spaPackage)) {
            dateOffs = dateOffService.findByDateOffAndSpaAndStatusApprove(Date.valueOf(dateBooking),
                    spaId);
            if (spaPackage.getType().equals(Type.ONESTEP)) {
                staffs = staffService.findBySpaIdAndStatusAvailable(spaId);
                if (dateOffs.size() != 0) {
                    List<Staff> staffDateOff = new ArrayList<>();
                    for (Staff staff : staffs) {
                        if (staff.getUser().isActive() == true) {
                            for (DateOff dateOff : dateOffs) {
                                if (staff.getUser().equals(dateOff.getEmployee())) {
                                    staffDateOff.add(staff);
                                }
                            }
                        } else {
                            staffDateOff.add(staff);
                        }
                    }
                    staffs.removeAll(staffDateOff);
                }
                if(staffs.size()!=0) {
                    countEmployee = staffs.size();
                    bookingDetailSteps = bookingDetailStepService
                            .findByDateBookingAndIsConsultationAndSpa(Date.valueOf(dateBooking),
                                    IsConsultation.FALSE, spaId);
                } else {
                    LOGGER.error(LoggingTemplate.NO_STAFF_IN_SPA + spaId);
                    return ResponseHelper.ok("");
                }
            } else {
                consultants =
                        consultantService.findBySpaIdAndStatusAvailable(spaId);
                if (dateOffs.size() != 0) {
                    List<Consultant> consultantDateOff = new ArrayList<>();
                    for (Consultant consultant : consultants) {
                        if (consultant.getUser().isActive() == true) {
                            for (DateOff dateOff : dateOffs) {
                                if (consultant.getUser().equals(dateOff.getEmployee())) {
                                    consultantDateOff.add(consultant);
                                }
                            }
                        } else {
                            consultantDateOff.add(consultant);
                        }
                    }
                    consultants.removeAll(consultantDateOff);
                }
                if(consultants.size()!=0) {
                    countEmployee = consultants.size();
                    bookingDetailSteps = bookingDetailStepService
                            .findByDateBookingAndIsConsultationAndSpa(Date.valueOf(dateBooking),
                                    IsConsultation.TRUE, spaId);
                } else {
                    LOGGER.error(LoggingTemplate.NO_CONSULTANT_IN_SPA + spaId);
                    return ResponseHelper.ok("");
                }
            }
            /*
                Separate bookingDetailSteps into lists with incrementation time
                and put into map
            */
            Map<Integer, List<BookingDetailStep>> map =
                    supportFunctions.separateBookingDetailStepListAndPutIntoMap(bookingDetailSteps);
            int check = countEmployee - map.size();
            List<String> timeBookingList = null;
            if (spaPackage.getType().equals(Type.ONESTEP)) {
                SpaTreatment spaTreatment =
                        spaTreatmentService.findTreatmentBySpaPackageIdWithTypeOneStep(spaPackageId);
                timeBookingList =
                        supportFunctions.getBookTime(spaTreatment.getTotalTime(), map, check);
            } else {
                timeBookingList =
                        supportFunctions.getBookTime(Constant.DURATION_OF_CONSULTATION, map, check);
            }
            if (timeBookingList.size() != 0) {
                timeBookingList =
                        supportFunctions.checkAndGetListTimeBookingByCustomer(customerId, timeBookingList,
                                dateBooking);
                Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
                Date dateCheck = Date.valueOf(LocalDate.parse(dateBooking));
                if (currentDate.compareTo(dateCheck) == 0) {
                    List<String> listTimeGet = new ArrayList<>();
                    Time currentTime = Time.valueOf(LocalTime.now(ZoneId.of(Constant.ZONE_ID)));
                    for (String time : timeBookingList) {
                        Time checkTime = Time.valueOf(time);
                        if (checkTime.compareTo(currentTime) > 0) {
                            listTimeGet.add(time);
                        }
                    }
                    timeBookingList = listTimeGet;
                }
                Page<String> page = new PageImpl<>(timeBookingList,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()),
                        timeBookingList.size());
                return ResponseHelper.ok(page);
            }
            return ResponseHelper.ok(LoggingTemplate.NO_EMPLOYEE_FREE);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_PACKAGE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.TIME_LIST));
    }

    @PostMapping("/userlocation/create")
    public Response createNewUserLocation(@RequestBody UserLocation userLocation) {
        UserLocation newUserLocation = userLocationService.insertNewUserLocation(userLocation);
        return ResponseHelper.ok(userLocation);
    }

    // Th??m trong BookingRequest spaId
    @PostMapping("/booking/create")
    public Response insertBooking(@RequestBody BookingRequest bookingRequest) {
        boolean checkNoEmployForBooking = false;
        SpaPackage spaPackageCheck = spaPackageService
                .findBySpaPackageId(bookingRequest.getBookingDataList().get(0).getPackageId());
        List<Staff> staffList = staffService.findBySpaIdAndStatusAvailable(bookingRequest.getSpaId());
        List<Consultant> consultantList =
                consultantService.findBySpaIdAndStatusAvailable(bookingRequest.getSpaId());
        for (BookingData bookingData : bookingRequest.getBookingDataList()) {
            spaPackageCheck = spaPackageService.findBySpaPackageId(bookingData.getPackageId());
            Time startTime = bookingData.getTimeBooking();
            List<DateOff> dateOffs =
                    dateOffService.findByDateOffAndSpaAndStatusApprove(bookingData.getDateBooking(),
                            bookingRequest.getSpaId());
            List<Staff> staffBookingList = staffList;
            List<Consultant> consultantBookingList = consultantList;
            if (dateOffs.size() != 0) {
                List<Staff> staffDateOffList = new ArrayList<>();
                List<Consultant> consultantDateOffList = new ArrayList<>();
                for (Staff staff : staffList) {
                    if (staff.getUser().isActive() == true) {
                        for (DateOff dateOff : dateOffs) {
                            if (staff.getUser().equals(dateOff.getEmployee())) {
                                staffDateOffList.add(staff);
                            }
                        }
                    } else {
                        staffDateOffList.add(staff);
                    }
                }
                staffBookingList.removeAll(staffDateOffList);
                for (Consultant consultant : consultantList) {
                    for (DateOff dateOff : dateOffs) {
                        if (consultant.getUser().equals(dateOff.getEmployee())) {
                            consultantDateOffList.add(consultant);
                        }
                    }
                }
                consultantBookingList.removeAll(consultantDateOffList);
            }
            int count = 0;
            if (spaPackageCheck.getType().equals(Type.ONESTEP)) {
                SpaTreatment spaTreatment = spaTreatmentService
                        .findTreatmentBySpaPackageIdWithTypeOneStep(spaPackageCheck.getId());
                Time endTime = Time.valueOf(startTime.toLocalTime()
                        .plusMinutes(spaTreatment.getTotalTime()));
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByStartTimeAndEndTimeAndDateBooking(startTime,
                                endTime, bookingData.getDateBooking());
                if (Objects.isNull(bookingDetailSteps)) {
                    LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                } else {
                    if (bookingDetailSteps.size() != 0) {
                        for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                            for (Staff staff : staffBookingList) {
                                if (bookingDetailStep.getStaff().equals(staff)) {
                                    count += 1;
                                }
                            }
                        }
                        if (count == staffBookingList.size()) {
                            checkNoEmployForBooking = true;
                        }
                    }
                }
            } else {
                Time endTime = Time.valueOf(startTime.toLocalTime()
                        .plusMinutes(Constant.DURATION_OF_CONSULTATION));
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByStartTimeAndEndTimeAndDateBooking(startTime,
                                endTime, bookingData.getDateBooking());
                if (Objects.isNull(bookingDetailSteps)) {
                    LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                    return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                } else {
                    if (bookingDetailSteps.size() != 0) {
                        for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                            for (Consultant consultant : consultantBookingList) {
                                if (bookingDetailStep.getConsultant().equals(consultant)) {
                                    count += 1;
                                }
                            }
                        }
                        if (count == consultantBookingList.size()) {
                            checkNoEmployForBooking = true;
                        }
                    }
                }
            }
        }
        if (checkNoEmployForBooking) {
            return ResponseHelper.error(LoggingTemplate.CANNOT_BOOKING_AT_TIME);
        } else {
            List<BookingData> bookingDataList = bookingRequest.getBookingDataList();
            List<SpaPackage> spaPackageList = new ArrayList<>();
            List<SpaTreatment> spaTreatmentList = new ArrayList<>();
            SpaPackage spaPackageSearchResult = null;
            Booking bookingResult = null;
            List<BookingDetail> bookingDetailResultList = new ArrayList<>();
            List<BookingDetailStep> bookingDetailStepResultList = new ArrayList<>();
            boolean isOnlyOneStep = true;
            boolean checkCanInsert = true;
            Double totalPrice = 0.0;
            Integer totalTime = 0;
            Spa spa = spaService.findById(bookingRequest.getSpaId());
            Customer customer = customerService.findByUserId(bookingRequest.getCustomerId());
            if (Objects.isNull(customer)) {
                ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CUSTOMER));
            }
            for (BookingData bookingData : bookingDataList) {
                spaPackageSearchResult = spaPackageService.findBySpaPackageId(bookingData.getPackageId());
                if (Objects.nonNull(spaPackageSearchResult)) {
                    if (!supportFunctions.checkSpaPackageExisted(spaPackageSearchResult, spaPackageList)) {
                        spaPackageList.add(spaPackageSearchResult);
                        if (spaPackageSearchResult.getType().equals(Type.MORESTEP)) {
                            isOnlyOneStep = false;
                        }
                    }
                }
            }
            Booking booking = new Booking();
            booking.setStatusBooking(StatusBooking.PENDING);
            booking.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
            booking.setCustomer(customer);
            booking.setSpa(spa);
            if (isOnlyOneStep) {
                for (SpaPackage spaPackage : spaPackageList) {
                    SpaTreatment spaTreatment = spaTreatmentService
                            .findTreatmentBySpaPackageIdWithTypeOneStep(spaPackage.getId());
                    totalPrice += spaTreatment.getTotalPrice();
                    totalTime += spaTreatment.getTotalTime();
                    spaTreatmentList.add(spaTreatment);
                }
                booking.setTotalPrice(totalPrice);
                booking.setTotalTime(totalTime);
                bookingResult = booking;
                for (BookingData bookingData : bookingDataList) {
                    for (SpaTreatment spaTreatment : spaTreatmentList) {
                        if (spaTreatment.getSpaPackage().getId().equals(bookingData.getPackageId())) {
                            BookingDetail bookingDetail = new BookingDetail();
                            bookingDetail.setBooking(bookingResult);
                            bookingDetail.setSpaPackage(spaTreatment.getSpaPackage());
                            bookingDetail.setTotalPrice(spaTreatment.getTotalPrice());
                            bookingDetail.setStatusBooking(StatusBooking.PENDING);
                            bookingDetail.setSpaTreatment(spaTreatment);
                            bookingDetail.setTotalTime(spaTreatment.getTotalTime());
                            bookingDetail.setType(spaTreatment.getSpaPackage().getType());
                            List<TreatmentService> treatmentServices =
                                    new ArrayList<>(spaTreatment.getTreatmentServices());
                            Collections.sort(treatmentServices);
                            Time startTime;
                            Time endTime = Time.valueOf(Constant.TIME_DEFAULT);
                            for (int i = 0; i < treatmentServices.size(); i++) {
                                BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
                                bookingDetailStep.setBookingPrice(treatmentServices.get(i)
                                        .getSpaService().getPrice());
                                bookingDetailStep.setTreatmentService(treatmentServices.get(i));
                                bookingDetailStep.setBookingDetail(bookingDetail);
                                bookingDetailStep.setIsConsultation(IsConsultation.FALSE);
                                if (i == 0) {
                                    startTime = bookingData.getTimeBooking();
                                    endTime =
                                            Time.valueOf(bookingData
                                                    .getTimeBooking().toLocalTime()
                                                    .plusMinutes(treatmentServices.get(i)
                                                            .getSpaService()
                                                            .getDurationMin()));
                                } else {
                                    startTime = endTime;
                                    endTime = Time.valueOf(startTime.toLocalTime()
                                            .plusMinutes(treatmentServices.get(i)
                                                    .getSpaService().getDurationMin()));
                                }
                                bookingDetailStep.setStartTime(startTime);
                                bookingDetailStep.setEndTime(endTime);
                                bookingDetailStepResultList.add(bookingDetailStep);
                            }
                            bookingDetail.addAllBookingDetailStep(bookingDetailStepResultList);
                            bookingDetailStepResultList.clear();
                            bookingDetailResultList.add(bookingDetail);
                        }
                    }
                }
                booking.addAllBookingDetail(bookingDetailResultList);
                bookingDetailResultList.clear();
            } else {
                for (BookingData bookingData : bookingDataList) {
                    for (SpaPackage spaPackage : spaPackageList) {
                        if (spaPackage.getId().equals(bookingData.getPackageId())) {
                            if (spaPackage.getType().equals(Type.ONESTEP)) {
                                SpaTreatment spaTreatment =
                                        spaTreatmentService
                                                .findTreatmentBySpaPackageIdWithTypeOneStep(spaPackage.getId());
                                BookingDetail bookingDetail = new BookingDetail();
                                bookingDetail.setBooking(booking);
                                bookingDetail.setSpaPackage(spaPackage);
                                bookingDetail.setSpaTreatment(spaTreatment);
                                bookingDetail.setStatusBooking(StatusBooking.PENDING);
                                bookingDetail.setTotalPrice(spaTreatment.getTotalPrice());
                                bookingDetail.setTotalTime(spaTreatment.getTotalTime());
                                bookingDetail.setType(Type.ONESTEP);
                                List<TreatmentService> treatmentServices =
                                        new ArrayList<>(spaTreatment.getTreatmentServices());
                                Collections.sort(treatmentServices);
                                Time startTime;
                                Time endTime = Time.valueOf(Constant.TIME_DEFAULT);
                                for (int i = 0; i < treatmentServices.size(); i++) {
                                    BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                    bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                    bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
                                    bookingDetailStep.setTreatmentService(treatmentServices.get(i));
                                    bookingDetailStep.setBookingPrice(treatmentServices.get(i)
                                            .getSpaService().getPrice());
                                    bookingDetailStep.setIsConsultation(IsConsultation.FALSE);
                                    bookingDetailStep.setBookingDetail(bookingDetail);
                                    if (i == 0) {
                                        startTime = bookingData.getTimeBooking();
                                        endTime = Time.valueOf(bookingData
                                                .getTimeBooking().toLocalTime()
                                                .plusMinutes(treatmentServices.get(i)
                                                        .getSpaService()
                                                        .getDurationMin()));
                                    } else {
                                        startTime = endTime;
                                        endTime = Time.valueOf(startTime.toLocalTime()
                                                .plusMinutes(treatmentServices.get(i)
                                                        .getSpaService().getDurationMin()));
                                    }
                                    bookingDetailStep.setStartTime(startTime);
                                    bookingDetailStep.setEndTime(endTime);
                                    bookingDetailStepResultList.add(bookingDetailStep);
                                }
                                bookingDetail.addAllBookingDetailStep(bookingDetailStepResultList);
                                bookingDetailStepResultList.clear();
                                bookingDetailResultList.add(bookingDetail);
                            } else {
                                BookingDetail bookingDetail = new BookingDetail();
                                bookingDetail.setBooking(booking);
                                bookingDetail.setType(Type.MORESTEP);
                                bookingDetail.setSpaPackage(spaPackage);
                                bookingDetail.setStatusBooking(StatusBooking.PENDING);
                                BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
                                bookingDetailStep.setIsConsultation(IsConsultation.TRUE);
                                bookingDetailStep.setBookingDetail(bookingDetail);
                                bookingDetailStep.setStartTime(bookingData.getTimeBooking());
                                bookingDetailStep.setEndTime(Time.valueOf(bookingData
                                        .getTimeBooking()
                                        .toLocalTime()
                                        .plusMinutes(Constant.DURATION_OF_CONSULTATION)));
                                bookingDetailStepResultList.add(bookingDetailStep);
                                bookingDetail.addAllBookingDetailStep(bookingDetailStepResultList);
                                bookingDetailStepResultList.clear();
                                bookingDetailResultList.add(bookingDetail);
                            }
                        }
                    }
                }
                booking.addAllBookingDetail(bookingDetailResultList);
                bookingDetailResultList.clear();
            }
            for (BookingDetail bookingDetail : booking.getBookingDetails()) {
                for (BookingDetailStep bookingDetailStep : bookingDetail.getBookingDetailSteps()) {
                    Time endTime = bookingDetailStep.getEndTime();
                    if ((endTime.toLocalTime().isAfter(LocalTime.parse(Constant.TIME_START_RELAX))
                            && endTime.toLocalTime().isBefore(LocalTime.parse(Constant.TIME_END_RELAX)))
                            || endTime.toLocalTime().isAfter(LocalTime.parse(Constant.TIME_END_DATE))) {
                        checkCanInsert = false;
                    }
                }
            }
            if (checkCanInsert) {
                Booking bookingInsert = bookingService.insertNewBooking(booking);
                if (Objects.nonNull(bookingInsert)) {
                    try {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                        List<Manager> managers =
                                managerService.findManagerBySpaAndStatusAvailable(spa.getId());
                        Map<String, String> map = new HashMap<>();
                        map.put(MessageTemplate.BOOKING_STATUS,
                                MessageTemplate.BOOKING_STATUS + "- bookingId " +
                                        bookingInsert.getId().toString());
                        if (notificationFireBaseService.notify(MessageTemplate.BOOKING_TITLE,
                                String.format(MessageTemplate.BOOKING_MESSAGE,
                                        LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                                map, managers.get(0).getUser().getId(), Role.MANAGER)) {
                            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.BOOKING));
                        } else {
                            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.BOOKING));
                        }
                    } catch (FirebaseMessagingException e) {
                        LOGGER.error(e.getMessage());
                        return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.BOOKING));
                    }
                }
            } else {
                return ResponseHelper.error(LoggingTemplate.BOOKING_OVERTIME);
            }
            return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.BOOKING));
        }
    }

    @PutMapping("/userlocation/edit")
    public Response editUserLocation(@RequestBody UserLocation userLocationUpdate) {
        UserLocation userLocation = userLocationService.findUserLocationByUserId(userLocationUpdate.getId());
        if (userLocation != null) {
            UserLocation result = userLocationService.editUserLocation(userLocationUpdate);
            if (Objects.nonNull(result)) {
                return ResponseHelper.ok(userLocationUpdate);
            }
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.USER_LOCATION
            ));
        }
        return ResponseHelper.error(LoggingTemplate.USER_EXISTED);
    }

    @PutMapping("/user/edit")
    public Response editUserProfile(@RequestBody User user) {
        User userResult = userService.findByPhone(user.getPhone());
        if (Objects.nonNull(userResult)) {
            userResult.setFullname(user.getFullname());
            userResult.setAddress(user.getAddress());
            userResult.setEmail(user.getEmail());
            if (Objects.nonNull(userService.editUser(userResult))) {
                return ResponseHelper.ok(userResult);
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.PROFILE));
    }

    @PutMapping(value = "/image/edit/{userId}",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response editImage(@PathVariable Integer userId, MultipartFile file) {
        User user = userService.findByUserId(userId);
        if (Objects.nonNull(user)) {
            if (!file.isEmpty()) {
                String imageLink = UploadImage.uploadImage(file);
                if (imageLink != "") {
                    user.setImage(imageLink);
                    User userResult = userService.editUser(user);
                    if (Objects.nonNull(userResult)) {
                        return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.IMAGE));
                    }
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            } else {
                LOGGER.error(LoggingTemplate.FILE_NOT_EXISTED);
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.USER));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.IMAGE));
    }


    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account) {
        Customer customer = customerService.findByUserId(account.getId());
        User oldUser = customer.getUser();
        User updateUser = customer.getUser();
        updateUser.setPassword(account.getPassword());
        if (Objects.nonNull(userService.editUser(updateUser))) {
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }

    @GetMapping("/getListConsultantForChat")
    public Response getListConsultantForChat(@RequestParam Integer customerId) {
        List<Consultant> consultants = new ArrayList<>();
        List<BookingDetail> bookingDetailList =
                bookingDetailService.findByCustomer(customerId);
        if (Objects.nonNull(bookingDetailList)) {
            for (BookingDetail bookingDetail : bookingDetailList) {
                List<BookingDetailStep> bookingDetailSteps =
                        bookingDetailStepService.findByBookingDetail(bookingDetail.getId(),
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()))
                                .getContent();
                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                    if (bookingDetailStep.getConsultant() != null) {
                        Consultant consultant = bookingDetailStep.getConsultant();
                        if (consultants.size() == 0) {
                            consultants.add(consultant);
                        } else {
                            if (!supportFunctions.checkConsultantExistedInList(consultant, consultants)) {
                                consultants.add(consultant);
                            }
                        }
                    }
                }
            }
            return ResponseHelper.ok(consultants);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.LIST_CONSULTANT_CHATTING));
    }

    @PutMapping("/rating/edit")
    public Response ratingStep(@RequestBody RatingRequest ratingRequest) throws FirebaseMessagingException {
        if (Objects.nonNull(ratingRequest.getRatingId())) {
            Rating ratingGet = ratingService.findByRatingId(ratingRequest.getRatingId());
            if (Objects.nonNull(ratingRequest.getRate())) {
                ratingGet.setRate(ratingRequest.getRate());
            }
            if (Objects.nonNull(ratingRequest.getComment())) {
                ratingGet.setComment(ratingRequest.getComment());
            }
            ratingGet.setStatusRating(StatusRating.RATED);
            Rating ratingResult = ratingService.editRating(ratingGet);
            if (Objects.nonNull(ratingResult)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                Map<String, String> map = new HashMap<>();
                map.put(MessageTemplate.RATING_STATUS,
                        MessageTemplate.RATING_STATUS + "- ratingId "
                                + ratingRequest.getRatingId());
                if (notificationFireBaseService.notify(MessageTemplate.RATING_TITLE,
                        String.format(MessageTemplate.RATING_MESSAGE,
                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                        map, ratingRequest.getStaffId(), Role.STAFF)) {
                    return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.RATING));
                } else {
                    return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.RATING));
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.RATING));
            }
        } else {
            LOGGER.error(LoggingTemplate.ID_NOT_EXISTED);
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.RATING));
    }

    @GetMapping("/getAllNotification/{customerId}")
    public Response getAllNotification(@PathVariable Integer customerId) {
        List<Notification> notifications =
                notificationService.findByIdAndRole(customerId, Role.CUSTOMER);
        if (Objects.nonNull(notifications)) {
            return ResponseHelper.ok(notifications);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.NOTIFICATION));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.NOTIFICATION));
    }

    @GetMapping("/getScheduleBooking/{customerId}")
    public Response getScheduleBookingByCustomer(@PathVariable Integer customerId) {
        List<ScheduleBookingResponse> scheduleBookingResponses = new ArrayList<>();
        ScheduleBookingResponse scheduleBookingResponse = new ScheduleBookingResponse();
        List<BookingDetailStep> bookingDetailStepAdd = new ArrayList<>();
        Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
        Date finalDate = Date.valueOf(currentDate.toLocalDate().plusDays(7));
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByCustomerAndFromToDate(customerId, currentDate, finalDate);
        List<BookingDetailStep> bookingDetailStepsFilter = new ArrayList<>();
        for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
            BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
            if(bookingDetail.getType().equals(Type.ONESTEP)){
                if(bookingDetailStep.equals(bookingDetail.getBookingDetailSteps().get(0))){
                    bookingDetailStepsFilter.add(bookingDetailStep);
                }
            } else {
                bookingDetailStepsFilter.add(bookingDetailStep);
            }
        }
        bookingDetailSteps = bookingDetailStepsFilter;
        if (Objects.nonNull(bookingDetailSteps)) {
            if (bookingDetailSteps.size() == 0) {
                return ResponseHelper.ok("");
            } else if(bookingDetailSteps.size() == 1) {
                scheduleBookingResponse.setDateBooking(bookingDetailSteps.get(0).getDateBooking());
                bookingDetailStepAdd.add(bookingDetailSteps.get(0));
                scheduleBookingResponse.setBookingDetailSteps(bookingDetailStepAdd);
                scheduleBookingResponses.add(scheduleBookingResponse);
            } else {
                Date oldDate;
                Date newDate = Date.valueOf(Constant.DATE_DEFAULT);
                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                    oldDate = newDate;
                    newDate = bookingDetailStep.getDateBooking();
                    if (oldDate.compareTo(newDate) == 0) {
                        bookingDetailStepAdd.add(bookingDetailStep);
                        if (bookingDetailSteps.get(bookingDetailSteps.size() - 1).equals(bookingDetailStep)) {
                            scheduleBookingResponse.setBookingDetailSteps(bookingDetailStepAdd);
                            scheduleBookingResponses.add(scheduleBookingResponse);
                        }
                    } else {
                        if (!bookingDetailSteps.get(bookingDetailSteps.size() - 1).equals(bookingDetailStep)) {
                            if (scheduleBookingResponses.size() == 0) {
                                if (!bookingDetailSteps.get(0).equals(bookingDetailStep)) {
                                    scheduleBookingResponse.setBookingDetailSteps(bookingDetailStepAdd);
                                    scheduleBookingResponses.add(scheduleBookingResponse);
                                    scheduleBookingResponse = new ScheduleBookingResponse();
                                    bookingDetailStepAdd = new ArrayList<>();
                                }
                            } else {
                                scheduleBookingResponse.setBookingDetailSteps(bookingDetailStepAdd);
                                scheduleBookingResponses.add(scheduleBookingResponse);
                                scheduleBookingResponse = new ScheduleBookingResponse();
                                bookingDetailStepAdd = new ArrayList<>();
                            }
                            scheduleBookingResponse.setDateBooking(newDate);
                            bookingDetailStepAdd.add(bookingDetailStep);
                        } else {
                            scheduleBookingResponse.setBookingDetailSteps(bookingDetailStepAdd);
                            scheduleBookingResponses.add(scheduleBookingResponse);
                            scheduleBookingResponse = new ScheduleBookingResponse();
                            bookingDetailStepAdd = new ArrayList<>();
                            scheduleBookingResponse.setDateBooking(newDate);
                            bookingDetailStepAdd.add(bookingDetailStep);
                            scheduleBookingResponse.setBookingDetailSteps(bookingDetailStepAdd);
                            scheduleBookingResponses.add(scheduleBookingResponse);
                        }
                    }
                }
            }
            for (ScheduleBookingResponse sbr : scheduleBookingResponses) {
                sbr.getBookingDetailSteps().sort(Comparator.comparing(BookingDetailStep::getStartTime));
            }
            Page<ScheduleBookingResponse> page =
                    new PageImpl<>(scheduleBookingResponses,
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.by("dateBooking")),
                            scheduleBookingResponses.size());
            return ResponseHelper.ok(page);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
    }

    @GetMapping("/bookingDetail/findByBookingDetailId/{bookingDetailId}")
    public Response findBookingDetailById(@PathVariable Integer bookingDetailId){
        BookingDetail bookingDetailResult = bookingDetailService.findByBookingDetailId(bookingDetailId);
        if(Objects.nonNull(bookingDetailResult)){
            List<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByBookingDetail(bookingDetailId,
                            PageRequest.of(Constant.PAGE_DEFAULT,Constant.SIZE_DEFAULT,Sort.unsorted()))
                    .getContent();
            bookingDetailResult.setBookingDetailSteps(bookingDetailSteps);
            return ResponseHelper.ok(conversion.convertToBookingDetailResponse(bookingDetailResult));
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
    }

    @GetMapping("/bookingDetailFinishTypeMoreStep/getAll/{customerId}")
    public Response getAllBookingDetailFinishTypeMoreStep(@PathVariable Integer customerId){
        List<BookingDetail> bookingDetails =
                bookingDetailService.findByCustomerAndTypeAndStatus(customerId,
                        Type.MORESTEP, StatusBooking.FINISH);
        if(bookingDetails!=null){
            return ResponseHelper.ok(bookingDetails);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
    }

    @GetMapping("/bookingDetailFinishTypeOneStep/getAll/{customerId}")
    public Response getAllBookingDetailFinishTypeOneStep(@PathVariable Integer customerId){
        List<BookingDetail> bookingDetails =
                bookingDetailService.findByCustomerAndTypeAndStatus(customerId,
                        Type.ONESTEP, StatusBooking.FINISH);
        if(bookingDetails!=null){
            return ResponseHelper.ok(bookingDetails);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
    }
}
