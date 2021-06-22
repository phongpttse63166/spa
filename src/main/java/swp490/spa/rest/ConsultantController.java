package swp490.spa.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import swp490.spa.dto.helper.Conversion;
import swp490.spa.dto.helper.ResponseHelper;
import swp490.spa.dto.requests.AccountPasswordRequest;
import swp490.spa.dto.support.Response;
import swp490.spa.entities.*;
import swp490.spa.services.BookingService;
import swp490.spa.services.ConsultantService;
import swp490.spa.services.DateOffService;
import swp490.spa.services.UserService;
import swp490.spa.utils.support.Notification;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/consultant")
@CrossOrigin
public class ConsultantController {
    private Logger LOGGER = LogManager.getLogger(ConsultantController.class);
    @Autowired
    ConsultantService consultantService;
    @Autowired
    UserService userService;
    @Autowired
    BookingService bookingService;
    @Autowired
    private DateOffService dateOffService;
    private Conversion conversion;

    public ConsultantController(ConsultantService consultantService, UserService userService,
                                DateOffService dateOffService, BookingService bookingService) {
        this.consultantService = consultantService;
        this.userService = userService;
        this.dateOffService = dateOffService;
        this.bookingService = bookingService;
        this.conversion = new Conversion();
    }

    @PutMapping("/editpassword")
    public Response editPassword(@RequestBody AccountPasswordRequest account){
        Consultant consultant = consultantService.findByConsultantId(account.getId());
        User oldUser = consultant.getUser();
        User updateUser = consultant.getUser();
        updateUser.setPassword(account.getPassword());
        if(Objects.nonNull(userService.editUser(updateUser))){
            return ResponseHelper.ok(updateUser);
        } else {
            userService.editUser(oldUser);
            return ResponseHelper.error("");
        }
    }

//    @GetMapping("/booking/findbybookingstatus")
//    public Response findByBookingStatusAndSpa(@RequestParam StatusBooking statusBooking,
//                                              @RequestParam Integer spaId,
//                                              @RequestParam Integer customerId,
//                                              Pageable pageable){
//        Page<Booking> bookings =
//                bookingService.findByBookingStatusAndSpa(statusBooking, spaId,
//                        customerId, pageable);
//        return ResponseHelper.ok(conversion.convertToPageBookingResponse(bookings));
//    }

    @PostMapping("/dateoff/create")
    public Response insertNewDateOff(@RequestBody List<DateOff> dateOffList){
        boolean isError = false;
        for (DateOff dateOff : dateOffList) {
            if(Objects.isNull(dateOffService.insertNewDateOff(dateOff))){
                LOGGER.info(dateOff.getDateOff() + " create failed!");
                isError = true;
            }
        }
        if(isError){
            return ResponseHelper.error(Notification.DATEOFF_CREATE_FAILED);
        }
        return ResponseHelper.ok(Notification.DATEOFF_CREATE_SUCCESS);
    }
}
