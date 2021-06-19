package swp490.spa.utils.support;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class UploadImage {

    private static final String imageURL = "https://storage.googleapis.com/testuploadimgae.appspot.com/%s";

    public static String uploadImage(MultipartFile file) {
        try {

            FileInputStream serviceAccount =
                    new FileInputStream("testuploadimgae-firebase-adminsdk-tpl07-8b469b8252.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("testuploadimgae.appspot.com")
                    .build();

            FirebaseApp.initializeApp(options);

            String fileName = save(file);
            String imageUrl = getImageUrl(fileName);

            return imageUrl;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "ERROR";
    }


    private static String save(MultipartFile file) throws IOException{
        Bucket bucket = StorageClient.getInstance().bucket();

        String name = generateFileName(file.getOriginalFilename());

        bucket.create(name, file.getBytes(), file.getContentType());

        return name;
    }

    private static String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + getExtension(originalFileName);
    }

    private static String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }

    private static String getImageUrl(String name) {
        return String.format(imageURL, name);
    }

}
