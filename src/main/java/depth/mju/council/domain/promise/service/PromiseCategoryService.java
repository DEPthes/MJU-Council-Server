package depth.mju.council.domain.promise.service;

import depth.mju.council.domain.promise.dto.res.PromiseCategoryRes;
import depth.mju.council.domain.promise.entity.PromiseCategory;
import depth.mju.council.domain.promise.repository.PromiseCategoryRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromiseCategoryService {
    private final UserRepository userRepository;
    private final PromiseCategoryRepository promiseCategoryRepository;
    @Transactional
    public void createPromiseCategory(Long userId, String promiseTitle) {
        UserEntity userEntity = userRepository.findById(userId).get();
        PromiseCategory promiseCategory = PromiseCategory.builder()
                .title(promiseTitle)
                .userEntity(userEntity)
                .build();
        promiseCategoryRepository.save(promiseCategory);
    }
    public List<PromiseCategoryRes> getPromiseCategory(Long userId) {
        UserEntity user = userRepository.findById(userId).get();
        List<PromiseCategory> promiseCategories = promiseCategoryRepository.findByUserEntity(user);
        List<PromiseCategoryRes> promiseCategoryRes = promiseCategories.stream()
                .map(promiseCategory -> PromiseCategoryRes.builder()
                        .promiseCategoryId(promiseCategory.getId())
                        .title(promiseCategory.getTitle())
                        .build())
                .collect(Collectors.toList());
        return promiseCategoryRes;
    }
    @Transactional
    public void modifyPromiseCategory(Long promiseId, String promiseTitle) {
        PromiseCategory promiseCategory = promiseCategoryRepository.findById(promiseId).get();
        promiseCategory.updatepromiseTitle(promiseTitle);
    }
    @Transactional
    public void deletePromiseCategory(Long promiseId) {
        PromiseCategory promiseCategory = promiseCategoryRepository.findById(promiseId).get();
        promiseCategoryRepository.delete(promiseCategory);
    }
}