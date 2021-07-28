package swp490.spa.rest;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.BookingDetailEditRequest;
import swp490.spa.dto.requests.BookingDetailStepRequest;
import swp490.spa.dto.requests.DateOffRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.entities.SpaService;
import swp490.spa.services.*;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.image.UploadImage;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;
import swp490.spa.utils.support.templates.MessageTemplate;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin
public class ConsultantController {
    private Logger LOGGER = LogManager.getLogger(ConsultantController.class);
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private BookingDetailService bookingDetailService;
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private DateOffService dateOffService;
    @Autowired
    private ConsultationContentService consultationContentService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private SpaServiceService spaServiceService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private NotificationFireBaseService notificationFireBaseService;
    @Autowired
    private NotificationService notificationService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public ConsultantController(ConsultantService consultantService, UserService userService,
                                DateOffService dateOffService, BookingService bookingService,
                                BookingDetailService bookingDetailService, StaffService staffService,
                                BookingDetailStepService bookingDetailStepService,
                                SpaTreatmentService spaTreatmentService, SpaServiceService spaServiceService,
                                ConsultationContentService consultationContentService, ManagerService managerService,
                                NotificationFireBaseService notificationFireBaseService,
                                RatingService ratingService, NotificationService notificationService) {
        this.consultantService = consultantService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.staffService = staffService;
        this.bookingDetailService = bookingDetailService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.managerService = managerService;
        this.spaServiceService = spaServiceService;
        this.spaTreatmentService = spaTreatmentService;
        this.consultationContentService = consultationContentService;
        this.ratingService = ratingService;
        this.notificationFireBaseService = notificationFireBaseService;
        this.notificationService = notificationService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions(bookingDetailStepService, bookingDetailService);
    }

