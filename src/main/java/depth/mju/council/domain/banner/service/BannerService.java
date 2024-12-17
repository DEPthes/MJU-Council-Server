package depth.mju.council.domain.banner.service;

import depth.mju.council.domain.banner.dto.res.GetBannerRes;
import depth.mju.council.domain.banner.entity.Banner;
import depth.mju.council.domain.banner.repository.BannerRepository;
import depth.mju.council.domain.regulation.entity.Regulation;
import depth.mju.council.domain.regulation.entity.RegulationFile;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import depth.mju.council.infrastructure.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {
    private final UserRepository userRepository;
    private final BannerRepository bannerRepository;
    private final S3Service s3Service;

    @Transactional
    public void createBanner(Long userId, MultipartFile img) {
        UserEntity user = validUserById(userId);
        String imgUrl = s3Service.uploadImage(img);
        Banner banner = Banner.builder()
                .imgUrl(imgUrl)
                .userEntity(user)
                .build();
        bannerRepository.save(banner);
    }

    public List<GetBannerRes> getBanner() {
        List<Banner> banners = bannerRepository.findAll();

        return banners.stream()
                .map(banner -> GetBannerRes.builder()
                        .bannerId(banner.getId())
                        .imgUrl(banner.getImgUrl())
                        .date(banner.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void modifyBanner(Long bannerId, MultipartFile img) {
        Banner banner = validaBannerById(bannerId);
        String deleteImgUrl = banner.getImgUrl();
        // 저장 파일명 구하기
        String saveFileName = s3Service.extractImageNameFromUrl(deleteImgUrl);
        // S3에서 삭제
        s3Service.deleteImage(saveFileName);
        String newImgUrl = s3Service.uploadImage(img);
        banner.updateImgUrl(newImgUrl);
    }

    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = validaBannerById(bannerId);
        String deleteImgUrl = banner.getImgUrl();
        String saveFileName = s3Service.extractImageNameFromUrl(deleteImgUrl);
        s3Service.deleteImage(saveFileName);
        bannerRepository.delete(banner);
    }

    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }

    private Banner validaBannerById(Long bannerId) {
        Optional<Banner> bannerOptional = bannerRepository.findById(bannerId);
        DefaultAssert.isOptionalPresent(bannerOptional);
        return bannerOptional.get();
    }
}