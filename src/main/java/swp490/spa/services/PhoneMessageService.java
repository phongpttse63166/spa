package swp490.spa.services;

import com.google.gson.Gson;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.WebClient;
import swp490.spa.utils.support.phoneMessage.ResponseGetAccessToken;
import swp490.spa.utils.support.templates.Constant;
import swp490.spa.utils.support.templates.LoggingTemplate;

@AllArgsConstructor
@NoArgsConstructor
public class PhoneMessageService {
    Logger LOGGER = LogManager.getLogger(PhoneMessageService.class);

    public void sendMessageTwilio(String message, String phoneNumber) {
        Twilio.init(Constant.ACCOUNT_SID, Constant.AUTH_TOKEN);
        Message.creator(new PhoneNumber(convertPhone(phoneNumber)),
                new PhoneNumber(Constant.PHONE_SERVICE), message).create();
    }

    private String convertPhone(String phoneNumber) {
        String subPhone = phoneNumber.substring(1, phoneNumber.length()).trim();
        String messPhone = "+84" + subPhone;
        return messPhone;
    }

    public String sendMessageZalo(String receiver, String message, String accessToken) {
        try {
            WebClient webClient = WebClient.builder().baseUrl(Constant.ZALO_URL).build();

            WebClient.ResponseSpec responseSpec = webClient.post().uri(uriBuilder -> uriBuilder.path("/message")
                    .queryParam("access_token", accessToken)
                    .queryParam("message", message)
                    .queryParam("link", "")
                    .queryParam("to", receiver).build())
                    .retrieve();

            String responseBody = responseSpec.bodyToMono(String.class).block();
            return responseBody;
        } catch (Exception ex) {
            LOGGER.error(LoggingTemplate.SEND_PHONE_MESSAGE_FAILED);
        }
        return LoggingTemplate.SEND_PHONE_MESSAGE_FAILED;
    }


    private String getAccessToken(String appId, String appSecret, String code) {
        try {
            WebClient webClient = WebClient.builder().baseUrl(Constant.ZALO_OAUTH_URL).build();

            WebClient.ResponseSpec responseSpec = webClient.get().uri(uriBuilder -> uriBuilder.path("/access_token")
                    .queryParam("app_id", appId)
                    .queryParam("app_secret", appSecret)
                    .queryParam("code", code)
                    .build())
                    .retrieve();

            String responseBody = responseSpec.bodyToMono(String.class).block();
            return responseBody;
        } catch (Exception ex) {
            LOGGER.error(LoggingTemplate.GET_ACCESS_TOKEN_FAILED);
        }
        return LoggingTemplate.GET_ACCESS_TOKEN_FAILED;
    }

    public ResponseGetAccessToken setResponseGetAccessToken(){
        String response = getAccessToken(Constant.APP_ID, Constant.APP_SECRET, Constant.CODE);
        Gson g = new Gson();
        ResponseGetAccessToken responseGetAccessToken =
                g.fromJson(response, ResponseGetAccessToken.class);
        return responseGetAccessToken;
    }
}
