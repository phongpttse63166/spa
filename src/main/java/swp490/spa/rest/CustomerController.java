package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.BookingRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.services.SpaService;
import swp490.spa.utils.support.Constant;
import swp490.spa.utils.support.Notification;
import swp490.spa.utils.support.SupportFunctions;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
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
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public CustomerController(CustomerService customerService, UserLocationService userLocationService,
                              AccountRegisterService accountRegisterService, UserService userService,
                              SpaPackageService spaPackageService, SpaService spaService,
                              BookingDetailStepService bookingDetailStepService, StaffService staffService,
                              SpaTreatmentService spaTreatmentService, ConsultantService consultantService,
                              BookingService bookingService, BookingDetailService bookingDetailService) {
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
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
    }

    @GetMapping("/search/{userId}")
    public Response findCustomerById(@PathVariable Integer userId) {
        Customer customer = customerService.findByUserId(userId);
        return ResponseHelper.ok(customer);
    }

    @PostMapping("/userlocation/create")
    public Response createNewUserLocation(@RequestBody UserLocation userLocation) {
        UserLocation newUserLocation = userLocationService.insertNewUserLocation(userLocation);
        return ResponseHelper.ok(userLocation);
    }

    @PutMapping("/userlocation/edit")
    public Response editUserLocation(@RequestBody UserLocation userLocationUpdate) {
        UserLocation userLocation = userLocationService.findUserLocationByUserId(userLocationUpdate.getId());
        if (userLocation != null) {
            UserLocation result = userLocationService.editUserLocation(userLocationUpdate);
            if (Objects.nonNull(result)) {
                return ResponseHelper.ok(userLocationUpdate);
            }
            return ResponseHelper.error(Notification.EDIT_USER_LOCATION_FAIL);
        }
        return ResponseHelper.error(Notification.USER_EXISTED);
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
        return ResponseHelper.error(Notification.EDIT_PROFILE_FAIL);
    }

    @GetMapping("/getprofile")
    public Response getUserProfile(@RequestParam String userId) {
        Customer customer = customerService.findByUserId(Integer.parseInt(userId));
        if (Objects.nonNull(customer)) {
            return ResponseHelper.ok(customer);
        }
        return ResponseHelper.error(Notification.CUSTOMER_NOT_EXISTED);
    }

    @GetMapping("/getlisttimebook")
    public Response getListTimeToBook(@RequestParam Integer spaPackageId,
                                      @RequestParam String dateBooking,
                                      @RequestParam String isStaff) {
        List<User> userList = new ArrayList<>();
        supportFunctions.setBookingDetailStepService(bookingDetailStepService);
        SpaTreatment spaTreatment
                = spaTreatmentService.findTreatmentBySpaPackageIdWithTypeOneStep(spaPackageId);
        if (isStaff.equalsIgnoreCase("true")) {
            List<Staff> staffList = staffService.findBySpaId(spaTreatment.getSpa().getId());
            for (Staff staff : staffList) {
                userList.add(staff.getUser());
            }
        } else {
            List<Consultant> consultantList =
                    consultantService.findBySpaId(spaTreatment.getSpa().getId());
            for (Consultant consultant : consultantList) {
                userList.add(consultant.getUser());
            }
        }
        List<String> timeBookingList =
                supportFunctions.getBookTime(spaTreatment.getTotalTime(), userList,
                        dateBooking, isStaff);
        Page<String> page = new PageImpl<>(timeBookingList,
                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()),
                timeBookingList.size());
        return ResponseHelper.ok(page);
    }

    @PostMapping("/booking/create")
    public Response insertBooking(@RequestBody BookingRequest bookingRequest) {
        List<BookingData> bookingDataList = bookingRequest.getBookingDataList();
        List<SpaPackage> spaPackageList = new ArrayList<>();
        List<SpaTreatment> spaTreatmentList = new ArrayList<>();
        SpaPackage spaPackageSearchResult = null;
        Booking bookingResult = null;
        BookingDetail bookingDetailResult = null;
        boolean isOnlyOneStep = true;
        Double totalPrice = 0.0;
        Integer totalTime = 0;
        Spa spa = null;
        Customer customer = customerService.findByUserId(bookingRequest.getCustomerId());
        if (Objects.isNull(customer)) {
            ResponseHelper.error(Notification.CUSTOMER_NOT_EXISTED);
        }
        for (BookingData bookingData : bookingDataList) {
            spaPackageSearchResult = spaPackageService.findBySpaPackageId(bookingData.getPackageId());
            if (Objects.nonNull(spaPackageSearchResult)) {
                spaPackageList.add(spaPackageSearchResult);
                spa = spaPackageSearchResult.getSpa();
                if (spaPackageSearchResult.getType().equals(Type.MORESTEP)) {
                    isOnlyOneStep = false;
                }
            }
        }
        Booking booking = new Booking();
        booking.setStatusBooking(StatusBooking.BOOKING);
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
            bookingResult = bookingService.insertNewBooking(booking);
            if (Objects.nonNull(bookingResult)) {
                for (BookingData bookingData : bookingDataList) {
                    for (SpaTreatment spaTreatment : spaTreatmentList) {
                        if (spaTreatment.getSpaPackage().getId().equals(bookingData.getPackageId())) {
                            BookingDetail bookingDetail = new BookingDetail();
                            bookingDetail.setBooking(bookingResult);
                            bookingDetail.setSpaPackage(spaTreatment.getSpaPackage());
                            bookingDetail.setSpaTreatment(spaTreatment);
                            bookingDetail.setTotalTime(spaTreatment.getTotalTime());
                            bookingDetail.setType(spaTreatment.getSpaPackage().getType());
                            bookingDetailResult = bookingDetailService
                                    .insertBookingDetail(bookingDetail);
                            if (Objects.nonNull(bookingDetailResult)) {
                                List<TreatmentService> treatmentServices =
                                        new ArrayList<>(spaTreatment.getTreatmentServices());
                                Collections.sort(treatmentServices);
                                Time startTime;
                                Time endTime = Time.valueOf(Constant.TIME_DEFAULT);
                                for (int i = 0; i < treatmentServices.size(); i++) {
                                    BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                    bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                    bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                                    bookingDetailStep.setTreatmentService(treatmentServices.get(i));
                                    bookingDetailStep.setBookingDetail(bookingDetailResult);
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
                                        endTime = Time.valueOf(bookingData
                                                .getTimeBooking().toLocalTime()
                                                .plusMinutes(treatmentServices.get(i)
                                                        .getSpaService()
                                                        .getDurationMin()));
                                    }
                                    bookingDetailStep.setStartTime(startTime);
                                    bookingDetailStep.setEndTime(endTime);
                                    if (Objects.isNull(bookingDetailStepService.insertBookingDetailStep(bookingDetailStep))) {
                                        return ResponseHelper.error(Notification.BOOKING_DETAIL_STEP_CREATE_FAILED);
                                    }
                                }
                                LOGGER.info(Notification.BOOKING_CREATE_SUCCESS + booking);
                            }
                            LOGGER.info(Notification.BOOKING_DETAIL_CREATE_FAILED + bookingDetail);
                        }
                    }
                }
            }
        } else {
            bookingResult = bookingService.insertNewBooking(booking);
            if(Objects.nonNull(bookingResult)){
                for (BookingData bookingData : bookingDataList) {
                    for (SpaPackage spaPackage : spaPackageList) {
                        if(spaPackage.getId().equals(bookingData.getPackageId())){
                            if(spaPackage.getType().equals(Type.ONESTEP)){
                                SpaTreatment spaTreatment =
                                        spaTreatmentService
                                                .findTreatmentBySpaPackageIdWithTypeOneStep(spaPackage.getId());
                                BookingDetail bookingDetail = new BookingDetail();
                                bookingDetail.setBooking(bookingResult);
                                bookingDetail.setSpaPackage(spaPackage);
                                bookingDetail.setSpaTreatment(spaTreatment);
                                bookingDetail.setTotalTime(spaTreatment.getTotalTime());
                                bookingDetail.setType(Type.ONESTEP);
                                bookingDetailResult = bookingDetailService
                                        .insertBookingDetail(bookingDetail);
                                if (Objects.nonNull(bookingDetailResult)) {
                                    List<TreatmentService> treatmentServices =
                                            new ArrayList<>(spaTreatment.getTreatmentServices());
                                    Collections.sort(treatmentServices);
                                    Time startTime;
                                    Time endTime = Time.valueOf(Constant.TIME_DEFAULT);
                                    for (int i = 0; i < treatmentServices.size(); i++) {
                                        BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                        bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                        bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                                        bookingDetailStep.setTreatmentService(treatmentServices.get(i));
                                        bookingDetailStep.setBookingDetail(bookingDetailResult);
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
                                            endTime = Time.valueOf(bookingData
                                                    .getTimeBooking().toLocalTime()
                                                    .plusMinutes(treatmentServices.get(i)
                                                            .getSpaService()
                                                            .getDurationMin()));
                                        }
                                        bookingDetailStep.setStartTime(startTime);
                                        bookingDetailStep.setEndTime(endTime);
                                        if (Objects.isNull(bookingDetailStepService.insertBookingDetailStep(bookingDetailStep))) {
                                            LOGGER.info(Notification.BOOKING_DETAIL_STEP_CREATE_FAILED + bookingDetailStep);
                                        }
                                    }
                                    LOGGER.info(Notification.BOOKING_CREATE_SUCCESS + bookingResult);
                                }
                                LOGGER.info(Notification.BOOKING_DETAIL_CREATE_FAILED + bookingDetail);
                            } else {
                                BookingDetail bookingDetail = new BookingDetail();
                                bookingDetail.setBooking(bookingResult);
                                bookingDetail.setType(Type.MORESTEP);
                                bookingDetail.setSpaPackage(spaPackage);
                                bookingDetailResult = bookingDetailService
                                        .insertBookingDetail(bookingDetail);
                                if (Objects.nonNull(bookingDetailResult)) {
                                    BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                    bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                    bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                                    bookingDetailStep.setBookingDetail(bookingDetailResult);
                                    bookingDetailStep.setStartTime(bookingData.getTimeBooking());
                                    if (Objects.isNull(bookingDetailStepService.insertBookingDetailStep(bookingDetailStep))) {
                                        LOGGER.info(Notification.BOOKING_DETAIL_STEP_CREATE_FAILED + bookingDetailStep);
                                    }
                                }

                            }
                        }
                    }
                }
                return ResponseHelper.ok(Notification.BOOKING_CREATE_SUCCESS);
            }
        }
        return ResponseHelper.error(Notification.BOOKING_CREATE_FAILED);
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account){
        Customer customer = customerService.findByUserId(account.getId());
        User oldUser = customer.getUser();
        User updateUser = customer.getUser();
        updateUser.setPassword(account.getPassword());
        if(Objects.nonNull(userService.editUser(updateUser))){
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }
}
