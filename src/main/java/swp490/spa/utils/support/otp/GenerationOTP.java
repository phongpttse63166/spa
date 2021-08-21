package swp490.spa.utils.support.otp;

import java.util.Random;

public class GenerationOTP {
    public static String generateOTPCode(int min, int max) {
        Random random = new Random();
        int randomNumber = random.ints(min, max).findFirst().getAsInt();
        if (randomNumber < 10) {
            return "000" + randomNumber;
        } else if (randomNumber < 100) {
            return "00" + randomNumber;
        } else if (randomNumber < 1000) {
            return "0" + randomNumber;
        }
        return randomNumber + "";
    }
}
