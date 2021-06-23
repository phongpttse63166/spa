package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.BookingDetailEditRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.utils.support.Constant;
import swp490.spa.utils.support.Notification;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalTime;
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
    private BookingDetailService bookingDetailService;
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    @Autowired
    private SpaTreatmentService spaTreatmentService;
    @Autowired
    private DateOffService dateOffService;
    private Conversion conversion;

    public ConsultantController(ConsultantService consultantService, UserService userService,
                                DateOffService dateOffService, BookingService bookingService,
                                BookingDetailService bookingDetailService,
                                BookingDetailStepService bookingDetailStepService,
                                SpaTreatmentService spaTreatmentService) {
        this.consultantService = consultantService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.bookingDetailService = bookingDetailService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.spaTreatmentService = spaTreatmentService;
        this.conversion = new Conversion();
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

    @PostMapping("/dateoff/create")
    public Response insertNewDateOff(@RequestBody List<DateOff> dateOffList) {
        boolean isError = false;
        for (DateOff dateOff : dateOffList) {
            if (Objects.isNull(dateOffService.insertNewDateOff(dateOff))) {
                LOGGER.info(dateOff.getDateOff() + " create failed!");
                isError = true;
            }
        }
        if (isError) {
            return ResponseHelper.error(Notification.DATEOFF_CREATE_FAILED);
        }
        return ResponseHelper.ok(Notification.DATEOFF_CREATE_SUCCESS);
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
        return ResponseHelper.error(Notification.BOOKING_NOT_EXISTED);
    }

    @GetMapping("/bookingdetail/findbybookingid")
    public Response findByBooking(@RequestParam Integer bookingId,
                                  Pageable pageable) {
        Page<BookingDetail> bookingDetails =
                bookingDetailService.findByBooking(bookingId, pageable);
        if (Objects.nonNull(bookingDetails)) {
            return ResponseHelper.ok(conversion.convertToPageBookingDetailResponse(bookingDetails));
        }
        return ResponseHelper.error(Notification.BOOKING_DETAIL_NOT_EXISTED);
    }

    @PostMapping("/bookingdetail/editandinsertmorestep")
    public Response editBookingDetail(@RequestBody BookingDetailEditRequest bookingDetailRequest) {
        Double totalPriceBooking = bookingDetailRequest.getBookingDetail().
                getSpaTreatment().getTotalPrice();
        Integer totalTimeBooking = bookingDetailRequest.getBookingDetail().
                getSpaTreatment().getTotalTime();
        Booking bookingResult =
                bookingService.findByBookingId(bookingDetailRequest
                        .getBookingDetail()
                        .getBooking()
                        .getId());
        if (Objects.isNull(bookingResult)) {
            LOGGER.info(Notification.BOOKING_NOT_EXISTED);
            return ResponseHelper.error(Notification.BOOKING_DETAIL_EDIT_FAILED);
        }
        Consultant consultant = consultantService.findByConsultantId(bookingDetailRequest.getConsultantId());
        if (Objects.isNull(consultant)) {
            LOGGER.info(Notification.CONSULTANT_NOT_EXISTED);
            return ResponseHelper.error(Notification.BOOKING_DETAIL_EDIT_FAILED);
        }
        List<BookingDetail> bookingDetailList = bookingDetailService
                .findByBooking(bookingResult.getId(),
                        PageRequest.of(Constant.PAGE_DEFAULT,
                                Constant.SIZE_DEFAULT,
                                Sort.unsorted())).getContent();
        if (Objects.isNull(bookingDetailList)) {
            LOGGER.info(Notification.BOOKING_DETAIL_NOT_EXISTED);
            return ResponseHelper.error(Notification.BOOKING_DETAIL_EDIT_FAILED);
        }
        for (BookingDetail bookingDetail : bookingDetailList) {
            if(Objects.nonNull(bookingDetail.getSpaTreatment())){
                totalPriceBooking += bookingDetail.getSpaTreatment().getTotalPrice();
                totalTimeBooking += bookingDetail.getSpaTreatment().getTotalTime();
            }
        }
        bookingResult.setTotalPrice(totalPriceBooking);
        bookingResult.setTotalTime(totalTimeBooking);
        if(Objects.isNull(bookingService.editBooking(bookingResult))){
            LOGGER.info(bookingResult + Notification.BOOKING_EDIT_FAILED);
            return ResponseHelper.error(Notification.BOOKING_DETAIL_EDIT_FAILED);
        }
        if(Objects.isNull(bookingDetailService
                .editBookingDetail(bookingDetailRequest.getBookingDetail()))){
            LOGGER.info(bookingDetailRequest.getBookingDetail() +
                    Notification.BOOKING_DETAIL_EDIT_FAILED);
            return ResponseHelper.error(Notification.BOOKING_DETAIL_EDIT_FAILED);
        } else {
            List<TreatmentService> treatmentServices =
                    new ArrayList<>(bookingDetailRequest
                            .getBookingDetail()
                            .getSpaTreatment()
                            .getTreatmentServices());
            Collections.sort(treatmentServices);
            for (int i = 0; i < treatmentServices.size(); i++) {
                BookingDetailStep bookingDetailStep = new BookingDetailStep();
                bookingDetailStep.setConsultant(consultant);
                bookingDetailStep.setBookingDetail(bookingDetailRequest.getBookingDetail());
                bookingDetailStep.setTreatmentService(treatmentServices.get(i));
                if (i == 0) {
                    bookingDetailStep
                            .setDateBooking(Date.valueOf(bookingDetailRequest.getDateBooking()));
                    bookingDetailStep
                            .setStartTime(Time.valueOf(bookingDetailRequest.getTimeBooking()));
                    Time endTime = Time.valueOf(LocalTime.parse(bookingDetailRequest.getTimeBooking())
                            .plusMinutes(treatmentServices.get(i)
                                    .getSpaService()
                                    .getDurationMin()));
                    bookingDetailStep.setEndTime(endTime);
                    bookingDetailStep.setStatusBooking(StatusBooking.BOOKING);
                } else {
                    bookingDetailStep.setStatusBooking(StatusBooking.NOT_BOOKING);
                }
                if(Objects.isNull(bookingDetailStepService
                        .insertBookingDetailStep(bookingDetailStep))){
                    LOGGER.info(bookingDetailStep + Notification.BOOKING_DETAIL_STEP_CREATE_FAILED);
                }
            }
        }
        return ResponseHelper.ok(Notification.BOOKING_DETAIL_EDIT_SUCCESS);
    }


}
