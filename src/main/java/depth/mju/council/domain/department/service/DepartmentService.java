package depth.mju.council.domain.department.service;

import depth.mju.council.domain.department.dto.res.DepartmentRes;
import depth.mju.council.domain.department.entity.Department;
import depth.mju.council.domain.department.repository.DepartmentRepository;
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
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional(readOnly = true)
    public List<DepartmentRes> getAllDepartments() {
        List<Department> departments = departmentRepository.findAll();

        return departments.stream()
                .map(department -> DepartmentRes.builder()
                        .departmentId(department.getId())
                        .description(department.getDescription())
                        .imgUrl(department.getImgUrl())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void createDepartment(String description, MultipartFile image, UserPrincipal userPrincipal) {
        UserEntity user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        String imgUrl = s3Service.uploadImage(image);
        Department department = Department.builder()
                .description(description)
                .imgUrl(imgUrl)
                .userEntity(user)
                .build();
        departmentRepository.save(department);
    }

    @Transactional
    public void updateDepartment(Long departmentId, String description, MultipartFile image, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "국 별 소개를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 기존 이미지 삭제
        String oldImageUrl = department.getImgUrl();
        if (oldImageUrl != null) {
            String oldImageName = s3Service.extractImageNameFromUrl(oldImageUrl);
            s3Service.deleteImage(oldImageName);
        }
        // 새 이미지 업로드
        String newImageUrl = s3Service.uploadImage(image);
        department.updateDescriptionAndImgUrl(description, newImageUrl);

        departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long departmentId, UserPrincipal userPrincipal) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DefaultException(ErrorCode.CONTENTS_NOT_FOUND, "국 별 소개를 찾을 수 없습니다."));
        userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new DefaultException(ErrorCode.USER_NOT_FOUND));

        // 이미지 삭제
        String imageUrl = department.getImgUrl();
        if (imageUrl != null) {
            String imageName = s3Service.extractImageNameFromUrl(imageUrl);
            s3Service.deleteImage(imageName);
        }

        // 국별 소개 삭제
        departmentRepository.delete(department);
    }
}