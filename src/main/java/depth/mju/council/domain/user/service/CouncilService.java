package depth.mju.council.domain.user.service;

import depth.mju.council.domain.user.dto.req.UpdateCouncilReq;
import depth.mju.council.domain.user.dto.res.CouncilImageRes;
import depth.mju.council.domain.user.dto.res.CouncilRes;
import depth.mju.council.domain.user.entity.CouncilImage;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.CouncilImageRepository;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.error.DefaultException;
import depth.mju.council.global.payload.ErrorCode;
import depth.mju.council.infrastructure.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouncilService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final CouncilImageRepository councilImageRepository;

    @Transactional(readOnly = true)
    public CouncilRes getCouncil() {
        UserEntity user = userRepository.findByIsDeleted(false);
        return CouncilRes.builder()
                .generation(user.getGeneration())
                .name(user.getName())
                .email(user.getEmail())
                .snsUrl(user.getSnsUrl())
                .logoUrl(user.getLogoUrl())
                .build();
    }

    @Transactional
    public void updateCouncil(UpdateCouncilReq request, MultipartFile image,
                                UserPrincipal userPrincipal) {
        UserEntity user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지 삭제
        String oldImageUrl = user.getLogoUrl();
        if (oldImageUrl != null) {
            String oldImageName = s3Service.extractImageNameFromUrl(oldImageUrl);
            s3Service.deleteImage(oldImageName);
        }

        // 새 이미지 업로드
        String newImageUrl = s3Service.uploadImage(image);
        user.updateCouncil(request.getGeneration(), request.getName(), request.getEmail(), request.getSnsUrl(),
                newImageUrl);

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<CouncilImageRes> getAllCouncilImages() {
        List<CouncilImage> councilImages = councilImageRepository.findAll();

        return councilImages.stream()
                .map(councilImage -> CouncilImageRes.builder()
                        .councilImageId(councilImage.getId())
                        .description(councilImage.getDescription())
                        .imgUrl(councilImage.getImgUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createCouncilImage(String description, MultipartFile image, UserPrincipal userPrincipal) {
        UserEntity user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        String imgUrl = s3Service.uploadImage(image);
        CouncilImage councilImage = CouncilImage.builder()
                .description(description)
                .imgUrl(imgUrl)
                .userEntity(user)
                .build();
        councilImageRepository.save(councilImage);
    }

    @Transactional
    public void updateCouncilImage(Long councilImageId, String description, MultipartFile image,
                                   UserPrincipal userPrincipal) {
        CouncilImage councilImage = councilImageRepository.findById(councilImageId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "소개이미지를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지 삭제
        String oldImageUrl = councilImage.getImgUrl();
        if (oldImageUrl != null) {
            String oldImageName = s3Service.extractImageNameFromUrl(oldImageUrl);
            s3Service.deleteImage(oldImageName);
        }
        // 새 이미지 업로드
        String newImageUrl = s3Service.uploadImage(image);
        councilImage.updateDescriptionAndImgUrl(description, newImageUrl);

        councilImageRepository.save(councilImage);
    }

    @Transactional
    public void deleteCouncilImage(Long councilImageId, UserPrincipal userPrincipal) {
        CouncilImage councilImage = councilImageRepository.findById(councilImageId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "소개이미지를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 이미지 삭제
        String imageUrl = councilImage.getImgUrl();
        if (imageUrl != null) {
            String imageName = s3Service.extractImageNameFromUrl(imageUrl);
            s3Service.deleteImage(imageName);
        }

        // 소개이미지 삭제
        councilImageRepository.delete(councilImage);
    }
}