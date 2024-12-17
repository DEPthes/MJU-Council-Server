package depth.mju.council.domain.regulation.service;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.regulation.dto.req.CreateRegulationReq;
import depth.mju.council.domain.regulation.dto.req.ModifyRegulationReq;
import depth.mju.council.domain.regulation.dto.res.GetAllRegulationRes;
import depth.mju.council.domain.regulation.dto.res.GetRegulationFileRes;
import depth.mju.council.domain.regulation.dto.res.GetRegulationRes;
import depth.mju.council.domain.regulation.entity.Regulation;
import depth.mju.council.domain.regulation.entity.RegulationFile;
import depth.mju.council.domain.regulation.repository.RegulationFileRepository;
import depth.mju.council.domain.regulation.repository.RegulationRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import depth.mju.council.global.payload.PageResponse;
import depth.mju.council.infrastructure.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RegulationService {
    private final UserRepository userRepository;
    private final RegulationRepository regulationRepository;
    private final RegulationFileRepository regulationFileRepository;
    private final S3Service s3Service;
    @Transactional
    public void createRegulation(Long userId, List<MultipartFile> files, CreateRegulationReq createRegulationReq) {
        UserEntity user = validUserById(userId);

        Regulation regulation = Regulation.builder()
                .title(createRegulationReq.getTitle())
                .content(createRegulationReq.getContent())
                .userEntity(user)
                .build();
        regulationRepository.save(regulation);
        uploadRegulationFiles(files, regulation);
    }
    public PageResponse getAllRegulation(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Regulation> pageResult;
        pageResult = regulationRepository.findAll(pageRequest);
        return PageResponse.builder()
                .totalElements(pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .pageSize(pageResult.getSize())
                .contents(pageResult.getContent().stream()
                        .map(regulation -> {
                            // 규정에 연결된 파일들의 URL 목록 가져오기
                            List<RegulationFile> regulationFiles = regulationFileRepository.findByRegulation(regulation);
                            List<String> fileUrls = regulationFiles.stream()
                                    .map(RegulationFile::getFileUrl)
                                    .collect(Collectors.toList());

                            return GetAllRegulationRes.builder()
                                    .regulationId(regulation.getId())
                                    .title(regulation.getTitle())
                                    .date(regulation.getCreatedAt())
                                    .fileUrls(fileUrls) // fileUrls 추가
                                    .build();
                        })
                        .collect(Collectors.toList()))
                .build();
    }
    public GetRegulationRes getRegulation(Long regulationId) {
        Regulation regulation = validateRegulationById(regulationId);
        List<RegulationFile> regulationFiles = regulationFileRepository.findByRegulation(regulation);

        List<GetRegulationFileRes> getRegulationFileRes = regulationFiles.stream()
                .map(file -> GetRegulationFileRes.builder()
                        .regulationFileId(file.getId())
                        .fileUrl(file.getFileUrl())
                        .fileName(file.getFileName())
                        .build())
                .collect(Collectors.toList());

        return GetRegulationRes.builder()
                .regulationId(regulation.getId())
//                .writer(user.getName())
                .title(regulation.getTitle())
                .content(regulation.getContent())
                .date(regulation.getCreatedAt())
                .files(getRegulationFileRes)
                .build();

    }
    @Transactional
    public void modifyRegulation(Long regulationId, List<MultipartFile> files, ModifyRegulationReq modifyRegulationReq) {
        Regulation regulation = validateRegulationById(regulationId);
        regulation.update(modifyRegulationReq);
        deleteRegulationFiles(modifyRegulationReq.getDeleteFiles(), FileType.FILE);
        uploadRegulationFiles(files, regulation);
    }

    @Transactional
    public void deleteRegulation(Long regulationId) {
        Regulation regulation = validateRegulationById(regulationId);
        List<RegulationFile> regulationFiles = regulationFileRepository.findByRegulation(regulation);
        List<Integer> fileIds = regulationFiles.stream()
                .map(file -> file.getId().intValue())  // Long을 Integer로 변환
                .toList();
        deleteRegulationFiles(fileIds,FileType.FILE);
        regulationRepository.delete(regulation);
    }
    @Transactional
    public void deleteAllRegulation() {
        List<RegulationFile> regulationFiles = regulationFileRepository.findAll();
        // regulationFiles의 ID 리스트를 Integer로 추출
        List<Integer> fileIds = regulationFiles.stream()
                .map(file -> file.getId().intValue())  // Long을 Integer로 변환
                .collect(Collectors.toList());
        deleteRegulationFiles(fileIds, FileType.FILE);
        regulationRepository.deleteAll();
    }
    private void uploadRegulationFiles(List<MultipartFile> files, Regulation regulation) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveUploadFiles(s3Service.uploadFile(file), file.getOriginalFilename(), regulation);
            }
        }
    }
    private void saveUploadFiles(String fileUrl, String originalFileName,Regulation regulation) {
        regulationFileRepository.save(RegulationFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .regulation(regulation)
                .build());
    }
    private void deleteRegulationFiles(List<Integer> files, FileType fileType) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<RegulationFile> filesToDelete = regulationFileRepository.findAllById(fileIds);
        filesToDelete.forEach(file -> {
            // 저장 파일명 구하기
            String saveFileName = s3Service.extractImageNameFromUrl(file.getFileUrl());
            // S3에서 삭제
            if (fileType == FileType.FILE) {
                s3Service.deleteFile(saveFileName);
            } else {
                s3Service.deleteImage(saveFileName);
            }
            // DB에서 삭제
            regulationFileRepository.delete(file);
        });
    }
    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }
    private Regulation validateRegulationById(Long regulationId) {
        Optional<Regulation> regulationOptional = regulationRepository.findById(regulationId);
        DefaultAssert.isOptionalPresent(regulationOptional);
        return regulationOptional.get();
    }
}