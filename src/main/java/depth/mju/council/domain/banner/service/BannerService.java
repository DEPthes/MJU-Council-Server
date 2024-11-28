package depth.mju.council.domain.banner.service;

import depth.mju.council.domain.banner.dto.res.GetBannerRes;
import depth.mju.council.domain.banner.entity.Banner;
import depth.mju.council.domain.banner.repository.BannerRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BannerService {
    private final UserRepository userRepository;
    private final BannerRepository bannerRepository;
    @Transactional
    public void createBanner(Long userId, MultipartFile img) {
        UserEntity user = userRepository.findById(userId).get();
        Banner banner = Banner.builder()
                .imgUrl("이미지 URL 저장 로직 필요")
                .userEntity(user)
                .build();
        bannerRepository.save(banner);
    }
    public List<GetBannerRes> getBanner(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).get();
        List<Banner> banners = bannerRepository.findByUserEntity(userEntity);
        List<GetBannerRes> bannersRes = banners.stream()
                .map(banner -> GetBannerRes.builder()
                        .bannerId(banner.getId())
                        .imgUrl(banner.getImgUrl())
                        .date(banner.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return bannersRes;
    }
    @Transactional
    public void modifyBanner(Long bannerId, MultipartFile img) {
        Banner banner = bannerRepository.findById(bannerId).get();
        // 이미지 URL 업데이트 로직 필요
        banner.updateImgUrl("새로운 이미지 URL");
    }
    @Transactional
    public void deleteBanner(Long bannerId) {
        Banner banner = bannerRepository.findById(bannerId).get();
        bannerRepository.delete(banner);
    }
}