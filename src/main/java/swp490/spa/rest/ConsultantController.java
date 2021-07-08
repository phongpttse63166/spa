package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.requests.BookingDetailEditRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.*;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;

import java.sql.Date;
import java.sql.Time;
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
    @Autowired
    private StaffService staffService;
    private Conversion conversion;
    private SupportFunctions supportFunctions;

    public ConsultantController(ConsultantService consultantService, UserService userService,
                                DateOffService dateOffService, BookingService bookingService,
                                BookingDetailService bookingDetailService, StaffService staffService,
                                BookingDetailStepService bookingDetailStepService,
                                SpaTreatmentService spaTreatmentService,
                                ConsultationContentService consultationContentService) {
        this.consultantService = consultantService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.staffService = staffService;
        this.bookingDetailService = bookingDetailService;
        this.bookingDetailStepService = bookingDetailStepService;
        this.spaTreatmentService = spaTreatmentService;
        this.consultationContentService = consultationContentService;
        this.conversion = new Conversion();
        this.supportFunctions = new SupportFunctions();
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

    @GetMapping("/getlisttimebookingtreatment")
    public Response getListTimeBookingTreatment(@RequestParam Integer treatmentId,
                                                @RequestParam String dateBooking,
                                                @RequestParam Integer customerId) {
        int countEmployee = 0;
        List<DateOff> dateOffs = null;
        List<Staff> staffs = null;
        List<BookingDetailStep> bookingDetailSteps = null;
        SpaTreatment spaTreatment = spaTreatmentService.findByTreatmentId(treatmentId);
        if (Objects.nonNull(spaTreatment)) {
            dateOffs = dateOffService.findByDateOffAndSpaAndStatusApprove(Date.valueOf(dateBooking),
                    spaTreatment.getSpa().getId());
            staffs = staffService.findBySpaId(spaTreatment.getSpa().getId());
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
            Map<Integer, List<BookingDetailStep>> map =
                    supportFunctions.separateBookingDetailStepListAndPutIntoMap(bookingDetailSteps);
            int check = countEmployee - map.size();
            List<String> timeBookingList = null;
            int totalTime = 0;
            List<TreatmentService> treatmentServices =
                    new ArrayList<>(spaTreatment.getTreatmentServices());
            for (TreatmentService treatmentService : treatmentServices) {
                if (treatmentService.getOrdinal().equals(1)) {
                    totalTime = treatmentService.getSpaService().getDurationMin();
                }
            }
            timeBookingList =
                    supportFunctions.getBookTime(totalTime, map, check);
            if (timeBookingList.size() != 0) {
                timeBookingList =
                        supportFunctions.checkAndGetListTimeBooking(customerId, timeBookingList,
                                dateBooking);
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

    @PutMapping("/bookingdetailstep/addtreatment")
    public Response editBookingDetail(@RequestBody BookingDetailEditRequest bookingDetailRequest) {
        Booking bookingBeforeEdit;
        BookingDetail bookingDetailEdit = null;
        List<BookingDetailStep> bookingDetailStepEditList = new ArrayList<>();
        List<ConsultationContent> consultationContentList = new ArrayList<>();
        List<ConsultationContent> consultationContentResultList = new ArrayList<>();
        boolean checkFinish = true;
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
                if (bookingDetail.getBookingDetailSteps().size() == 1) {
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
                    }
                    bookingDetailStep.setBookingPrice(treatmentService.getSpaService().getPrice());
                    bookingDetailStep.setStatusBooking(StatusBooking.PENDING);
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
                            if(bookingDetailStep.getIsConsultation().equals(IsConsultation.FALSE)) {
                                ConsultationContent consultationContent = new ConsultationContent();
                                consultationContent.setBookingDetailStep(bookingDetailStep);
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
                    if(Objects.nonNull(bookingDetailStepService.editBookingDetailStep(bookingDetailStep))){
                        LOGGER.info(String.format(LoggingTemplate.EDIT_FAILED,
                                Constant.BOOKING_DETAIL_STEP));
                    }
                }
                return ResponseHelper.ok(String.format(LoggingTemplate.INSERT_SUCCESS,
                        Constant.BOOKING_DETAIL_TREATMENT));
            }
        } else {
            LOGGER.error(String.format(LoggingTemplate.GET_FAILED, Constant.BOOKING_DETAIL));
        }
        return ResponseHelper.error(String.format(LoggingTemplate.EDIT_FAILED, Constant.BOOKING_DETAIL));
    }
}
