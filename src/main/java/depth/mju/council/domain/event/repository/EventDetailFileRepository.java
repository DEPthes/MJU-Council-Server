package depth.mju.council.domain.event.repository;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.event.entity.EventDetail;
import depth.mju.council.domain.event.entity.EventDetailFile;
import depth.mju.council.domain.notice.dto.res.FileRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDetailFileRepository extends JpaRepository<EventDetailFile, Long> {

    @Modifying
    @Query("UPDATE EventDetailFile edf SET edf.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    @Query("SELECT new depth.mju.council.domain.notice.dto.res.FileRes(edf.id, edf.fileName, edf.fileUrl) " +
            "FROM EventDetailFile edf " +
            "WHERE edf.eventDetail.id = :eventDetailId AND edf.fileType = :fileType " +
            "ORDER BY edf.createdAt ASC")
    List<FileRes> findEventDetailFilesByEventDetailIdAndFileType(@Param("eventDetailId") Long eventDetailId, @Param("fileType") FileType fileType);


    @Modifying
    @Query("DELETE FROM EventDetailFile edf WHERE edf.eventDetail = :eventDetail")
    void deleteFilesByEventDetail(@Param("eventDetail") EventDetail eventDetail);

    List<EventDetailFile> findByEventDetail(EventDetail eventDetail);

    @Modifying
    @Query("DELETE FROM EventDetailFile edf WHERE edf IN :files")
    void deleteEventDetailFiles(@Param("files") List<EventDetailFile> files);
}
