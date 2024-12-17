package depth.mju.council.domain.committe.service;

import depth.mju.council.domain.committe.dto.res.CommitteeRes;
import depth.mju.council.domain.committe.entity.Committee;
import depth.mju.council.domain.committe.repository.CommitteeRepository;
import depth.mju.council.domain.committe.dto.req.CreateCommitteeReq;
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
public class CommitteeService {
    private final CommitteeRepository committeeRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<CommitteeRes> getAllCommittees() {
        List<Committee> committees = committeeRepository.findAll();

        return committees.stream()
                .map(committee -> CommitteeRes.builder()
                        .committeeId(committee.getId())
                        .description(committee.getDescription())
                        .college(committee.getCollege())
                        .name(committee.getName())
                        .pageUrl(committee.getPageUrl())
                        .snsUrl(committee.getSnsUrl())
                        .imgUrl(committee.getImgUrl())
                        .build())
                .collect(Collectors.toList());
    }

    //현재 하나씩 저장 // 여러개씩 저장으로 바뀔 수도 있음 (소개이미지, 조직도, 국별업무, 중운위에 해당)
    @Transactional
    public void createCommittee(CreateCommitteeReq request, MultipartFile image, UserPrincipal userPrincipal) {
        UserEntity user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        Committee committee = Committee.builder()
                .description(request.description)
                .college(request.college)
                .name(request.name)
                .pageUrl(request.pageUrl)
                .snsUrl(request.snsUrl)
                .imgUrl(s3Service.uploadImage(image))
                .userEntity(user)
                .build();
        committeeRepository.save(committee);
    }

    @Transactional
    public void updateCommittee(Long committeeId, CreateCommitteeReq request, MultipartFile image,
                                UserPrincipal userPrincipal) {
        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "중운위(단과대)를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지 삭제
        String oldImageUrl = committee.getImgUrl();
        if (oldImageUrl != null) {
            String oldImageName = s3Service.extractImageNameFromUrl(oldImageUrl);
            s3Service.deleteImage(oldImageName);
        }

        // 새 이미지 업로드
        String newImageUrl = s3Service.uploadImage(image);
        committee.update(request.description, request.college, request.name, request.pageUrl, request.snsUrl,
                newImageUrl);

        committeeRepository.save(committee);
    }

    @Transactional
    public void deleteCommittee(Long committeeId, UserPrincipal userPrincipal) {
        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "중운위(단과대)를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 이미지 삭제
        String imageUrl = committee.getImgUrl();
        if (imageUrl != null) {
            String imageName = s3Service.extractImageNameFromUrl(imageUrl);
            s3Service.deleteImage(imageName);
        }

        // 중운위(단과대) 삭제
        committeeRepository.delete(committee);
    }
}