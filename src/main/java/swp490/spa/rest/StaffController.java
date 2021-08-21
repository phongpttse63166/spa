package swp490.spa.rest;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.*;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
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
@RequestMapping("/api/staff")
@CrossOrigin
public class StaffController {
    private Logger LOGGER = LogManager.getLogger(StaffController.class);
    @Autowired
    private StaffService staffService;
    @Autowired
    private UserService userService;
    @Autowired
    private DateOffService dateOffService;
    @Autowired
    private BookingDetailService bookingDetailService;
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ConsultationContentService consultationContentService;
    @Autowired
    private RatingService ratingService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private NotificationFireBaseService notificationFireBaseService;
    @Autowired
    private NotificationService notificationService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public StaffController(StaffService staffService, UserService userService,
                           DateOffService dateOffService, RatingService ratingService,
                           BookingDetailStepService bookingDetailStepService,
                           ConsultationContentService consultationContentService,
                           BookingDetailService bookingDetailService, BookingService bookingService,
                           NotificationFireBaseService notificationFireBaseService,
                           NotificationService notificationService, ManagerService managerService) {
        this.staffService = staffService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.consultationContentService = consultationContentService;
        this.bookingDetailService = bookingDetailService;
        this.bookingService = bookingService;
        this.ratingService = ratingService;
        this.managerService = managerService;
        this.notificationFireBaseService = notificationFireBaseService;
        this.notificationService = notificationService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
    }

    @GetMapping("/findbyId")
    public Response findStaffById(@RequestParam Integer userId) {
        Staff staff = staffService.findByStaffId(userId);
        return ResponseHelper.ok(staff);
    }

