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
import java.time.LocalTime;
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
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public CustomerController(CustomerService customerService, UserLocationService userLocationService,
                              AccountRegisterService accountRegisterService, UserService userService,
                              SpaPackageService spaPackageService, SpaService spaService,
                              BookingDetailStepService bookingDetailStepService, StaffService staffService,
                              SpaTreatmentService spaTreatmentService, ConsultantService consultantService,
                              BookingService bookingService, BookingDetailService bookingDetailService,
                              DateOffService dateOffService) {
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
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
    }

    @GetMapping("/search/{userId}")
    public Response getCustomerById(@PathVariable Integer userId) {
        Customer customer = customerService.findByUserId(userId);
        return ResponseHelper.ok(customer);
    }

    @GetMapping("/getprofile")
    public Response getUserProfile(@RequestParam String userId) {
        Customer customer = customerService.findByUserId(Integer.parseInt(userId));
        if (Objects.nonNull(customer)) {
            return ResponseHelper.ok(customer);
        }
        return ResponseHelper.error(String.format(Notification.GET_FAILED, Constant.CUSTOMER));
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
                    return ResponseHelper.error(String.format(Notification.GET_FAILED,
                            Constant.BOOKING_DETAIL_STEP));
                }
            }
            bookingDetailPage = new PageImpl<>(bookingDetails, pageable, totalElements);
            return ResponseHelper.ok(conversion.convertToPageBookingDetailResponse(bookingDetailPage));
        }
        return ResponseHelper.error(String.format(Notification.GET_FAILED, Constant.BOOKING_DETAIL));
    }

    @GetMapping("/getlisttimebook")
    public Response getListTimeBookingTest(@RequestParam Integer spaPackageId,
                                           @RequestParam String dateBooking) {
        supportFunctions.setBookingDetailStepService(bookingDetailStepService);
        int countEmployee = 0;
        List<DateOff> dateOffs = null;
        List<Staff> staffs = null;
        List<Consultant> consultants = null;
        List<BookingDetailStep> bookingDetailSteps = null;
        SpaPackage spaPackage = spaPackageService.findBySpaPackageId(spaPackageId);
        if (Objects.nonNull(spaPackage)) {
            dateOffs = dateOffService.findByDateOffAndSpaAndStatusApprove(Date.valueOf(dateBooking),
                    spaPackage.getSpa().getId());
            if (spaPackage.getType().equals(Type.ONESTEP)) {
                staffs = staffService.findBySpaId(spaPackage.getSpa().getId());
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
                countEmployee = staffs.size();
                bookingDetailSteps = bookingDetailStepService
                        .findByDateBookingAndIsConsultation(Date.valueOf(dateBooking),
                                IsConsultation.FALSE,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()))
                        .getContent();
            } else {
                consultants =
                        consultantService.findBySpaId(spaPackage.getSpa().getId());
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
                countEmployee = consultants.size();
                bookingDetailSteps = bookingDetailStepService
                        .findByDateBookingAndIsConsultation(Date.valueOf(dateBooking),
                                IsConsultation.TRUE,
                                PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()))
                        .getContent();
            }
            List<BookingDetailStep> bookingDetailStepCheckList = bookingDetailSteps;
            Map<Integer, List<BookingDetailStep>> map = new HashMap<>();
            int count = 0;
            int oldBookingDetailId = 0;
            int newBookingDetailId = 0;
            int checkIgnore = -1;
            boolean checkFinish = false;
            BookingDetailStep oldBookingDetailStep = null;
            while (!checkFinish) {
                List<BookingDetailStep> bookingDetailStepDraftList = bookingDetailStepCheckList;
                bookingDetailStepCheckList = new ArrayList<>();
                List<BookingDetailStep> list = new ArrayList<>();
                for (BookingDetailStep bookingDetailStep : bookingDetailStepDraftList) {
                    oldBookingDetailId = newBookingDetailId;
                    newBookingDetailId = bookingDetailStep.getBookingDetail().getId();
                    if (checkIgnore != newBookingDetailId) {
                        if (oldBookingDetailId == 0) {
                            list.add(bookingDetailStep);
                            oldBookingDetailStep = bookingDetailStep;
                        } else if (!bookingDetailStepDraftList.get(bookingDetailStepDraftList.size() - 1).equals(bookingDetailStep)) {
                            if (oldBookingDetailId == newBookingDetailId) {
                                list.add(bookingDetailStep);
                                oldBookingDetailStep = bookingDetailStep;
                            } else {
                                if (oldBookingDetailStep.getStartTime()
                                        .compareTo(bookingDetailStep.getStartTime()) > 0) {
                                    checkIgnore = newBookingDetailId;
                                    bookingDetailStepCheckList.add(bookingDetailStep);
                                } else {
                                    list.add(bookingDetailStep);
                                    oldBookingDetailStep = bookingDetailStep;
                                }
                            }
                        } else {
                            if (oldBookingDetailId == newBookingDetailId) {
                                list.add(bookingDetailStep);
                            } else {
                                if (oldBookingDetailStep.getStartTime().toLocalTime()
                                        .isBefore(bookingDetailStep.getEndTime().toLocalTime())) {
                                    bookingDetailStepCheckList.add(bookingDetailStep);
                                } else {
                                    list.add(bookingDetailStep);
                                }
                            }
                            count++;
                            checkIgnore = -1;
                            newBookingDetailId = 0;
                            map.put(count, list);
                        }
                    } else {
                        bookingDetailStepCheckList.add(bookingDetailStep);
                        if(bookingDetailStepDraftList.get(bookingDetailStepDraftList.size() - 1).equals(bookingDetailStep)) {
                            count++;
                            checkIgnore = -1;
                            newBookingDetailId = 0;
                            map.put(count, list);
                        }
                    }
                }
                if (bookingDetailStepCheckList.size() == 0) {
                    checkFinish = true;
                }
            }
            int check = countEmployee - count;
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
                Page<String> page = new PageImpl<>(timeBookingList,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()),
                        timeBookingList.size());
                return ResponseHelper.ok(page);
            }
            return ResponseHelper.ok(Notification.NO_EMPLOYEE_FREE);
        } else {
            return ResponseHelper.error(String.format(Notification.GET_FAILED, Constant.SPA_PACKAGE));
        }
    }

    @PostMapping("/userlocation/create")
    public Response createNewUserLocation(@RequestBody UserLocation userLocation) {
        UserLocation newUserLocation = userLocationService.insertNewUserLocation(userLocation);
        return ResponseHelper.ok(userLocation);
    }

    @PostMapping("/booking/create")
    public Response insertBooking(@RequestBody BookingRequest bookingRequest) {
        boolean checkNoEmployForBooking = false;
        SpaPackage spaPackageCheck = spaPackageService
                .findBySpaPackageId(bookingRequest.getBookingDataList().get(0).getPackageId());
        List<Staff> staffList = staffService.findBySpaId(spaPackageCheck.getSpa().getId());
        List<Consultant> consultantList =
                consultantService.findBySpaId(spaPackageCheck.getSpa().getId());
        for (BookingData bookingData : bookingRequest.getBookingDataList()) {
            spaPackageCheck = spaPackageService.findBySpaPackageId(bookingData.getPackageId());
            Time startTime = bookingData.getTimeBooking();
            List<DateOff> dateOffs =
                    dateOffService.findByDateOffAndSpaAndStatusApprove(bookingData.getDateBooking(),
                            spaPackageCheck.getSpa().getId());
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
                    LOGGER.info(String.format(Notification.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                    return ResponseHelper.error(String.format(Notification.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
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
                    LOGGER.info(String.format(Notification.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                    return ResponseHelper.error(String.format(Notification.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
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
            return ResponseHelper.error(Notification.CANNOT_BOOKING_AT_TIME);
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
            Spa spa = null;
            Customer customer = customerService.findByUserId(bookingRequest.getCustomerId());
            if (Objects.isNull(customer)) {
                ResponseHelper.error(String.format(Notification.GET_FAILED, Constant.CUSTOMER));
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
                bookingResult = booking;
                for (BookingData bookingData : bookingDataList) {
                    for (SpaTreatment spaTreatment : spaTreatmentList) {
                        if (spaTreatment.getSpaPackage().getId().equals(bookingData.getPackageId())) {
                            BookingDetail bookingDetail = new BookingDetail();
                            bookingDetail.setBooking(bookingResult);
                            bookingDetail.setSpaPackage(spaTreatment.getSpaPackage());
                            bookingDetail.setTotalPrice(spaTreatment.getTotalPrice());
                            bookingDetail.setStatusBooking(StatusBooking.BOOKING);
                            bookingDetail.setSpaTreatment(spaTreatment);
                            bookingDetail.setTotalTime(spaTreatment.getTotalTime());
                            bookingDetail.setType(spaTreatment.getSpaPackage().getType());
                            bookingDetailResultList.add(bookingDetail);
                            List<TreatmentService> treatmentServices =
                                    new ArrayList<>(spaTreatment.getTreatmentServices());
                            Collections.sort(treatmentServices);
                            Time startTime;
                            Time endTime = Time.valueOf(Constant.TIME_DEFAULT);
                            for (int i = 0; i < treatmentServices.size(); i++) {
                                BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
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
                        }
                    }
                }
            } else {
                bookingResult = booking;
                for (BookingData bookingData : bookingDataList) {
                    for (SpaPackage spaPackage : spaPackageList) {
                        if (spaPackage.getId().equals(bookingData.getPackageId())) {
                            if (spaPackage.getType().equals(Type.ONESTEP)) {
                                SpaTreatment spaTreatment =
                                        spaTreatmentService
                                                .findTreatmentBySpaPackageIdWithTypeOneStep(spaPackage.getId());
                                BookingDetail bookingDetail = new BookingDetail();
                                bookingDetail.setBooking(bookingResult);
                                bookingDetail.setSpaPackage(spaPackage);
                                bookingDetail.setSpaTreatment(spaTreatment);
                                bookingDetail.setStatusBooking(StatusBooking.BOOKING);
                                bookingDetail.setTotalPrice(spaTreatment.getTotalPrice());
                                bookingDetail.setTotalTime(spaTreatment.getTotalTime());
                                bookingDetail.setType(Type.ONESTEP);
                                bookingDetailResultList.add(bookingDetail);
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
                            } else {
                                BookingDetail bookingDetail = new BookingDetail();
                                bookingDetail.setBooking(bookingResult);
                                bookingDetail.setType(Type.MORESTEP);
                                bookingDetail.setSpaPackage(spaPackage);
                                bookingDetail.setStatusBooking(StatusBooking.BOOKING);
                                bookingDetailResultList.add(bookingDetail);
                                BookingDetailStep bookingDetailStep = new BookingDetailStep();
                                bookingDetailStep.setDateBooking(bookingData.getDateBooking());
                                bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                                bookingDetailStep.setIsConsultation(IsConsultation.TRUE);
                                bookingDetailStep.setBookingDetail(bookingDetail);
                                bookingDetailStep.setStartTime(bookingData.getTimeBooking());
                                bookingDetailStep.setEndTime(Time.valueOf(bookingData
                                        .getTimeBooking()
                                        .toLocalTime()
                                        .plusMinutes(Constant.DURATION_OF_CONSULTATION)));
                                bookingDetailStepResultList.add(bookingDetailStep);
                            }
                        }
                    }
                }
            }
            for (BookingDetailStep bookingDetailStep : bookingDetailStepResultList) {
                Time endTime = bookingDetailStep.getEndTime();
                if ((endTime.toLocalTime().isAfter(LocalTime.parse(Constant.TIME_START_RELAX))
                        && endTime.toLocalTime().isBefore(LocalTime.parse(Constant.TIME_END_RELAX)))
                        || endTime.toLocalTime().isAfter(LocalTime.parse(Constant.TIME_END_DATE))) {
                    checkCanInsert = false;
                }
            }
            if (checkCanInsert) {
                Booking bookingInsert = bookingService.insertNewBooking(bookingResult);
                if (Objects.nonNull(bookingInsert)) {
                    for (BookingDetail bookingDetail : bookingDetailResultList) {
                        BookingDetail bookingDetailCheck = bookingDetail;
                        bookingDetail.setBooking(bookingInsert);
                        BookingDetail bookingDetailInsert =
                                bookingDetailService.insertBookingDetail(bookingDetail);
                        if (Objects.nonNull(bookingDetailInsert)) {
                            for (BookingDetailStep bookingDetailStep : bookingDetailStepResultList) {
                                if (bookingDetailStep.getBookingDetail().equals(bookingDetailCheck)) {
                                    bookingDetailStep.setBookingDetail(bookingDetailInsert);
                                    BookingDetailStep bookingDetailStepInsert =
                                            bookingDetailStepService
                                                    .insertBookingDetailStep(bookingDetailStep);
                                    if (Objects.isNull(bookingDetailStepInsert)) {
                                        LOGGER.info(String.format(Notification.INSERT_FAILED, Constant.BOOKING_DETAIL_STEP));
                                        // delete booking + bookingDetail + bookingDetailStep inserted
                                        List<BookingDetail> bookingDetailDeleteList =
                                                bookingDetailService
                                                        .findByBooking(bookingDetailInsert.getId(),
                                                                PageRequest.of(Constant.PAGE_DEFAULT,
                                                                        Constant.SIZE_DEFAULT,
                                                                        Sort.unsorted()))
                                                        .toList();
                                        for (BookingDetail bdd : bookingDetailDeleteList) {
                                            List<BookingDetailStep> bookingDetailStepDeleteList =
                                                    bookingDetailStepService
                                                            .findByBookingDetail(bdd.getId(),
                                                                    PageRequest.of(Constant.PAGE_DEFAULT,
                                                                            Constant.SIZE_DEFAULT,
                                                                            Sort.unsorted()))
                                                            .toList();
                                            for (BookingDetailStep bdsd : bookingDetailStepDeleteList) {
                                                bookingDetailStepService.removeDB(bdsd.getId());
                                            }
                                            bookingDetailService.removeDB(bdd.getId());
                                        }
                                        bookingService.removeDB(bookingInsert.getId());
                                        return ResponseHelper.error(String.format(Notification.INSERT_FAILED, Constant.BOOKING));
                                    }
                                }
                            }
                        } else {
                            LOGGER.info(String.format(Notification.INSERT_FAILED, Constant.BOOKING_DETAIL));
                        }
                    }
                    return ResponseHelper.ok(String.format(Notification.INSERT_SUCCESS, Constant.BOOKING));
                } else {
                    LOGGER.info(String.format(Notification.INSERT_FAILED, Constant.BOOKING));
                }
            } else {
                return ResponseHelper.error(Notification.BOOKING_OVERTIME);
            }
            return ResponseHelper.error(String.format(Notification.INSERT_FAILED, Constant.BOOKING));
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
            return ResponseHelper.error(String.format(Notification.EDIT_FAILED, Constant.USER_LOCATION
            ));
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
        return ResponseHelper.error(String.format(Notification.EDIT_FAILED, Constant.PROFILE));
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
}
