package depth.mju.council.domain.promise.service;

import depth.mju.council.domain.promise.dto.req.CreatePromiseReq;
import depth.mju.council.domain.promise.dto.req.ModifyPromiseReq;
import depth.mju.council.domain.promise.dto.res.PromiseRes;
import depth.mju.council.domain.promise.entity.Promise;
import depth.mju.council.domain.promise.entity.PromiseCategory;
import depth.mju.council.domain.promise.repository.PromiseCategoryRepository;
import depth.mju.council.domain.promise.repository.PromiseRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.payload.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromiseService {
    private final UserRepository userRepository;
    private final PromiseCategoryRepository promiseCategoryRepository;
    private final PromiseRepository promiseRepository;
    @Transactional
    public void createPromise(Long userId, String promiseTitle, CreatePromiseReq createPromiseReq) {
        UserEntity user = userRepository.findById(userId).get();
        // 정책 가져오기
        PromiseCategory promiseCategory = promiseCategoryRepository.findByUserEntityAndTitle(user,promiseTitle);
        // 공약 생성
        Promise promise = Promise.builder()
                .title(createPromiseReq.getTitle())
                .content(createPromiseReq.getContent())
                .progress(createPromiseReq.getProgress())
                .promiseCategory(promiseCategory)
                .build();
        promiseRepository.save(promise);
    }

    public List<PromiseRes> getPromise(Long userId, String promiseTitle) {
        UserEntity user = userRepository.findById(userId).get();
        PromiseCategory promiseCategory = promiseCategoryRepository.findByUserEntityAndTitle(user, promiseTitle);
        List<Promise> promises = promiseRepository.findByPromiseCategory(promiseCategory);
        List<PromiseRes> promiseRes = promises.stream()
                .map(promise -> PromiseRes.builder()
                        .promiseCategoryId(promise.getId())
                        .title(promise.getTitle())
                        .content(promise.getContent())
                        .progress(promise.getProgress())
                        .build())
                .collect(Collectors.toList());
        return promiseRes;
    }
    @Transactional
    public void deletePromise(Long promiseId) {
        Promise promise = promiseRepository.findById(promiseId).get();
        promiseRepository.delete(promise);
    }
    @Transactional
    public void modifyPromise(Long promiseId, ModifyPromiseReq modifyPromiseReq) {
        Promise promise = promiseRepository.findById(promiseId).get();
        promise.update(modifyPromiseReq);
    }
}