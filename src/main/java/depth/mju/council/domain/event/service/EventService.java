package depth.mju.council.domain.event.service;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.event.dto.req.CreateEventDetailReq;
import depth.mju.council.domain.event.dto.req.CreateEventReq;
import depth.mju.council.domain.event.dto.req.ModifyEventDetailReq;
import depth.mju.council.domain.event.dto.res.EventDetailListRes;
import depth.mju.council.domain.event.dto.res.EventDetailRes;
import depth.mju.council.domain.event.dto.res.EventListRes;
import depth.mju.council.domain.event.dto.res.EventRes;
import depth.mju.council.domain.event.entity.Event;
import depth.mju.council.domain.event.entity.EventDetailFile;
import depth.mju.council.domain.event.entity.EventFile;
import depth.mju.council.domain.event.entity.EventDetail;
import depth.mju.council.domain.event.repository.EventFileRepository;
import depth.mju.council.domain.event.repository.EventDetailFileRepository;
import depth.mju.council.domain.event.repository.EventDetailRepository;
import depth.mju.council.domain.event.repository.EventRepository;
import depth.mju.council.domain.event.dto.req.ModifyEventReq;
import depth.mju.council.domain.notice.dto.res.FileRes;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.user.repository.UserRepository;
import depth.mju.council.global.DefaultAssert;
import depth.mju.council.global.config.UserPrincipal;
import depth.mju.council.infrastructure.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
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
public class EventService {
    private final EventRepository eventRepository;
    private final EventFileRepository eventFileRepository;
    private final EventDetailRepository eventDetailRepository;
    private final EventDetailFileRepository eventDetailFileRepository;
    private final UserRepository userRepository;

    private final S3Service s3Service;

    public EventRes getEvent(Long eventId) {
        Event event = validEventById(eventId);
        List<FileRes> images = eventFileRepository.findEventFilesByEventIdAndFileType(eventId, FileType.IMAGE);
        return EventRes.builder()
                .title(event.getTitle())
                .content(event.getContent())
                .startDate(event.getStartDate())
                .endDate(event.getEndDate())
                .images(images)
                .eventDetails(findEventDetail(eventId))
                .build();
    }

    private List<EventDetailListRes> findEventDetail(Long eventId) {
        return eventDetailRepository.findEventDetailsByEventId(eventId, false);
    }

    public List<EventListRes> getAllEvent() {
        return eventRepository.findEventsByIsDeleted(false);
    }

    @Transactional
    public void createEvent(
            UserPrincipal userPrincipal,
            List<MultipartFile> images, CreateEventReq createEventReq)
    {
        UserEntity user = validUserById(userPrincipal.getId());
        validDateRange(createEventReq.getStartDate(), createEventReq.getEndDate());
        Event event = Event.builder()
                .title(createEventReq.getTitle())
                .content(createEventReq.getContent())
                .startDate(createEventReq.getStartDate())
                .endDate(createEventReq.getEndDate())
                .userEntity(user)
                .build();
        eventRepository.save(event);
        uploadEventFiles(images, event, FileType.IMAGE);
    }

