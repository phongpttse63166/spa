package swp490.spa.services;

import com.google.firebase.messaging.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import swp490.spa.entities.User;
import swp490.spa.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class NotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);
    @Autowired
    private UserRepository userRepository;
    private static final int MAX_NOTIFICATION_RETRY = 3;
    private static final long WAIT_NOTIFICATION_RETRY = 10 * 60 * 1000;

    @Async
    public CompletableFuture<String> notify(String title, String message,
                                            Map<String, String> data,
                                            Integer userId) throws FirebaseMessagingException {
        User user = this.userRepository.findById(userId).get();
        Message notification = Message.builder()
                .putAllData(data)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(message)
                        .build())
                .setToken(user.getId().toString())
                .build();
        for (int i = 0; i < MAX_NOTIFICATION_RETRY; i++) {
            try {
                String response = FirebaseMessaging.getInstance().send(notification);
                LOGGER.info("Notification sent {}", response);
                return CompletableFuture.completedFuture(response);
            } catch (FirebaseMessagingException ex) {
                try {
                    Thread.sleep(WAIT_NOTIFICATION_RETRY);
                } catch (InterruptedException exception) {
                    LOGGER.error("Error when retrying sending notification");
                    ex.printStackTrace();
                }
            }
        }
        return CompletableFuture.completedFuture(FirebaseMessaging.getInstance().send(notification));
    }

    public BatchResponse notifyMulti(String title, String message,
                                     Map<String, String> data,
                                     Integer... userIds) throws FirebaseMessagingException {
        if (ArrayUtils.isEmpty(userIds)) {
            throw new IllegalArgumentException("Empty user ids");
        }
        List<Integer> userIdListResult = StreamSupport.stream(
                this.userRepository.findAllById(Arrays.asList(userIds)).spliterator(), false)
                .map(User::getId)
                .collect(Collectors.toList());
        List<String> tokens = new ArrayList<>();
        for (Integer userId : userIdListResult) {
            tokens.add(userId.toString());
        }
        MulticastMessage multicastNotification = MulticastMessage.builder()
                .putAllData(data)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(message)
                        .build())
                .addAllTokens(tokens)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(multicastNotification);
        LOGGER.info("Sent multicast notification {}", response);
        return response;
    }
}

