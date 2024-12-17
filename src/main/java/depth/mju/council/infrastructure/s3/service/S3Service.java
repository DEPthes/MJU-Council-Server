package depth.mju.council.infrastructure.s3.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 이미지 업로드
    public String uploadImage(MultipartFile image) {
        String originalFileName = image.getOriginalFilename();
        String saveFileName = "image/" + createSaveFileName(originalFileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());
        metadata.setContentType(image.getContentType());

        try (InputStream inputStream = image.getInputStream()) {
            // S3에 업로드 및 저장
            amazonS3.putObject(new PutObjectRequest(bucket, saveFileName, inputStream, metadata));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다.");
        }

        return getImageFullPath(saveFileName);
    }

    // 파일 업로드
    public String uploadFile(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String saveFileName = "file/" + createSaveFileName(originalFileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            // S3에 업로드 및 저장
            amazonS3.putObject(new PutObjectRequest(bucket, saveFileName, inputStream, metadata));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        return getFileFullPath(saveFileName);
    }

    // 파일 저장명 만들기
    // 사용자들이 올리는 파일 이름이 같을 수 있으므로, 랜덤 이름을 만들어 사용
    private String createSaveFileName(String originalFileName) {
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자명 구하기
    private String extractExt(String originalFileName) {
        int pos = originalFileName.lastIndexOf(".");
        return originalFileName.substring(pos + 1);
    }

    // fullPath 만들기(이미지)
    public String getImageFullPath(String fileName) {
        return "https://" + bucket + ".s3.amazonaws.com/" + fileName;
    }

    // fullPath 만들기(파일)
    public String getFileFullPath(String fileName) {
        return "https://" + bucket + ".s3.amazonaws.com/" + fileName;
    }


    // 주의: 해당 메소드를 호출할 시 파일의 주소가 아닌 '이름'만 전달할 것
    public void deleteFile(String fileName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, "file/" + fileName));
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");
        }
    }

    public void deleteImage(String imageName) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, "image/" + imageName));
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.");
        }
    }

    public String extractImageNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }

}
