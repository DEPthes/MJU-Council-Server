package depth.mju.council.domain.minute.service;

import depth.mju.council.domain.minute.dto.req.CreateMinuteReq;
import depth.mju.council.domain.minute.dto.req.ModifyMinuteReq;
import depth.mju.council.domain.minute.dto.res.GetAllMinuteRes;
import depth.mju.council.domain.minute.dto.res.GetMinuteRes;
import depth.mju.council.domain.minute.entity.Minute;
import depth.mju.council.domain.minute.repository.MinuteRepository;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.payload.PageResponse;
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
    @Transactional
    public void createMinute(Long id, List<MultipartFile> imgs, CreateMinuteReq createMinuteReq) {
        UserEntity userEntity = userRepository.findById(id).get();
        // 회의록 저장 로직 필요
        Minute minute = Minute.builder()
                .title(createMinuteReq.getTitle())
                .content(createMinuteReq.getContent())
                .userEntity(userEntity)
                .build();
        minuteRepository.save(minute);
    }
    public PageResponse getAllMinute(Optional<String> keyword, int page, int size) {
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
        //회의록 파일들 가져오는 로직 필요함
//        User user = userRepository.findById(userId).get();
        Minute minutes = minuteRepository.findById(minuteId).get();
        return GetMinuteRes.builder()
                .minuteId(minutes.getId())
//                .writer(user.getName())
                .title(minutes.getTitle())
                .content(minutes.getContent())
                .date(minutes.getCreatedAt())
                .build();
    }
    @Transactional
    public void modifyMinute(Long minuteId, ModifyMinuteReq modifyMinuteReq, List<MultipartFile> imgs) {
        Minute minute = minuteRepository.findById(minuteId).get();
        minute.update(modifyMinuteReq);
        //사진 수정 로직 필요
    }
    @Transactional
    public void deleteMinute(Long minuteId) {
        Minute minute = minuteRepository.findById(minuteId).get();
        minuteRepository.delete(minute);
    }
}