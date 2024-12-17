package depth.mju.council.domain.event.repository;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.event.entity.Event;
import depth.mju.council.domain.event.entity.EventFile;
import depth.mju.council.domain.notice.dto.res.FileRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventFileRepository extends JpaRepository<EventFile, Long> {

    @Modifying
    @Query("UPDATE EventFile ef SET ef.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    @Query("SELECT new depth.mju.council.domain.notice.dto.res.FileRes(ef.id, ef.fileName, ef.fileUrl) " +
            "FROM EventFile ef " +
            "WHERE ef.event.id = :eventId AND ef.fileType = :fileType " +
            "ORDER BY ef.createdAt ASC")
    List<FileRes> findEventFilesByEventIdAndFileType(@Param("eventId") Long eventId, @Param("fileType") FileType fileType);

    @Modifying
    @Query("DELETE FROM EventFile ef WHERE ef IN :files")
    void deleteEventFiles(@Param("files") List<EventFile> files);

    List<EventFile> findByEvent(Event event);
}
