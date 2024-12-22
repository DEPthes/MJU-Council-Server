package depth.mju.council.domain.promise.service;

import depth.mju.council.domain.promise.dto.res.PromiseCategoryRes;
import depth.mju.council.domain.promise.entity.Promise;
import depth.mju.council.domain.promise.entity.PromiseCategory;
import depth.mju.council.domain.promise.repository.PromiseCategoryRepository;
import depth.mju.council.domain.promise.repository.PromiseRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromiseCategoryService {
    private final UserRepository userRepository;
    private final PromiseCategoryRepository promiseCategoryRepository;
    private final PromiseRepository promiseRepository;
    @Transactional
    public void createPromiseCategory(Long userId, String promiseTitle) {
        UserEntity user = validUserById(userId);
        PromiseCategory promiseCategory = PromiseCategory.builder()
                .title(promiseTitle)
                .userEntity(user)
                .build();
        promiseCategoryRepository.save(promiseCategory);
    }
    public List<PromiseCategoryRes> getPromiseCategory() {
        List<PromiseCategory> promiseCategories = promiseCategoryRepository.findAll();
        return promiseCategories.stream()
                .map(promiseCategory -> PromiseCategoryRes.builder()
                        .promiseCategoryId(promiseCategory.getId())
                        .title(promiseCategory.getTitle())
                        .build())
                .collect(Collectors.toList());
    }
    @Transactional
    public void modifyPromiseCategory(Long promiseCategoryId, String promiseTitle) {
        PromiseCategory promiseCategory = validatePromiseCategoryById(promiseCategoryId);
        promiseCategory.updatepromiseTitle(promiseTitle);
    }
    @Transactional
    public void deletePromiseCategory(Long promiseCategoryId) {
        PromiseCategory promiseCategory = validatePromiseCategoryById(promiseCategoryId);
        List<Promise> promises = promiseRepository.findByPromiseCategory(promiseCategory);
        promiseRepository.deleteAll(promises);
        promiseCategoryRepository.delete(promiseCategory);
    }
    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }
    private PromiseCategory validatePromiseCategoryById(Long categoryId) {
        Optional<PromiseCategory> categoryOptional = promiseCategoryRepository.findById(categoryId);
        DefaultAssert.isOptionalPresent(categoryOptional);
        return categoryOptional.get();
    }
}