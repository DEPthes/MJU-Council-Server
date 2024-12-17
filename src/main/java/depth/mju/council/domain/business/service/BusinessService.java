package depth.mju.council.domain.business.service;

import depth.mju.council.domain.business.dto.req.CreateBusinessReq;
import depth.mju.council.domain.business.dto.req.ModifyBusinessReq;
import depth.mju.council.domain.business.dto.res.BusinessListRes;
import depth.mju.council.domain.business.dto.res.BusinessRes;
import depth.mju.council.domain.business.entity.Business;
import depth.mju.council.domain.business.entity.BusinessFile;
import depth.mju.council.domain.business.repository.BusinessFileRepository;
import depth.mju.council.domain.business.repository.BusinessRepository;
import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.dto.res.FileRes;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.PageResponse;
import depth.mju.council.infrastructure.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.hibernate.id.IncrementGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessService {

    private final S3Service s3Service;

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final BusinessFileRepository businessFileRepository;

    public BusinessRes getBusiness(Long businessId) {
        Business business = validBusinessById(businessId);
        List<FileRes> images = businessFileRepository.findBusinessFilesByBusinessIdAndFileType(businessId, FileType.IMAGE);
        List<FileRes> files = businessFileRepository.findBusinessFilesByBusinessIdAndFileType(businessId, FileType.FILE);

        return BusinessRes.builder()
                .title(business.getTitle())
                .content(business.getContent())
                .createdAt(business.getCreatedAt().toLocalDate())
                .images(images)
                .files(files)
                .build();
    }

    public PageResponse<BusinessListRes> getAllBusiness(Optional<String> keyword, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

        Page<BusinessListRes> businessListResponses;
        if (keyword.isPresent()) {
            businessListResponses = businessRepository.findByTitleContaining(keyword.get(), pageRequest);
        } else {
            businessListResponses = businessRepository.findAllBusinesses(pageRequest);
        }
        return PageResponse.<BusinessListRes>builder()
                .totalElements(businessListResponses.getTotalElements())
                .totalPage(businessListResponses.getTotalPages())
                .pageSize(businessListResponses.getSize())
                .contents(businessListResponses.getContent())
                .build();
    }

    @Transactional
    public void createBusiness (
            UserPrincipal userPrincipal, List<MultipartFile> images, List<MultipartFile> files, CreateBusinessReq createBusinessReq)
    {
        UserEntity user = validUserById(userPrincipal.getId());
        Business business = Business.builder()
                .title(createBusinessReq.getTitle())
                .content(createBusinessReq.getContent())
                .userEntity(user)
                .build();
        businessRepository.save(business);

        uploadBusinessFiles(images, business, FileType.IMAGE);
        uploadBusinessFiles(files, business, FileType.FILE);
    }

    private void uploadBusinessFiles(List<MultipartFile> files, Business business, FileType fileType) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveBusinessFiles(s3Service.uploadFile(file), file.getOriginalFilename(), fileType, business);
            }
        }
    }

    private void saveBusinessFiles(String fileUrl, String originalFileName, FileType fileType, Business business) {
        businessFileRepository.save(BusinessFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .fileType(fileType)
                .business(business)
                .build());
    }

    @Transactional
    public void deleteBusiness(Long businessId) {
        Business business = validBusinessById(businessId);
        List<BusinessFile> businessFiles = businessFileRepository.findByBusiness(business);
        deleteBusinessFiles(businessFiles, FileType.FILE);
        deleteBusinessFiles(businessFiles, FileType.IMAGE);

        businessRepository.delete(business);
    }

    @Transactional
    public void deleteAllBusiness() {
        businessRepository.updateIsDeletedForAll(true);
        businessFileRepository.updateIsDeletedForAll(true);
    }

    @Transactional
    public void modifyBusiness(Long businessId, List<MultipartFile> images, List<MultipartFile> files, ModifyBusinessReq modifyBusinessReq) {
        Business business = validBusinessById(businessId);
        business.updateTitleAndContent(modifyBusinessReq.getTitle(), modifyBusinessReq.getContent());
        // 지우고자 하는 이미지/파일 삭제
        findBusinessFilesByIds(modifyBusinessReq.getDeleteFiles(), FileType.FILE);
        findBusinessFilesByIds(modifyBusinessReq.getDeleteImages(), FileType.IMAGE);
        // 파일/이미지 업로드
        uploadBusinessFiles(images, business, FileType.IMAGE);
        uploadBusinessFiles(files, business, FileType.FILE);
    }

    private void findBusinessFilesByIds(List<Integer> files, FileType fileType) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<BusinessFile> filesToDelete = businessFileRepository.findAllById(fileIds);
        deleteBusinessFiles(filesToDelete, fileType);
    }

    private void deleteBusinessFiles(List<BusinessFile> files, FileType fileType) {
        files.forEach(file -> {
            // 저장 파일명 구하기
            String saveFileName = s3Service.extractImageNameFromUrl(file.getFileUrl());
            // S3에서 삭제
            if (fileType == FileType.FILE) {
                s3Service.deleteFile(saveFileName);
            } else {
                s3Service.deleteImage(saveFileName);
            }
        });
        businessFileRepository.deleteBusinessFiles(files);
    }

    private Business validBusinessById(Long businessId) {
        Optional<Business> businessOptional = businessRepository.findByIdAndIsDeleted(businessId, false);
        DefaultAssert.isOptionalPresent(businessOptional);
        return businessOptional.get();
    }

    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }
}