package depth.mju.council.domain.orgarnization.service;

import depth.mju.council.domain.orgarnization.dto.res.OrganizationRes;
import depth.mju.council.domain.orgarnization.entity.Organization;
import depth.mju.council.domain.orgarnization.repository.OrganizationRepository;
import depth.mju.council.domain.user.entity.UserEntity;
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
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<OrganizationRes> getAllOrganizations() {
        List<Organization> organizations = organizationRepository.findAll();

        return organizations.stream()
                .map(organization -> OrganizationRes.builder()
                        .organizationId(organization.getId())
                        .title(organization.getTitle())
                        .imgUrl(organization.getImgUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createOrganization(String title, MultipartFile image, UserPrincipal userPrincipal) {

        UserEntity user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        String imgUrl = s3Service.uploadImage(image);
        Organization organization = Organization.builder()
                .title(title)
                .imgUrl(imgUrl)
                .userEntity(user)
                .build();
        organizationRepository.save(organization);

    }

    @Transactional
    public void updateOrganization(Long organizationId, String title, MultipartFile image, UserPrincipal userPrincipal) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "조직도를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지 삭제
        String oldImageUrl = organization.getImgUrl();
        if (oldImageUrl != null) {
            String oldImageName = s3Service.extractImageNameFromUrl(oldImageUrl);
            s3Service.deleteImage(oldImageName);
        }
        // 새 이미지 업로드
        String newImageUrl = s3Service.uploadImage(image);
        organization.updateTitleAndImgUrl(title, newImageUrl);

        organizationRepository.save(organization);
    }

    @Transactional
    public void deleteOrganization(Long organizationId, UserPrincipal userPrincipal) {
        Organization organization = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "조직도를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 이미지 삭제
        String imageUrl = organization.getImgUrl();
        if (imageUrl != null) {
            String imageName = s3Service.extractImageNameFromUrl(imageUrl);
            s3Service.deleteImage(imageName);
        }

        // 조직도 삭제
        organizationRepository.delete(organization);
    }
}