    private void uploadEventFiles(List<MultipartFile> files, Event event, FileType fileType) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveEventFiles(s3Service.uploadFile(file), file.getOriginalFilename(), fileType, event);
            }
        }
    }

    private void saveEventFiles(String fileUrl, String originalFileName, FileType fileType, Event event) {
        eventFileRepository.save(EventFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .fileType(fileType)
                .event(event)
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
    public void deleteEvent(Long eventId) {
        Event event = validEventById(eventId);
        // 행사 세부사항 삭제
        List<EventDetail> eventDetails = eventDetailRepository.findByEventId(eventId);
        for (EventDetail eventDetail : eventDetails) {
            deleteEventDetailFiles(eventDetailFileRepository.findByEventDetail(eventDetail));
        }
        eventDetailRepository.deleteEventDetailsByEvent(event);
        // 행사 삭제
        List<EventFile> eventFiles = eventFileRepository.findByEvent(event);
        deleteEventFiles(eventFiles);
        eventRepository.delete(event);
    }

    @Transactional
    public void deleteAllEvent() {
        eventRepository.updateIsDeletedForAll(true);
        eventFileRepository.updateIsDeletedForAll(true);
        eventDetailRepository.updateIsDeletedForAll(true);
        eventDetailFileRepository.updateIsDeletedForAll(true);
    }

    @Transactional
    public void modifyEvent(Long eventId, List<MultipartFile> images, ModifyEventReq modifyEventReq) {
        Event event = validEventById(eventId);
        validDateRange(modifyEventReq.getStartDate(), modifyEventReq.getEndDate());
        event.update(modifyEventReq.getTitle(), modifyEventReq.getContent(), modifyEventReq.getStartDate(), modifyEventReq.getEndDate());

        findEventFilesByIds(modifyEventReq.getDeleteImages());
        uploadEventFiles(images, event, FileType.IMAGE);
    }

    private void findEventFilesByIds(List<Integer> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<EventFile> filesToDelete = eventFileRepository.findAllById(fileIds);
        deleteEventFiles(filesToDelete);
    }

    private void deleteEventFiles(List<EventFile> files) {
        files.forEach(file -> {
            String saveFileName = s3Service.extractImageNameFromUrl(file.getFileUrl());
            // if (fileType == FileType.FILE) {
            //     s3Service.deleteFile(saveFileName);
            // } else {
                s3Service.deleteImage(saveFileName);
            // }
        });
        eventFileRepository.deleteEventFiles(files);
    }

    private UserEntity validUserById(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        DefaultAssert.isOptionalPresent(userOptional);
        return userOptional.get();
    }

    private Event validEventById(Long eventId) {
        Optional<Event> eventOptional = eventRepository.findByIdAndIsDeleted(eventId, false);
        DefaultAssert.isOptionalPresent(eventOptional);
        return eventOptional.get();
    }

    // ------------- Event Detail -------------
    @Transactional
    public void createEventDetail(
            Long eventId,
            List<MultipartFile> images, CreateEventDetailReq createEventDetailReq)
    {
        Event event = validEventById(eventId);
        EventDetail eventDetail = EventDetail.builder()
                .title(createEventDetailReq.getTitle())
                .content(createEventDetailReq.getContent())
                .event(event)
                .build();
        eventDetailRepository.save(eventDetail);
        uploadEventDetailFiles(images, eventDetail, FileType.IMAGE);
    }

    private void uploadEventDetailFiles(List<MultipartFile> files, EventDetail eventDetail, FileType fileType) {
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                saveEventDetailFiles(s3Service.uploadFile(file), file.getOriginalFilename(), fileType, eventDetail);
            }
        }
    }

    private void saveEventDetailFiles(String fileUrl, String originalFileName, FileType fileType, EventDetail eventDetail) {
        eventDetailFileRepository.save(EventDetailFile.builder()
                .fileUrl(fileUrl)
                .fileName(originalFileName)
                .fileType(fileType)
                .eventDetail(eventDetail)
                .build());
    }

    @Transactional
    public void deleteEventDetail(Long eventId, Long eventDetailId) {
        Event event = validEventById(eventId);
        EventDetail eventDetail = validEventDetailById(eventDetailId);
        DefaultAssert.isTrue(event == eventDetail.getEvent(), "잘못된 접근입니다.");

        List<EventDetailFile> eventDetailFiles = eventDetailFileRepository.findByEventDetail(eventDetail);
        deleteEventDetailFiles(eventDetailFiles);
        eventDetailRepository.deleteEventDetailsByEvent(event);
    }

    private EventDetail validEventDetailById(Long eventDetailId) {
        Optional<EventDetail> eventDetailOptional = eventDetailRepository.findByIdAndIsDeleted(eventDetailId, false);
        DefaultAssert.isOptionalPresent(eventDetailOptional);
        return eventDetailOptional.get();
    }

    @Transactional
    public void modifyEventDetail(Long eventId, Long eventDetailId, List<MultipartFile> images, ModifyEventDetailReq modifyEventDetailReq) {
        Event event = validEventById(eventId);
        EventDetail eventDetail = validEventDetailById(eventDetailId);
        DefaultAssert.isTrue(event == eventDetail.getEvent(), "잘못된 접근입니다.");
        eventDetail.updateTitleAndContent(modifyEventDetailReq.getTitle(), modifyEventDetailReq.getContent());

        findEventDetailFilesByIds(modifyEventDetailReq.getDeleteImages());
        uploadEventDetailFiles(images, eventDetail, FileType.IMAGE);
    }

    private void findEventDetailFilesByIds(List<Integer> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        List<Long> fileIds = files.stream().map(Long::valueOf).collect(Collectors.toList());
        List<EventDetailFile> filesToDelete = eventDetailFileRepository.findAllById(fileIds);
        deleteEventDetailFiles(filesToDelete);
    }

    private void deleteEventDetailFiles(List<EventDetailFile> files) {
        files.forEach(file -> {
            s3Service.deleteImage(s3Service.extractImageNameFromUrl(file.getFileUrl()));
        });
        eventDetailFileRepository.deleteEventDetailFiles(files);
    }

    public EventDetailRes getEventDetail(Long eventId, Long eventDetailId) {
        Event event = validEventById(eventId);
        EventDetail eventDetail = validEventDetailById(eventDetailId);
        DefaultAssert.isTrue(event == eventDetail.getEvent(), "잘못된 접근입니다.");
        List<FileRes> images = eventDetailFileRepository.findEventDetailFilesByEventDetailIdAndFileType(eventDetailId, FileType.IMAGE);
        return EventDetailRes.builder()
                .title(eventDetail.getTitle())
                .content(eventDetail.getContent())
                .createdAt(eventDetail.getCreatedAt().toLocalDate())
                .images(images)
                .build();
    }

}