package swp490.spa.utils.support.Image;

import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import swp490.spa.utils.support.Templates.Constant;

import java.io.IOException;
import java.util.UUID;

public class UploadImage {

    public static String uploadImage(MultipartFile file) {
        try {
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
        return String.format(Constant.STORAGE_URL, name);
    }

}