    @PostMapping("/dateoff/create/{consultantId}")
    public Response insertNewDateOff(@PathVariable Integer consultantId,
                                     @RequestBody DateOffRequest dateOffRequest) throws FirebaseMessagingException {
        Date dateRegister = Date.valueOf(dateOffRequest.getDateOff());
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByDateBookingAndConsultant(dateRegister,
                        consultantId);
        if (bookingDetailSteps.size() == 0) {
            DateOff dateOff = new DateOff();
            Consultant consultant = consultantService.findByConsultantId(consultantId);
            List<Manager> managers =
                    managerService.findManagerBySpaAndStatusAvailable(consultant.getSpa().getId());
            dateOff.setStatusDateOff(StatusDateOff.WAITING);
            dateOff.setReasonCancel(dateOffRequest.getReasonDateOff());
            dateOff.setEmployee(consultant.getUser());
            dateOff.setSpa(consultant.getSpa());
            DateOff dateOffResult = dateOffService.insertNewDateOff(dateOff);
            if (Objects.nonNull(dateOffResult)) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                Map<String, String> map = new HashMap<>();
                map.put(MessageTemplate.REGISTER_DATE_OFF_STATUS, "- dateOffId "
                        + dateOffResult.getId());
                if (notificationFireBaseService.notify(MessageTemplate.REGISTER_DATE_OFF_TITLE,
                        String.format(MessageTemplate.REGISTER_DATE_OFF_MESSAGE,
                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                        map, managers.get(0).getUser().getId(), Role.MANAGER)) {
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.DATE_OFF));
                } else {
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.DATE_OFF));
                }
            }
        } else {
            return ResponseHelper.error(String.format(LoggingTemplate.BOOKING_SERVICE_EXISTED));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.DATE_OFF));
    }

    @GetMapping("/findById/{id}")
    public Response getProfile(@PathVariable Integer id) {
        Consultant consultant = consultantService.findByConsultantId(id);
        if (Objects.nonNull(consultant)) {
            return ResponseHelper.ok(consultant);
        }
        LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
    }

    @PutMapping("/editprofile")
    public Response editProfileStaff(@RequestBody User user) {
        Consultant consultant = consultantService.findByConsultantId(user.getId());
        if (Objects.nonNull(consultant)) {
            User userResult = consultant.getUser();
            userResult.setFullname(user.getFullname());
            userResult.setEmail(user.getEmail());
            userResult.setAddress(user.getAddress());
            userResult.setBirthdate(user.getBirthdate());
            userResult.setGender(user.getGender());
            if (Objects.nonNull(userService.editUser(user))) {
                return ResponseHelper.ok(userResult);
            }
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.PROFILE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account) {
        Consultant consultant = consultantService.findByConsultantId(account.getId());
        User oldUser = consultant.getUser();
        User updateUser = consultant.getUser();
        updateUser.setPassword(account.getPassword());
        if (Objects.nonNull(userService.editUser(updateUser))) {
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }

    @PutMapping("/consultationcontent/edit")
    public Response editConsultationContent(@RequestBody ConsultationContent consultationContent) {
        ConsultationContent consultationContentEdit =
                consultationContentService.findByConsultationContentId(consultationContent.getId());
        if (Objects.nonNull(consultationContentEdit)) {
            if (Objects.nonNull(consultationContent.getDescription())) {
                consultationContentEdit.setDescription(consultationContent.getDescription());
            }
            if (Objects.nonNull(consultationContent.getExpectation())) {
                consultationContentEdit.setExpectation(consultationContent.getExpectation());
            }
            if (Objects.nonNull(consultationContent.getResult())) {
                consultationContentEdit.setResult(consultationContent.getResult());
            }
            if (Objects.nonNull(consultationContent.getNote())) {
                consultationContentEdit.setNote(consultationContent.getNote());
            }
            ConsultationContent consultationContentResult =
                    consultationContentService.editByConsultationContent(consultationContentEdit);
            if (Objects.isNull(consultationContentResult)) {
                LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
            }
            LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.CONSULTATION_CONTENT));
            return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.BOOKING_DETAIL));
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTATION_CONTENT));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
    }

    @PutMapping("/bookingdetailstep/addtreatment")
    public Response editBookingDetail(@RequestBody BookingDetailEditRequest bookingDetailRequest) throws FirebaseMessagingException {
        Booking bookingBeforeEdit;
        BookingDetail bookingDetailEdit = null;
        List<BookingDetailStep> bookingDetailStepEditList = new ArrayList<>();
        List<ConsultationContent> consultationContentList = new ArrayList<>();
        List<ConsultationContent> consultationContentResultList = new ArrayList<>();
        boolean checkFinish = true;
        Rating rating = null;
        Integer totalTime = 0;
        Double totalPrice = 0.0;
        //Get Booking to edit
        BookingDetail bookingDetailBeforeEdit =
                bookingDetailService.findByBookingDetailId(bookingDetailRequest.getBookingDetailId());
        if (Objects.nonNull(bookingDetailBeforeEdit)) {
            bookingBeforeEdit = bookingDetailBeforeEdit.getBooking();
            // Get totalPrice and totalTime from another booking detail
            for (BookingDetail bookingDetail : bookingBeforeEdit.getBookingDetails()) {
                if (bookingDetail.getTotalPrice() != null && bookingDetail.getTotalTime() != null) {
                    totalPrice += bookingDetail.getTotalPrice();
                    totalTime += bookingDetail.getTotalTime();
                }
                if (bookingDetail.getBookingDetailSteps().size() == 1
                        && bookingDetailBeforeEdit.getId().equals(bookingDetail.getId())) {
                    bookingDetail.getBookingDetailSteps().get(0).setStatusBooking(StatusBooking.FINISH);
                    bookingDetailEdit = bookingDetail;
                }
            }
            // Get SpaTreatment to get price And time from Treatment chosen
            // and setting bookingDetailEdit
            SpaTreatment spaTreatment =
                    spaTreatmentService.findByTreatmentId(bookingDetailRequest.getSpaTreatmentId());
            totalPrice += spaTreatment.getTotalPrice();
            totalTime += spaTreatment.getTotalTime();
            bookingBeforeEdit.setTotalPrice(totalPrice);
            bookingBeforeEdit.setTotalTime(totalTime);
            bookingDetailEdit.setStatusBooking(StatusBooking.PENDING);
            bookingDetailEdit.setTotalPrice(spaTreatment.getTotalPrice());
            bookingDetailEdit.setTotalTime(spaTreatment.getTotalTime());
            bookingDetailEdit.setSpaTreatment(spaTreatment);
            // Get consultant to set into bookingDetailStep
            Consultant consultant =
                    consultantService.findByConsultantId(bookingDetailRequest.getConsultantId());
            if (Objects.nonNull(consultant)) {
                // for TreatmentService - get Service to prepare list booking detail step
                List<TreatmentService> treatmentServiceList =
                        new ArrayList<>(spaTreatment.getTreatmentServices());
                Collections.sort(treatmentServiceList);
                for (int i = 0; i < treatmentServiceList.size(); i++) {
                    TreatmentService treatmentService = treatmentServiceList.get(i);
                    BookingDetailStep bookingDetailStep = new BookingDetailStep();
                    if (i == 0) {
                        bookingDetailStep.setDateBooking(Date.valueOf(bookingDetailRequest.getDateBooking()));
                        bookingDetailStep.setStartTime(Time.valueOf(bookingDetailRequest.getTimeBooking()));
                        Time endTime =
                                Time.valueOf(Time.valueOf(bookingDetailRequest.getTimeBooking()).toLocalTime()
                                        .plusMinutes(treatmentService.getSpaService().getDurationMin()));
                        bookingDetailStep.setEndTime(endTime);
                        bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                    } else {
                        bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
                    }
                    bookingDetailStep.setBookingPrice(treatmentService.getSpaService().getPrice());
                    bookingDetailStep.setIsConsultation(IsConsultation.FALSE);
                    bookingDetailStep.setTreatmentService(treatmentService);
                    bookingDetailStep.setConsultant(consultant);
                    bookingDetailStep.setBookingDetail(bookingDetailEdit);
                    bookingDetailStepEditList.add(bookingDetailStep);
                }
                bookingDetailEdit.getBookingDetailSteps().addAll(bookingDetailStepEditList);
            }
            // set bookingDetail into booking before insert
            for (BookingDetail bookingDetail : bookingBeforeEdit.getBookingDetails()) {
                if (bookingDetail.getBookingDetailSteps().size() == 1) {
                    bookingDetail = bookingDetailEdit;
                }
            }
            Booking bookingResult = bookingService.editBookingByAddTreatment(bookingBeforeEdit);
            if (Objects.nonNull(bookingResult)) {
                // Create consultation content base on bookingDetailStep created
                for (BookingDetail bookingDetail : bookingResult.getBookingDetails()) {
                    if (bookingDetail.getType().equals(Type.MORESTEP)) {
                        for (BookingDetailStep bookingDetailStep : bookingDetail.getBookingDetailSteps()) {
                            if (bookingDetailStep.getIsConsultation().equals(IsConsultation.FALSE)) {
                                ConsultationContent consultationContent = new ConsultationContent();
                                consultationContent.setBookingDetailStep(bookingDetailStep);
                                consultationContent.setDescription(bookingDetailStep.getTreatmentService()
                                        .getSpaService().getDescription());
                                consultationContentList.add(consultationContent);
                                ConsultationContent result =
                                        consultationContentService.insertNewConsultationContent(consultationContent);
                                if (result == null) {
                                    LOGGER.error(String.format(LoggingTemplate.INSERT_FAILED,
                                            Constant.CONSULTATION_CONTENT));
                                    checkFinish = false;
                                } else {
                                    consultationContentResultList.add(result);
                                }
                            }
                        }
                    }
                }
            }
            if (!checkFinish) {
                for (ConsultationContent consultationContent : consultationContentResultList) {
                    consultationContentService.removeDB(consultationContent.getId());
                }
                for (ConsultationContent consultationContent : consultationContentList) {
                    consultationContentService.insertNewConsultationContent(consultationContent);
                }
            } else {
                for (ConsultationContent consultationContent : consultationContentResultList) {
                    BookingDetailStep bookingDetailStep = consultationContent.getBookingDetailStep();
                    bookingDetailStep.setConsultationContent(consultationContent);
                    if (Objects.nonNull(bookingDetailStepService.editBookingDetailStep(bookingDetailStep))) {
                        LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS,
                                Constant.BOOKING_DETAIL_STEP));
                    }
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                Customer customer = bookingResult.getCustomer();
                Map<String, String> map = new HashMap<>();
                map.put(MessageTemplate.FINISH_STATUS, "- bookingDetailStepId "
                        + bookingDetailBeforeEdit.getBookingDetailSteps().get(0).getId());
                if (notificationFireBaseService.notify(MessageTemplate.FINISH_TITLE,
                        String.format(MessageTemplate.FINISH_CONSULTATION_MESSAGE,
                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                        map, customer.getUser().getId(), Role.CUSTOMER)) {
                    Notification notification = new Notification();
                    notification.setRole(Role.CUSTOMER);
                    notification.setTitle(MessageTemplate.FINISH_TITLE);
                    notification.setMessage(String.format(MessageTemplate.FINISH_CONSULTATION_MESSAGE,
                            LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)));
                    notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                    notification.setType(Constant.STEP_FINISH_TYPE);
                    notification.setUser(customer.getUser());
                    notificationService.insertNewNotification(notification);
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,
                            Constant.BOOKING_DETAIL_TREATMENT));
                } else {
                    Notification notification = new Notification();
                    notification.setRole(Role.CUSTOMER);
                    notification.setTitle(MessageTemplate.FINISH_TITLE);
                    notification.setMessage(String.format(MessageTemplate.FINISH_CONSULTATION_MESSAGE,
                            LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)));
                    notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                    notification.setType(Constant.STEP_FINISH_TYPE);
                    notification.setUser(customer.getUser());
                    notificationService.insertNewNotification(notification);
                    return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,
                            Constant.BOOKING_DETAIL_TREATMENT));
                }
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
    }

    @GetMapping("/booking/findbybookingstatus")
    public Response findByBookingStatusAndSpa(@RequestParam StatusBooking statusBooking,
                                              @RequestParam Integer spaId,
                                              Pageable pageable) {
        Page<Booking> bookings =
                bookingService.findByBookingStatusAndSpa(statusBooking, spaId, pageable);
        if (Objects.nonNull(bookings)) {
            return ResponseHelper.ok(conversion.convertToPageBookingResponse(bookings));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING));
    }

    @GetMapping("/bookingdetail/findbybookingid")
    public Response findByBooking(@RequestParam Integer bookingId,
                                  Pageable pageable) {
        Page<BookingDetail> bookingDetails =
                bookingDetailService.findByBooking(bookingId, pageable);
        if (Objects.nonNull(bookingDetails)) {
            return ResponseHelper.ok(conversion.convertToPageBookingDetailResponse(bookingDetails));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
    }

    @GetMapping("/spatreatment/findbyspapackage")
    public Response findSpaTreatmentBySpaPackage(@RequestParam Integer spaPackageId,
                                                 Pageable pageable) {
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(spaPackageId,
                        Constant.SEARCH_NO_CONTENT, pageable);
        if (Objects.nonNull(spaTreatments)) {
            return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_TREATMENT));
    }

    @GetMapping("/workingofconsultant/findbydatechosen/{consultantId}")
    public Response findWorkingOfConsultantByDateChosen(@PathVariable Integer consultantId,
                                                        @RequestParam String dateChosen) {
        Consultant consultant = consultantService.findByConsultantId(consultantId);
        if (Objects.nonNull(consultant)) {
            Page<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByConsultantIdAndDateBooking(consultantId, Date.valueOf(dateChosen),
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_MAX, Sort.unsorted()));
            if (Objects.nonNull(bookingDetailSteps)) {
                return ResponseHelper.ok(conversion.convertToPageBookingDetailStepResponse(bookingDetailSteps));
            } else {
                LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
        }
    }

    @GetMapping("/getListCustomerOfConsultant/{consultantId}")
    public Response getListCustomerOfConsultant(@PathVariable Integer consultantId) {
        List<User> userList = new ArrayList<>();
        List<Booking> bookings = new ArrayList<>();
        List<BookingDetail> bookingDetails = new ArrayList<>();
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByConsultantIdAndStatusBookingPendingBooking(consultantId);
        if (Objects.nonNull(bookingDetailSteps)) {
            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
                if (bookingDetails.size() == 0) {
                    bookingDetails.add(bookingDetail);
                } else {
                    if (!supportFunctions.checkBookingDetailExistedInList(bookingDetail, bookingDetails)) {
                        bookingDetails.add(bookingDetail);
                    }
                }
            }
            for (BookingDetail bookingDetail : bookingDetails) {
                Booking booking = bookingDetail.getBooking();
                if (bookings.size() == 0) {
                    bookings.add(booking);
                } else {
                    if (!supportFunctions.checkBookingExistedInList(booking, bookings)) {
                        bookings.add(booking);
                    }
                }
            }
            for (Booking booking : bookings) {
                User customer = booking.getCustomer().getUser();
                if (userList.size() == 0) {
                    userList.add(customer);
                } else {
                    if (!supportFunctions.checkUserExistedInList(customer, userList)) {
                        userList.add(customer);
                    }
                }
            }
            Page result = new PageImpl(userList,
                    PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                    userList.size());
            return ResponseHelper.ok(result);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CUSTOMER));
    }

    @GetMapping("/bookingDetail/findByCustomerAndConsultant/{customerId}/{consultantId}")
    public Response findBookingDetailByCustomerAndConsultant(@PathVariable Integer customerId,
                                                             @PathVariable Integer consultantId) {
        List<BookingDetail> bookingDetailResult = new ArrayList<>();
        Consultant consultant = consultantService.findByConsultantId(consultantId);
        if (Objects.nonNull(consultant)) {
            List<BookingDetail> bookingDetails = bookingDetailService
                    .findByCustomerAndSpa(customerId, consultant.getSpa().getId());
            for (BookingDetail bookingDetail : bookingDetails) {
                List<BookingDetailStep> bookingDetailStepCheck =
                        bookingDetail.getBookingDetailSteps();
                for (BookingDetailStep bookingDetailStep : bookingDetailStepCheck) {
                    if (bookingDetailStep.getConsultant() != null) {
                        if (bookingDetailStep.getConsultant().equals(consultant)) {
                            if (bookingDetailResult.size() == 0) {
                                bookingDetailResult.add(bookingDetail);
                            } else {
                                if (!supportFunctions.checkBookingDetailExistedInList(bookingDetail, bookingDetailResult)) {
                                    bookingDetailResult.add(bookingDetail);
                                }
                            }
                        }
                    }
                }
            }
            Page<BookingDetail> page = new PageImpl<>(bookingDetailResult,
                    PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                    bookingDetailResult.size());
            return ResponseHelper.ok(page);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
    }

    @GetMapping("/bookingDetailStep/findByBookingDetail/{bookingDetailId}")
    public Response findBookingDetailStepByBookingDetail(@PathVariable Integer bookingDetailId) {
        Page<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByBookingDetail(bookingDetailId,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()));
        if (Objects.nonNull(bookingDetailSteps)) {
            return ResponseHelper.ok(bookingDetailSteps);
        }
        LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED,
                Constant.BOOKING_DETAIL_STEP));
    }

    @GetMapping("/getListTimeBookingForAddTreatment")
    public Response getListTimeBookingForAddTreatment(@RequestParam Integer spaTreatmentId,
                                                      @RequestParam String dateBooking,
                                                      @RequestParam Integer customerId,
                                                      @RequestParam Integer consultantId,
                                                      @RequestParam Integer spaId) {
        int countEmployee = 0;
        List<DateOff> dateOffs = null;
        List<Staff> staffs = null;
        List<BookingDetailStep> bookingDetailSteps = new ArrayList<>();
        // Get List Staff and All Booking Detail Step List
        Consultant consultant = consultantService.findByConsultantId(consultantId);
        SpaTreatment spaTreatment = spaTreatmentService.findByTreatmentId(spaTreatmentId);
        if (Objects.nonNull(spaTreatment)) {
            dateOffs = dateOffService.findByDateOffAndSpaAndStatusApprove(Date.valueOf(dateBooking),
                    spaId);
            if (dateOffs.size() != 0) {
                for (DateOff dateOff : dateOffs) {
                    if (dateOff.getEmployee().getId().equals(consultant.getUser().getId())) {
                        return ResponseHelper.ok(String.format(LoggingTemplate.CONSULTANT_DATE_OFF,
                                dateBooking));
                    }
                }
                staffs = staffService.findBySpaIdAndStatusAvailable(spaId);
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
            } else {
                staffs = staffService.findBySpaIdAndStatusAvailable(spaId);
            }
            countEmployee = staffs.size();
            bookingDetailSteps = bookingDetailStepService
                    .findByDateBookingAndIsConsultationAndSpa(Date.valueOf(dateBooking),
                            IsConsultation.FALSE, spaId);
            /*
                Separate bookingDetailSteps into lists with incrementation time
                and put into map
            */
            Map<Integer, List<BookingDetailStep>> map =
                    supportFunctions.separateBookingDetailStepListAndPutIntoMap(bookingDetailSteps);
            int check = countEmployee - map.size();
            List<String> timeBookingList = null;
            timeBookingList =
                    supportFunctions.getBookTime(spaTreatment.getTotalTime(), map, check);
            if (timeBookingList.size() != 0) {
                timeBookingList =
                        supportFunctions.checkAndGetListTimeBookingByCustomer(customerId, timeBookingList,
                                dateBooking);
                Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
                Date dateCheck = Date.valueOf(LocalDate.parse(dateBooking));
                if(currentDate.compareTo(dateCheck) == 0){
                    List<String> listTimeGet = new ArrayList<>();
                    Time currentTime = Time.valueOf(LocalTime.now(ZoneId.of(Constant.ZONE_ID)));
                    for (String time: timeBookingList) {
                        Time checkTime = Time.valueOf(time);
                        if(checkTime.compareTo(currentTime)>0){
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
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_TREATMENT));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.TIME_LIST));
    }

    @GetMapping("/spaTreatment/{spaPackageId}")
    public Response findSpaTreatmentMoreStepBySpaPackageId(@PathVariable Integer spaPackageId) {
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(spaPackageId, Constant.SEARCH_NO_CONTENT,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()));
        if (Objects.nonNull(spaTreatments)) {
            return ResponseHelper.ok(spaTreatments);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_TREATMENT));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_TREATMENT));
        }
    }

    @GetMapping("/getListTimeBookingForAStep")
    public Response getListTimeBookingForAStep(@RequestParam Integer spaServiceId,
                                               @RequestParam Integer bookingDetailStepId,
                                               @RequestParam String dateBooking,
                                               @RequestParam Integer customerId,
                                               @RequestParam Integer spaId) {
        List<DateOff> dateOffs = null;
        List<BookingDetailStep> bookingDetailSteps = new ArrayList<>();
        BookingDetailStep bookingDetailStep =
                bookingDetailStepService.findById(bookingDetailStepId);
        if (Objects.nonNull(bookingDetailStep)) {
            Consultant consultant = bookingDetailStep.getConsultant();
            Staff staff = bookingDetailStep.getStaff();
            SpaService spaService = spaServiceService.findBySpaServiceId(spaServiceId);
            if (Objects.nonNull(spaService)) {
                dateOffs = dateOffService.findByDateOffAndSpaAndStatusApprove(Date.valueOf(dateBooking),
                        spaId);
                if (dateOffs.size() != 0) {
                    for (DateOff dateOff : dateOffs) {
                        if (dateOff.getEmployee().getId().equals(consultant.getUser().getId())) {
                            return ResponseHelper.ok(String.format(LoggingTemplate.CONSULTANT_DATE_OFF,
                                    dateBooking));
                        }
                        if (dateOff.getEmployee().getId().equals(staff.getUser().getId())) {
                            return ResponseHelper.ok(String.format(LoggingTemplate.STAFF_DATE_OFF,
                                    dateBooking));
                        }
                    }
                }
                bookingDetailSteps =
                        bookingDetailStepService.findByDateBookingAndStaff(Date.valueOf(dateBooking),
                                staff.getId());
                Map<Integer, List<BookingDetailStep>> map = new HashMap<>();
                map.put(staff.getId(), bookingDetailSteps);
                List<String> timeBookingList = null;
                timeBookingList =
                        supportFunctions.getBookTime(spaService.getDurationMin(), map, 0);
                if (timeBookingList.size() != 0) {
                    timeBookingList =
                            supportFunctions.checkAndGetListTimeBookingByCustomer(customerId, timeBookingList,
                                    dateBooking);
                    Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
                    Date dateCheck = Date.valueOf(LocalDate.parse(dateBooking));
                    if(currentDate.compareTo(dateCheck) == 0){
                        List<String> listTimeGet = new ArrayList<>();
                        Time currentTime = Time.valueOf(LocalTime.now(ZoneId.of(Constant.ZONE_ID)));
                        for (String time: timeBookingList) {
                            Time checkTime = Time.valueOf(time);
                            if(checkTime.compareTo(currentTime)>0){
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
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.SERVICE));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.TIME_LIST));
    }

    @PutMapping("/bookingDetailStep/requestChangeStaff")
    public Response requestChangeStaffForMoreStep(@RequestBody BookingDetailStep requestChangeStaff) throws FirebaseMessagingException {
        BookingDetailStep bookingDetailStep =
                bookingDetailStepService.findById(requestChangeStaff.getId());
        if (Objects.nonNull(bookingDetailStep)) {
            bookingDetailStep.setReason(Constant.CHANGE_STAFF_STATUS_REASON + "-" +
                    requestChangeStaff.getReason());
            bookingDetailStep.setStatusBooking(StatusBooking.CHANGE_STAFF);
            BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
            bookingDetail.setStatusBooking(StatusBooking.CHANGE_STAFF);
            if (Objects.nonNull(bookingDetailStepService.editBookingDetailStep(bookingDetailStep)) &&
                    Objects.nonNull(bookingDetailService.editBookingDetail(bookingDetail))) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                List<Manager> managers =
                        managerService.findManagerBySpaAndStatusAvailable(bookingDetail.getBooking().getSpa().getId());
                Map<String, String> map = new HashMap<>();
                map.put(MessageTemplate.CHANGE_STAFF_STATUS, "- bookingDetailId "
                        + bookingDetail.getId().toString());
                if (notificationFireBaseService.notify(MessageTemplate.CHANGE_STAFF_TITLE,
                        String.format(MessageTemplate.CHANGE_STAFF_MESSAGE,
                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                        map, managers.get(0).getUser().getId(), Role.MANAGER)) {
                    return ResponseHelper.ok(LoggingTemplate.REQUEST_CHANGE_STAFF_SUCCESS);
                } else {
                    return ResponseHelper.ok(LoggingTemplate.REQUEST_CHANGE_STAFF_SUCCESS);
                }
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        }
        return ResponseHelper.error(LoggingTemplate.REQUEST_CHANGE_STAFF_FAILED);
    }

    @PutMapping("/bookingDetailStep/addTimeNextStep")
    public Response addTimeForNextStep(@RequestBody BookingDetailStepRequest bookingDetailStepRequest) {
        BookingDetailStep bookingDetailStep =
                bookingDetailStepService.findById(bookingDetailStepRequest.getBookingDetailStepId());
        int duration = bookingDetailStep.getTreatmentService().getSpaService().getDurationMin();
        Time starTime = Time.valueOf(bookingDetailStepRequest.getTimeBooking());
        Time endTime = Time.valueOf(starTime.toLocalTime().plusMinutes(duration));
        bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
        bookingDetailStep.setStartTime(starTime);
        bookingDetailStep.setEndTime(endTime);
        bookingDetailStep.setDateBooking(Date.valueOf(bookingDetailStepRequest.getDateBooking()));
        if (Objects.nonNull(bookingDetailStepService.editBookingDetailStep(bookingDetailStep))) {
            return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,
                    Constant.TIME_NEXT_STEP));
        }
        LOGGER.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.TIME_NEXT_STEP));
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED,
                Constant.TIME_NEXT_STEP));
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
}
