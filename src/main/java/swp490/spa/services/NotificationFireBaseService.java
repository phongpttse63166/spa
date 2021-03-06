package swp490.spa.services;

import com.google.firebase.messaging.*;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import swp490.spa.entities.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class NotificationFireBaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationFireBaseService.class);
    @Autowired
    private StaffService staffService;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private ManagerService managerService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private AdminService adminService;
    private static final int MAX_NOTIFICATION_RETRY = 3;
    private static final long WAIT_NOTIFICATION_RETRY = 10 * 1000;

    public boolean notify(String title, String message,
                       Map<String, String> data,
                       Integer userId, Role role) throws FirebaseMessagingException {
        Message notification = null;
        switch (role) {
            case STAFF:
                Staff staff = staffService.findByStaffId(userId);
                if(staff.getTokenFCM() == null){
                    return false;
                } else {
                    notification = Message.builder()
                            .putAllData(data)
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(message)
                                    .build())
                            .setToken(staff.getTokenFCM())
                            .build();
                }
                break;
            case MANAGER:
                Manager manager = managerService.findManagerById(userId);
                if(manager.getTokenFCM() == null){
                    return false;
                } else {
                    notification = Message.builder()
                            .putAllData(data)
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(message)
                                    .build())
                            .setToken(manager.getTokenFCM())
                            .build();
                }

                break;
            case CUSTOMER:
                Customer customer = customerService.findByUserId(userId);
                if(customer.getTokenFCM() == null){
                    return false;
                } else {
                    notification = Message.builder()
                            .putAllData(data)
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(message)
                                    .build())
                            .setToken(customer.getTokenFCM())
                            .build();
                }

                break;
            case CONSULTANT:
                Consultant consultant = consultantService.findByConsultantId(userId);
                if(consultant.getTokenFCM() == null){
                    return false;
                } else {
                    notification = Message.builder()
                            .putAllData(data)
                            .setNotification(Notification.builder()
                                    .setTitle(title)
                                    .setBody(message)
                                    .build())
                            .setToken(consultant.getTokenFCM())
                            .build();

                }
                break;
            case ADMIN:
                break;
        }
        for (int i = 0; i < MAX_NOTIFICATION_RETRY; i++) {
            try {
                String response = FirebaseMessaging.getInstance().send(notification);
                LOGGER.info("Notification sent {}", response);
                return true;
            } catch (FirebaseMessagingException ex) {
                try {
                    Thread.sleep(WAIT_NOTIFICATION_RETRY);
                } catch (InterruptedException exception) {
                    LOGGER.error("Error when retrying sending notification");
                    ex.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
}