    @PutMapping("/editProfile")
    public Response editProfileStaff(@RequestBody User user) {
        Staff staffResult = staffService.findByStaffId(user.getId());
        if (Objects.nonNull(staffResult)) {
            User userEdit = staffResult.getUser();
            userEdit.setFullname(user.getFullname());
            userEdit.setEmail(user.getEmail());
            userEdit.setAddress(user.getAddress());
            userEdit.setBirthdate(user.getBirthdate());
            userEdit.setGender(user.getGender());
            User userResult = userService.editUser(user);
            if (Objects.nonNull(userResult)) {
                return ResponseHelper.ok(userResult);
            }
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.PROFILE));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account) {
        Staff staff = staffService.findByStaffId(account.getId());
        User oldUser = staff.getUser();
        User updateUser = staff.getUser();
        updateUser.setPassword(account.getPassword());
        if (Objects.nonNull(userService.editUser(updateUser))) {
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }

    @PostMapping("/dateoff/create/{staffId}")
    public Response insertNewDateOff(@PathVariable Integer staffId,
                                     @RequestBody DateOffRequest dateOffRequest) throws FirebaseMessagingException {
        Date dateRegister = Date.valueOf(dateOffRequest.getDateOff());
        DateOff dateOffGet = dateOffService.findByEmployeeAndDateOff(staffId, dateRegister);
        if (dateOffGet != null) {
            return ResponseHelper.error(LoggingTemplate.DATE_OFF_REGISTERED);
        } else {
            List<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByDateBookingAndStaff(dateRegister,
                            staffId);
            if (bookingDetailSteps.size() == 0) {
                DateOff dateOff = new DateOff();
                Staff staff = staffService.findByStaffId(staffId);
                List<Manager> managers =
                        managerService.findManagerBySpaAndStatusAvailable(staff.getSpa().getId());
                dateOff.setStatusDateOff(StatusDateOff.WAITING);
                dateOff.setReasonDateOff(dateOffRequest.getReasonDateOff());
                dateOff.setDateOff(dateRegister);
                dateOff.setEmployee(staff.getUser());
                dateOff.setSpa(staff.getSpa());
                DateOff dateOffResult = dateOffService.insertNewDateOff(dateOff);
                if (Objects.nonNull(dateOffResult)) {
                    if (Objects.nonNull(dateOffResult)) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                        Map<String, String> map = new HashMap<>();
                        map.put(MessageTemplate.REGISTER_DATE_OFF_STATUS,
                                MessageTemplate.REGISTER_DATE_OFF_STATUS + "- dateOffId "
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
                }
            } else {
                return ResponseHelper.error(String.format(LoggingTemplate.BOOKING_SERVICE_EXISTED));
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.DATE_OFF));
    }

    @GetMapping("/workingofstaff/findbydatechosen/{staffId}")
    public Response findWorkingOfStaffByDateChosen(@PathVariable Integer staffId,
                                                   @RequestParam String dateChosen) {
        Staff staff = staffService.findByStaffId(staffId);
        if (Objects.nonNull(staff)) {
            Page<BookingDetailStep> bookingDetailStepPage =
                    bookingDetailStepService.findByStaffIdAndDateBooking(staffId, Date.valueOf(dateChosen),
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()));
            if (Objects.nonNull(bookingDetailStepPage)) {
                List<BookingDetailStep> result = new ArrayList<>();
                List<BookingDetail> bookingDetails = new ArrayList<>();
                List<BookingDetailStep> bookingDetailSteps = bookingDetailStepPage.getContent();
                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                    if (result.size() == 0) {
                        result.add(bookingDetailStep);
                        bookingDetails.add(bookingDetailStep.getBookingDetail());
                    } else {
                        BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
                        if (!supportFunctions.checkBookingDetailExistedInList(bookingDetail, bookingDetails)) {
                            result.add(bookingDetailStep);
                            bookingDetails.add(bookingDetailStep.getBookingDetail());
                        }
                    }
                }
                bookingDetailStepPage = new PageImpl<>(result,
                        PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()),
                        result.size());
                return ResponseHelper.ok(conversion.convertToPageBookingDetailStepResponse(bookingDetailStepPage));
            } else {
                LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
                return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
            }
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
            return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.STAFF));
        }
    }

    @PutMapping("/bookingDetailStep/confirmFinishAStep")
    public Response confirmFinishAStep(@RequestBody ConfirmAStepRequest confirmAStepRequest)
            throws FirebaseMessagingException {
        boolean checkFinishAll = false;
        BookingDetailStep bookingDetailStep =
                bookingDetailStepService.findById(confirmAStepRequest.getBookingDetailStepId());
        if (Objects.nonNull(bookingDetailStep)) {
            Staff staff = staffService.findByStaffId(confirmAStepRequest.getStaffId());
            if (staff.equals(bookingDetailStep.getStaff())) {
                Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
                if (currentDate.compareTo(bookingDetailStep.getDateBooking()) == 0 ||
                        currentDate.compareTo(bookingDetailStep.getDateBooking()) == 1) {
                    Time currentTime = Time.valueOf(LocalTime.now(ZoneId.of(Constant.ZONE_ID)));
                    Time confirmTime = Time.valueOf(bookingDetailStep.getStartTime().toLocalTime().plusMinutes(15));
                    if (currentTime.compareTo(confirmTime) > 0) {
                        ConsultationContent consultationContentGet = bookingDetailStep.getConsultationContent();
                        consultationContentGet.setResult(confirmAStepRequest.getResult());
                        if (Objects.nonNull(consultationContentService.editByConsultationContent(consultationContentGet))) {
                            Rating rating = new Rating();
                            rating.setStatusRating(StatusRating.WAITING);
                            rating.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                            rating.setExpireTime(Date.valueOf(LocalDateTime.now().plusDays(3).toLocalDate()));
                            rating.setCustomer(bookingDetailStep.getBookingDetail().getBooking().getCustomer());
                            Rating ratingNew = ratingService.insertNewRating(rating);
                            if (Objects.nonNull(ratingNew)) {
                                bookingDetailStep.setRating(rating);
                                bookingDetailStep.setStatusBooking(StatusBooking.FINISH);
                                BookingDetailStep bookingDetailStepEdited =
                                        bookingDetailStepService.editBookingDetailStep(bookingDetailStep);
                                if (Objects.nonNull(bookingDetailStepEdited)) {
                                    int ordinal = bookingDetailStep.getTreatmentService().getOrdinal();
                                    if (ordinal == bookingDetailStep.getBookingDetail().getBookingDetailSteps().size() - 1) {
                                        checkFinishAll = true;
                                    }

                                    Customer customer = bookingDetailStep.getBookingDetail().getBooking().getCustomer();
                                    Map<String, String> map = new HashMap<>();
                                    if (checkFinishAll) {
                                        BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
                                        bookingDetail.setStatusBooking(StatusBooking.FINISH);
                                        BookingDetail bookingDetailEdited =
                                                bookingDetailService.editBookingDetail(bookingDetail);
                                        if (Objects.nonNull(bookingDetailEdited)) {
                                            map.put(MessageTemplate.FINISH_STATUS,
                                                    MessageTemplate.FINISH_STATUS + "- bookingDetailId "
                                                            + bookingDetailEdited.getId().toString());
                                            if (notificationFireBaseService.notify(MessageTemplate.FINISH_TITLE,
                                                    MessageTemplate.FINISH_ALL_MESSAGE, map,
                                                    customer.getUser().getId(), Role.CUSTOMER)) {
                                                Notification notification = new Notification();
                                                notification.setRole(Role.CUSTOMER);
                                                notification.setTitle(MessageTemplate.FINISH_TITLE);
                                                notification.setMessage(MessageTemplate.FINISH_ALL_MESSAGE);
                                                notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                                notification.setType(Constant.TREATMENT_FINISH_TYPE);
                                                notification.setUser(customer.getUser());
                                                notificationService.insertNewNotification(notification);
                                                return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                                            } else {
                                                Notification notification = new Notification();
                                                notification.setRole(Role.CUSTOMER);
                                                notification.setTitle(MessageTemplate.FINISH_TITLE);
                                                notification.setMessage(MessageTemplate.FINISH_ALL_MESSAGE);
                                                notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                                notification.setType(Constant.TREATMENT_FINISH_TYPE);
                                                notification.setUser(customer.getUser());
                                                notificationService.insertNewNotification(notification);
                                                return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                                            }
                                        } else {
                                            LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
                                        }
                                    } else {
                                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                                        map.put(MessageTemplate.FINISH_STATUS,
                                                MessageTemplate.FINISH_STATUS + "- bookingDetailStepId "
                                                        + bookingDetailStepEdited.getId().toString());
                                        if (notificationFireBaseService.notify(MessageTemplate.FINISH_TITLE,
                                                String.format(MessageTemplate.FINISH_MESSAGE,
                                                        LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                                                map, customer.getUser().getId(), Role.CUSTOMER)) {
                                            Notification notification = new Notification();
                                            notification.setRole(Role.CUSTOMER);
                                            notification.setTitle(MessageTemplate.FINISH_TITLE);
                                            notification.setMessage(String.format(MessageTemplate.FINISH_MESSAGE,
                                                    LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)));
                                            notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                            notification.setType(Constant.STEP_FINISH_TYPE);
                                            notification.setUser(customer.getUser());
                                            notificationService.insertNewNotification(notification);
                                            return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                                        } else {
                                            Notification notification = new Notification();
                                            notification.setRole(Role.CUSTOMER);
                                            notification.setTitle(MessageTemplate.FINISH_TITLE);
                                            notification.setMessage(String.format(MessageTemplate.FINISH_MESSAGE,
                                                    LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)));
                                            notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                            notification.setType(Constant.STEP_FINISH_TYPE);
                                            notification.setUser(customer.getUser());
                                            notificationService.insertNewNotification(notification);
                                            return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                                        }
                                    }
                                } else {
                                    LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL_STEP));
                                }
                            } else {
                                LOGGER.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.RATING));
                            }
                        } else {
                            LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
                        }
                    } else {
                        LOGGER.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                        return ResponseHelper.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                    }
                } else {
                    LOGGER.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                    return ResponseHelper.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                }
            } else {
                LOGGER.error(LoggingTemplate.WRONG_STAFF_ID);
                return ResponseHelper.error(LoggingTemplate.WRONG_STAFF_ID);
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
        }
        return ResponseHelper.error(LoggingTemplate.CONFIRM_FINISH_FAILED);
    }

    @PutMapping("/bookingDetail/confirmFinishOneStep")
    public Response confirmFinishOneStep(@RequestBody ConfirmOneStepRequest request) throws FirebaseMessagingException {
        if (request.getBookingDetailId() != null) {
            BookingDetail bookingDetail =
                    bookingDetailService.findByBookingDetailId(request.getBookingDetailId());
            if (Objects.nonNull(bookingDetail)) {
                Staff staff = staffService.findByStaffId(request.getStaffId());
                if (staff.equals(bookingDetail.getBookingDetailSteps().get(0).getStaff())) {
                    Date currentDate = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
                    if (currentDate.compareTo(bookingDetail.getBookingDetailSteps().get(0).getDateBooking()) == 0 ||
                            currentDate.compareTo(bookingDetail.getBookingDetailSteps().get(0).getDateBooking()) == 1) {
                        Time currentTime = Time.valueOf(LocalTime.now(ZoneId.of(Constant.ZONE_ID)));
                        Time confirmTime =
                                Time.valueOf(bookingDetail.getBookingDetailSteps()
                                        .get(0).getStartTime()
                                        .toLocalTime().plusMinutes(15));
                        if (currentTime.compareTo(confirmTime) > 0) {
                            bookingDetail.setStatusBooking(StatusBooking.FINISH);
                            List<BookingDetailStep> bookingDetailSteps = bookingDetail.getBookingDetailSteps();
                            Rating rating = new Rating();
                            rating.setStatusRating(StatusRating.WAITING);
                            rating.setCreateTime(Date.valueOf(LocalDateTime.now().toLocalDate()));
                            rating.setExpireTime(Date.valueOf(LocalDateTime.now().plusDays(3).toLocalDate()));
                            rating.setCustomer(bookingDetail.getBooking().getCustomer());
                            rating.setBookingDetailStep(bookingDetailSteps.get(0));
                            Rating ratingNew = ratingService.insertNewRating(rating);
                            if (Objects.nonNull(ratingNew)) {
                                for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                                    if (bookingDetailStep.equals(bookingDetailSteps.get(0))) {
                                        bookingDetailStep.setRating(ratingNew);
                                    }
                                    bookingDetailStep.setStatusBooking(StatusBooking.FINISH);
                                }
                                bookingDetail.setBookingDetailSteps(bookingDetailSteps);
                                BookingDetail bookingDetailResult =
                                        bookingDetailService.editBookingDetail(bookingDetail);
                                if (Objects.nonNull(bookingDetailResult)) {
                                    Booking booking = bookingDetailResult.getBooking();
                                    if (booking.getBookingDetails().size() == 1) {
                                        booking.setStatusBooking(StatusBooking.FINISH);
                                        bookingService.editBooking(booking);
                                    }
                                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                                    Customer customer = bookingDetailResult.getBooking().getCustomer();
                                    Map<String, String> map = new HashMap<>();
                                    map.put(MessageTemplate.FINISH_STATUS,
                                            MessageTemplate.FINISH_STATUS + "- bookingDetailId "
                                                    + bookingDetailResult.getId());
                                    if (notificationFireBaseService.notify(MessageTemplate.FINISH_TITLE,
                                            String.format(MessageTemplate.FINISH_MESSAGE,
                                                    LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                                            map, customer.getUser().getId(), Role.CUSTOMER)) {
                                        Notification notification = new Notification();
                                        notification.setRole(Role.CUSTOMER);
                                        notification.setTitle(MessageTemplate.FINISH_TITLE);
                                        notification.setMessage(String.format(MessageTemplate.FINISH_ALL_MESSAGE,
                                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)));
                                        notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                        notification.setType(Constant.TREATMENT_FINISH_TYPE);
                                        notification.setUser(customer.getUser());
                                        notificationService.insertNewNotification(notification);
                                        return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,
                                                Constant.BOOKING_DETAIL_TREATMENT));
                                    } else {
                                        Notification notification = new Notification();
                                        notification.setRole(Role.CUSTOMER);
                                        notification.setTitle(MessageTemplate.FINISH_TITLE);
                                        notification.setMessage(String.format(MessageTemplate.FINISH_ALL_MESSAGE,
                                                LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)));
                                        notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                        notification.setType(Constant.TREATMENT_FINISH_TYPE);
                                        notification.setUser(customer.getUser());
                                        notificationService.insertNewNotification(notification);
                                        return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,
                                                Constant.BOOKING_DETAIL_TREATMENT));
                                    }
                                }
                            } else {
                                LOGGER.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.RATING));
                            }
                        } else {
                            LOGGER.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                            return ResponseHelper.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                        }
                    } else {
                        LOGGER.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                        return ResponseHelper.error(LoggingTemplate.WRONG_TIME_CONFIRM);
                    }
                } else {
                    LOGGER.error(LoggingTemplate.WRONG_STAFF_ID);
                    return ResponseHelper.error(LoggingTemplate.WRONG_STAFF_ID);
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
            }
        } else {
            LOGGER.error(LoggingTemplate.ID_NOT_EXISTED);
        }
        return ResponseHelper.error(LoggingTemplate.CONFIRM_FINISH_FAILED);
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

    @GetMapping("/getAllNotification/{staffId}")
    public Response getAllNotification(@PathVariable Integer staffId) {
        List<Notification> notifications =
                notificationService.findByIdAndRole(staffId, Role.STAFF);
        if (Objects.nonNull(notifications)) {
            return ResponseHelper.ok(notifications);
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.NOTIFICATION));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.NOTIFICATION));
    }

    @PutMapping(value = "/consultationContent/uploadImageBefore",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response uploadImageBefore(ConsultationContentImageRequest request) {
        if (request.getConsultationContentId() == null || request.getFile() == null) {
            LOGGER.error(LoggingTemplate.DATA_MISSING);
            return ResponseHelper.error(LoggingTemplate.DATA_MISSING);
        } else {
            ConsultationContent consultationContent =
                    consultationContentService
                            .findByConsultationContentId(request.getConsultationContentId());
            if (Objects.nonNull(consultationContent)) {
                String imageLink = UploadImage.uploadImage(request.getFile());
                if (imageLink != "") {
                    consultationContent.setImageBefore(imageLink);
                    ConsultationContent consultationContentResult =
                            consultationContentService.editByConsultationContent(consultationContent);
                    if (Objects.nonNull(consultationContentResult)) {
                        return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.CONSULTATION_CONTENT));
                    }
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTATION_CONTENT));
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
    }

    @PutMapping(value = "/consultationContent/uploadImageAfter",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public Response uploadImageAfter(ConsultationContentImageRequest request) {
        if (request.getConsultationContentId() == null || request.getFile() == null) {
            LOGGER.error(LoggingTemplate.DATA_MISSING);
            return ResponseHelper.error(LoggingTemplate.DATA_MISSING);
        } else {
            ConsultationContent consultationContent =
                    consultationContentService
                            .findByConsultationContentId(request.getConsultationContentId());
            if (Objects.nonNull(consultationContent)) {
                String imageLink = UploadImage.uploadImage(request.getFile());
                if (imageLink != "") {
                    consultationContent.setImageAfter(imageLink);
                    ConsultationContent consultationContentResult =
                            consultationContentService.editByConsultationContent(consultationContent);
                    if (Objects.nonNull(consultationContentResult)) {
                        return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.CONSULTATION_CONTENT));
                    }
                } else {
                    LOGGER.info(LoggingTemplate.SAVE_IMAGE_FAILED);
                    return ResponseHelper.error(LoggingTemplate.SAVE_IMAGE_FAILED);
                }
            } else {
                LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTATION_CONTENT));
            }
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
    }
}
