package swp490.spa.rest;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.DateOffRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.utils.support.image.UploadImage;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.templates.MessageTemplate;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private ConsultationContentService consultationContentService;
    @Autowired
    private RatingService ratingService;
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
                           BookingDetailService bookingDetailService,
                           NotificationFireBaseService notificationFireBaseService,
                           NotificationService notificationService) {
        this.staffService = staffService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.consultationContentService = consultationContentService;
        this.bookingDetailService = bookingDetailService;
        this.ratingService = ratingService;
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

    @PutMapping("/editprofile")
    public Response editProfileStaff(@RequestBody User user) {
        Staff staffResult = staffService.findByStaffId(user.getId());
        if (Objects.nonNull(staffResult)) {
            User userResult = staffResult.getUser();
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
                                     @RequestBody DateOffRequest dateOffRequest) {
        Date dateRegister = Date.valueOf(dateOffRequest.getDateOff());
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findByDateBookingAndConsultant(dateRegister,
                        staffId);
        if (bookingDetailSteps.size() == 0) {
            DateOff dateOff = new DateOff();
            Staff staff = staffService.findByStaffId(staffId);
            dateOff.setStatusDateOff(StatusDateOff.WAITING);
            dateOff.setReasonCancel(dateOffRequest.getReasonDateOff());
            dateOff.setEmployee(staff.getUser());
            dateOff.setSpa(staff.getSpa());
            DateOff dateOffResult = dateOffService.insertNewDateOff(dateOff);
            if (Objects.nonNull(dateOffResult)) {
                return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.DATE_OFF));
            }
        } else {
            return ResponseHelper.error(String.format(LoggingTemplate.BOOKING_SERVICE_EXISTED));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.DATE_OFF));
    }

    @GetMapping("/workingofstaff/findbydatechosen/{staffId}")
    public Response findWorkingOfStaffByDateChosen(@PathVariable Integer staffId,
                                                   @RequestParam String dateChosen) {
        Staff staff = staffService.findByStaffId(staffId);
        if (Objects.nonNull(staff)) {
            Page<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByStaffIdAndDateBooking(staffId, Date.valueOf(dateChosen),
                            PageRequest.of(Constant.PAGE_DEFAULT, Constant.SIZE_DEFAULT, Sort.unsorted()));
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

    @PutMapping("/bookingDetailStep/confirmFinishAStep/{bookingDetailStepId}")
    public Response confirmFinishAStep(@PathVariable Integer bookingDetailStepId,
                                       @RequestBody ConsultationContent consultationContent) throws FirebaseMessagingException {
        boolean checkFinishAll = false;
        BookingDetailStep bookingDetailStep =
                bookingDetailStepService.findById(bookingDetailStepId);
        if (Objects.nonNull(bookingDetailStep)) {
            ConsultationContent consultationContentGet = bookingDetailStep.getConsultationContent();
            consultationContentGet.setResult(consultationContent.getResult());
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
                                map.put(MessageTemplate.FINISH_STATUS, "bookingDetailId "
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
                                    notificationService.insertNewNotification(notification);
                                    return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                                } else {
                                    return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                                }
                            } else {
                                LOGGER.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
                            }
                        } else {
                            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");
                            map.put(MessageTemplate.FINISH_STATUS, "bookingDetailStepId "
                                    + bookingDetailStepEdited.getId().toString());
                            if (notificationFireBaseService.notify(MessageTemplate.FINISH_TITLE,
                                    String.format(MessageTemplate.FINISH_MESSAGE,
                                            LocalTime.now(ZoneId.of(Constant.ZONE_ID)).format(dtf)),
                                    map, customer.getUser().getId(), Role.CUSTOMER)) {
                                Notification notification = new Notification();
                                notification.setRole(Role.CUSTOMER);
                                notification.setTitle(MessageTemplate.FINISH_TITLE);
                                notification.setMessage(MessageTemplate.FINISH_MESSAGE);
                                notification.setData(map.get(MessageTemplate.FINISH_STATUS));
                                notification.setType(Constant.STEP_FINISH_TYPE);
                                notificationService.insertNewNotification(notification);
                                return ResponseHelper.ok(LoggingTemplate.CONFIRM_FINISH_SUCCESS);
                            } else {
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
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL_STEP));
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
                    if(Objects.nonNull(userResult)){
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
