package depth.mju.council.domain.minute.service;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.minute.dto.req.CreateMinuteReq;
import depth.mju.council.domain.minute.dto.req.ModifyMinuteReq;
import depth.mju.council.domain.minute.dto.res.GetMinuteFileRes;
import depth.mju.council.domain.minute.dto.res.GetMinuteRes;
import depth.mju.council.domain.minute.entity.Minute;
import depth.mju.council.domain.minute.entity.MinuteFile;
import depth.mju.council.domain.minute.repository.MinuteFileRepository;
import depth.mju.council.domain.minute.repository.MinuteRepository;
import depth.mju.council.domain.minute.dto.res.GetAllMinuteRes;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MinuteService {
    private final UserRepository userRepository;
    private final MinuteRepository minuteRepository;
    private final MinuteFileRepository minuteFileRepository;
    private final S3Service s3Service;
    @Transactional
    public void createMinute(Long id, List<MultipartFile> files, CreateMinuteReq createMinuteReq) {
        UserEntity user = validUserById(id);
        Minute minute = Minute.builder()
                .title(createMinuteReq.getTitle())
                .content(createMinuteReq.getContent())
                .userEntity(user)
                .build();
        minuteRepository.save(minute);
        uploadMinuteFiles(files, minute);
    }
    public PageResponse  getAllMinute(int page, int size, Optional<String> keyword) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Minute> pageResult;
        if (keyword.isPresent()) {
            pageResult = minuteRepository.findByTitleContaining(keyword.get(), pageRequest);
        } else {
            pageResult = minuteRepository.findAll(pageRequest);
        }
        return PageResponse.builder()
                .totalElements(pageResult.getTotalElements())
                .totalPage(pageResult.getTotalPages())
                .pageSize(pageResult.getSize())
                .contents(pageResult.getContent().stream()
                        .map(getMinute -> GetAllMinuteRes.builder()
                                .minuteId(getMinute.getId())
                                .title(getMinute.getTitle())
                                .date(getMinute.getCreatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
    public GetMinuteRes getMinute(Long minuteId) {
        Minute minutes = validaMinuteById(minuteId);
        List<MinuteFile> minuteFiles = minuteFileRepository.findByMinute(minutes);

        List<GetMinuteFileRes> getMinuteFileRes = minuteFiles.stream()
                .map(file -> GetMinuteFileRes.builder()
                        .minuteFileId(file.getId())
                        .fileUrl(file.getFileUrl())
                        .fileName(file.getFileName())
                        .build())
                .collect(Collectors.toList());

        return GetMinuteRes.builder()
                .minuteId(minutes.getId())
//                .writer(user.getName())
                .title(minutes.getTitle())
                .content(minutes.getContent())
                .date(minutes.getCreatedAt())
                .files(getMinuteFileRes)
                .build();
    }
    @Transactional
    public void modifyMinute(Long minuteId, ModifyMinuteReq modifyMinuteReq, List<MultipartFile> files) {
        Minute minute = validaMinuteById(minuteId);
        minute.update(modifyMinuteReq);
        deleteMinuteFiles(modifyMinuteReq.getDeleteFiles(), FileType.FILE);
        uploadMinuteFiles(files, minute);
    }
    @Transactional
    public void deleteMinute(Long minuteId) {
        Minute minute = validaMinuteById(minuteId);
        List<MinuteFile> minuteFiles = minuteFileRepository.findByMinute(minute);
        // minuteFiles의 ID 리스트를 Integer로 추출
        List<Integer> fileIds = minuteFiles.stream()
                .map(file -> file.getId().intValue())  // Long을 Integer로 변환
                .collect(Collectors.toList());
        deleteMinuteFiles(fileIds,FileType.FILE);
        minuteRepository.delete(minute);
    }
    @Transactional
    public void deleteAllMinute() {
        List<MinuteFile> minuteFiles = minuteFileRepository.findAll();
        // minuteFiles의 ID 리스트를 Integer로 추출
        List<Integer> fileIds = minuteFiles.stream()
                .map(file -> file.getId().intValue())  // Long을 Integer로 변환
                .collect(Collectors.toList());
        deleteMinuteFiles(fileIds,FileType.FILE);
        minuteRepository.deleteAll();
    }
    private void uploadMinuteFiles(List<MultipartFile> files, Minute minute) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveUploadFiles(s3Service.uploadFile(file), file.getOriginalFilename(), minute);
            }
        }
    }
    private void saveUploadFiles(String fileUrl, String originalFileName,Minute minute) {
        minuteFileRepository.save(MinuteFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .minute(minute)
                .build());
    }
    private void deleteMinuteFiles(List<Integer> files, FileType fileType) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<MinuteFile> filesToDelete = minuteFileRepository.findAllById(fileIds);
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
            minuteFileRepository.delete(file);
        });
    }
    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }
    private Minute validaMinuteById(Long bannerId) {
        Optional<Minute> minuteOptional = minuteRepository.findById(bannerId);
        DefaultAssert.isOptionalPresent(minuteOptional);
        return minuteOptional.get();
    }
}