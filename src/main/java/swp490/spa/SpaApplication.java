package swp490.spa;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import swp490.spa.utils.support.templates.Constant;

import java.io.FileInputStream;

@SpringBootApplication
@EnableScheduling
public class SpaApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(SpaApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        FileInputStream serviceAccount =
                new FileInputStream(Constant.FILE_JSON);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket(Constant.STORAGE_BUCKET)
                .setDatabaseUrl(Constant.DATABASE_URL)
                .build();

        FirebaseApp.initializeApp(options);
    }
}
