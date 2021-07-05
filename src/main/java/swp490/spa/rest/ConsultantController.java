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
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;

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
    @Autowired
    private ConsultationContentService consultationContentService;
    private Conversion conversion;

    public ConsultantController(ConsultantService consultantService, UserService userService,
                                DateOffService dateOffService, BookingService bookingService,
                                BookingDetailService bookingDetailService,
                                BookingDetailStepService bookingDetailStepService,
                                SpaTreatmentService spaTreatmentService,
                                ConsultationContentService consultationContentService) {
        this.consultantService = consultantService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.bookingDetailService = bookingDetailService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.spaTreatmentService = spaTreatmentService;
        this.consultationContentService = consultationContentService;
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
        List<DateOff> dateOffs = new ArrayList<>();
        DateOff dateOffResult = null;
        for (DateOff dateOff : dateOffList) {
            dateOffResult = dateOffService.insertNewDateOff(dateOff);
            if (Objects.isNull(dateOffResult)) {
                for (DateOff dateOffRemove : dateOffs) {
                    dateOffService.removeDateOff(dateOffRemove.getId());
                }
                return ResponseHelper.error(String.format(LoggingTemplate.INSERT_FAILED, Constant.DATE_OFF));
            } else {
                dateOffs.add(dateOff);
            }
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.DATE_OFF));
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
                                                 Pageable pageable){
        Page<SpaTreatment> spaTreatments =
                spaTreatmentService.findByPackageId(spaPackageId,
                        Constant.SEARCH_NO_CONTENT, pageable);
        if(Objects.nonNull(spaTreatments)){
            return ResponseHelper.ok(conversion.convertToPageSpaTreatmentResponse(spaTreatments));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.GET_FAILED, Constant.SPA_TREATMENT));
    }

    @GetMapping("/workingofconsultant/findbydatechosen/{consultantId}")
    public Response findWorkingOfConsultantByDateChosen(@PathVariable Integer consultantId,
                                                    @RequestParam String dateChosen){
        Consultant consultant = consultantService.findByConsultantId(consultantId);
        if(Objects.nonNull(consultant)){
            Page<BookingDetailStep> bookingDetailSteps =
                    bookingDetailStepService.findByConsultantIdAndDateBooking(consultantId, Date.valueOf(dateChosen),
                            PageRequest.of(Constant.PAGE_DEFAULT,Constant.SIZE_MAX, Sort.unsorted()));
            if(Objects.nonNull(bookingDetailSteps)){
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

    // Cho nay con check lai nha
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
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING));
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
        }
        Consultant consultant = consultantService.findByConsultantId(bookingDetailRequest.getConsultantId());
        if (Objects.isNull(consultant)) {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTANT));
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
        }
        List<BookingDetail> bookingDetailList = bookingDetailService
                .findByBooking(bookingResult.getId(),
                        PageRequest.of(Constant.PAGE_DEFAULT,
                                Constant.SIZE_DEFAULT,
                                Sort.unsorted())).getContent();
        if (Objects.isNull(bookingDetailList)) {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
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
            LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING));
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
        }
        if(Objects.isNull(bookingDetailService
                .editBookingDetail(bookingDetailRequest.getBookingDetail()))){
            LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
            return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
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
                    bookingDetailStep.setStatusBooking(StatusBooking.START);
                }
                BookingDetailStep bookingDetailStepNew = (bookingDetailStepService
                        .insertBookingDetailStep(bookingDetailStep));
                if(Objects.isNull(bookingDetailStepNew)){
                    LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.BOOKING_DETAIL_STEP));
                } else {
                    ConsultationContent consultationContent = new ConsultationContent();
                    consultationContent.setBookingDetailStep(bookingDetailStepNew);
                    ConsultationContent consultationContentResult =
                            consultationContentService.insertNewConsultationContent(consultationContent);
                    if(Objects.nonNull(consultationContentResult)){
                        LOGGER.info(String.format(LoggingTemplate.INSERT_SUCCESS, Constant.CONSULTATION_CONTENT));
                    } else {
                        LOGGER.info(String.format(LoggingTemplate.INSERT_FAILED, Constant.CONSULTATION_CONTENT));
                    }
                }
            }
        }
        return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.BOOKING_DETAIL));
    }

    @PutMapping("/consultationcontent/edit")
    public Response editConsultationContent(@RequestBody ConsultationContent consultationContent){
        ConsultationContent consultationContentEdit =
                consultationContentService.findByConsultationContentId(consultationContent.getId());
        if(Objects.nonNull(consultationContentEdit)){
            if(Objects.nonNull(consultationContent.getDescription())) {
                consultationContentEdit.setDescription(consultationContent.getDescription());
            }
            if(Objects.nonNull(consultationContent.getExpectation())) {
                consultationContentEdit.setExpectation(consultationContent.getExpectation());
            }
            if(Objects.nonNull(consultationContent.getResult())) {
                consultationContentEdit.setResult(consultationContent.getResult());
            }
            if(Objects.nonNull(consultationContent.getNote())) {
                consultationContentEdit.setNote(consultationContent.getNote());
            }
            ConsultationContent consultationContentResult =
                    consultationContentService.editByConsultationContent(consultationContentEdit);
            if(Objects.isNull(consultationContentResult)){
                LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
            }
            LOGGER.info(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.CONSULTATION_CONTENT));
            return ResponseHelper.ok(String.format(LoggingTemplate.EDIT_SUCCESS, Constant.BOOKING_DETAIL));
        } else {
            LOGGER.info(String.format(LoggingTemplate.GET_FAILED, Constant.CONSULTATION_CONTENT));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.CONSULTATION_CONTENT));
    }
}
