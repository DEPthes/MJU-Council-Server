package depth.mju.council.domain.alliance.service;

import depth.mju.council.domain.alliance.dto.req.CreateAllianceReq;
import depth.mju.council.domain.alliance.dto.req.ModifyAllianceReq;
import depth.mju.council.domain.alliance.dto.res.AllianceListRes;
import depth.mju.council.domain.alliance.dto.res.AllianceRes;
import depth.mju.council.domain.alliance.entity.Alliance;
import depth.mju.council.domain.alliance.entity.AllianceFile;
import depth.mju.council.domain.alliance.repository.AllianceFileRepository;
import depth.mju.council.domain.alliance.repository.AllianceRepository;
import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.dto.res.FileRes;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.global.payload.PageResponse;
import depth.mju.council.infrastructure.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AllianceService {

    private final AllianceRepository allianceRepository;
    private final AllianceFileRepository allianceFileRepository;
    private final UserRepository userRepository;

    private final S3Service s3Service;

    public AllianceRes getAlliance(Long allianceId) {
        Alliance alliance = validAllianceById(allianceId);
        List<FileRes> images = allianceFileRepository.findAllianceFilesByAllianceIdAndFileType(allianceId, FileType.IMAGE);
        List<FileRes> files = allianceFileRepository.findAllianceFilesByAllianceIdAndFileType(allianceId, FileType.FILE);
        return AllianceRes.builder()
                .title(alliance.getTitle())
                .content(alliance.getContent())
                .startDate(alliance.getStartDate())
                .endDate(alliance.getEndDate())
                .images(images)
                .files(files)
                .build();
    }

    public PageResponse<AllianceListRes> getAllAlliance(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<AllianceListRes> AllianceListResponses = allianceRepository.findAlliancesByIsDeleted(false, pageRequest);
        return PageResponse.<AllianceListRes>builder()
                .totalElements(AllianceListResponses.getTotalElements())
                .totalPage(AllianceListResponses.getTotalPages())
                .pageSize(AllianceListResponses.getSize())
                .contents(AllianceListResponses.getContent())
                .build();
    }

    @Transactional
    public void createAlliance(
            UserPrincipal userPrincipal,
            List<MultipartFile> images, List<MultipartFile> files, CreateAllianceReq createAllianceReq)
    {
        UserEntity user = validUserById(userPrincipal.getId());
        validDateRange(createAllianceReq.getStartDate(), createAllianceReq.getEndDate());
        Alliance alliance = Alliance.builder()
                .title(createAllianceReq.getTitle())
                .content(createAllianceReq.getContent())
                .startDate(createAllianceReq.getStartDate())
                .endDate(createAllianceReq.getEndDate())
                .userEntity(user)
                .build();
        allianceRepository.save(alliance);
        uploadAllianceFiles(images, alliance, FileType.IMAGE);
        uploadAllianceFiles(files, alliance, FileType.FILE);
    }

    private void uploadAllianceFiles(List<MultipartFile> files, Alliance alliance, FileType fileType) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveAllianceFiles(s3Service.uploadFile(file), file.getOriginalFilename(), fileType, alliance);
            }
        }
    }

    private void saveAllianceFiles(String fileUrl, String originalFileName, FileType fileType, Alliance alliance) {
        allianceFileRepository.save(AllianceFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .fileType(fileType)
                .alliance(alliance)
                .build());
    }

    private void validDateRange(LocalDate startDate, LocalDate endDate) {
        DefaultAssert.notNull(startDate, "시작 일자는 비어있으면 안 됩니다.");
        DefaultAssert.notNull(endDate, "종료 일자는 비어있으면 안 됩니다.");
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("종료 일자는 시작 일자보다 빠를 수 없습니다.");
        }
    }

    @Transactional
    public void deleteAlliance(Long allianceId) {
        Alliance alliance = validAllianceById(allianceId);
        List<AllianceFile> allianceFiles = allianceFileRepository.findByAlliance(alliance);
        deleteAllianceFiles(allianceFiles, FileType.FILE);
        deleteAllianceFiles(allianceFiles, FileType.IMAGE);

        allianceRepository.delete(alliance);
    }

    @Transactional
    public void deleteAllAlliance() {
        allianceRepository.updateIsDeletedForAll(true);
        allianceFileRepository.updateIsDeletedForAll(true);
    }

    @Transactional
    public void modifyAlliance(Long allianceId, List<MultipartFile> images, List<MultipartFile> files, ModifyAllianceReq modifyAllianceReq) {
        Alliance alliance = validAllianceById(allianceId);
        validDateRange(modifyAllianceReq.getStartDate(), modifyAllianceReq.getEndDate());
        alliance.update(modifyAllianceReq.getTitle(), modifyAllianceReq.getContent(), modifyAllianceReq.getStartDate(), modifyAllianceReq.getEndDate());

        findAllianceFilesByIds(modifyAllianceReq.getDeleteImages(), FileType.IMAGE);
        findAllianceFilesByIds(modifyAllianceReq.getDeleteFiles(), FileType.FILE);
        uploadAllianceFiles(images, alliance, FileType.IMAGE);
        uploadAllianceFiles(files, alliance, FileType.FILE);
    }

    private void findAllianceFilesByIds(List<Integer> files, FileType fileType) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<AllianceFile> filesToDelete = allianceFileRepository.findAllById(fileIds);
        deleteAllianceFiles(filesToDelete, fileType);
    }

    private void deleteAllianceFiles(List<AllianceFile> files, FileType fileType) {
        files.forEach(file -> {
            String saveFileName = s3Service.extractImageNameFromUrl(file.getFileUrl());
            if (fileType == FileType.FILE) {
                s3Service.deleteFile(saveFileName);
            } else {
            s3Service.deleteImage(saveFileName);
            }
        });
        allianceFileRepository.deleteAllianceFiles(files);
    }

    private Alliance validAllianceById(Long allianceId) {
        Optional<Alliance> allianceOptional = allianceRepository.findByIdAndIsDeleted(allianceId, false);
        DefaultAssert.isOptionalPresent(allianceOptional);
        return allianceOptional.get();
    }

    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }
}