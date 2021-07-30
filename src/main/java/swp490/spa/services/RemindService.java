package swp490.spa.services;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import swp490.spa.entities.*;
import swp490.spa.utils.support.SupportFunctions;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.MessageTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class RemindService {
    Logger LOGGER = LogManager.getLogger(RemindService.class);
    @Autowired
    private BookingDetailStepService bookingDetailStepService;
    @Autowired
    private NotificationFireBaseService notificationFireBaseService;
    @Autowired
    private NotificationService notificationService;
    private SupportFunctions supportFunctions;

    public RemindService(BookingDetailStepService bookingDetailStepService,
                         NotificationFireBaseService notificationFireBaseService,
                         NotificationService notificationService) {
        this.bookingDetailStepService = bookingDetailStepService;
        this.notificationFireBaseService = notificationFireBaseService;
        this.notificationService = notificationService;
        this.supportFunctions = new SupportFunctions();
    }

    @Scheduled(cron = "0 0 7 * * *", zone = Constant.ZONE_ID)
    public void triggerRemind() throws FirebaseMessagingException {
        Date dateBooking = Date.valueOf(LocalDate.now(ZoneId.of(Constant.ZONE_ID)));
        List<BookingDetailStep> bookingDetailSteps =
                bookingDetailStepService.findAllByDateAndIsConsultation(IsConsultation.FALSE,
                        StatusBooking.BOOKING, dateBooking);
        if(Objects.nonNull(bookingDetailSteps)){
            List<BookingDetail> bookingDetails = new ArrayList<>();
            for (BookingDetailStep bookingDetailStep : bookingDetailSteps) {
                BookingDetail bookingDetail = bookingDetailStep.getBookingDetail();
                if(bookingDetails.size() == 0){
                    bookingDetails.add(bookingDetail);
                } else {
                    if(!supportFunctions.checkBookingDetailExistedInList(bookingDetail, bookingDetails)){
                        bookingDetails.add(bookingDetail);
                    }
                }
            }
            List<User> customers = new ArrayList<>();
            for (BookingDetail bookingDetail : bookingDetails) {
                User customer = bookingDetail.getBooking().getCustomer().getUser();
                if(customers.size() == 0){
                    customers.add(customer);
                } else {
                    if(!supportFunctions.checkUserExistedInList(customer, customers)){
                        customers.add(customer);
                    }
                }
            }
            Map<String, String> map = new HashMap<>();
            map.put(MessageTemplate.REMIND_STATUS,
                    MessageTemplate.REMIND_STATUS + "- date: " + dateBooking);
            for (User customer : customers) {
                if(notificationFireBaseService.notify(MessageTemplate.REMIND_TITLE,
                        MessageTemplate.REMIND_MESSAGE,map,customer.getId(),Role.CUSTOMER)){
                    Notification notification = new Notification();
                    notification.setRole(Role.CUSTOMER);
                    notification.setTitle(MessageTemplate.REMIND_TITLE);
                    notification.setMessage(MessageTemplate.REMIND_MESSAGE);
                    notification.setData(map.get(MessageTemplate.REMIND_STATUS));
                    notification.setType(Constant.REMIND_TYPE);
                    notification.setUser(customer);
                    notificationService.insertNewNotification(notification);
                } else {
                    Notification notification = new Notification();
                    notification.setRole(Role.CUSTOMER);
                    notification.setTitle(MessageTemplate.REMIND_TITLE);
                    notification.setMessage(MessageTemplate.REMIND_MESSAGE);
                    notification.setData(map.get(MessageTemplate.REMIND_STATUS));
                    notification.setType(Constant.REMIND_TYPE);
                    notification.setUser(customer);
                    notificationService.insertNewNotification(notification);
                }
            }
        }
    }
}
