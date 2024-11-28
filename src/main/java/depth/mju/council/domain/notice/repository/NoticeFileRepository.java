package depth.mju.council.domain.notice.repository;

import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.dto.res.FileRes;
import depth.mju.council.domain.notice.entity.NoticeFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeFileRepository extends JpaRepository<NoticeFile, Long> {
    @Modifying
    @Query("UPDATE NoticeFile nf SET nf.isDeleted = :isDeleted WHERE nf.notice.id = :noticeId")
    void updateIsDeletedByNoticeId(@Param("noticeId") Long noticeId, @Param("isDeleted") boolean isDeleted);

    @Modifying
    @Query("UPDATE NoticeFile nf SET nf.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    @Query("SELECT new depth.mju.council.domain.notice.dto.res.FileRes(nf.id, nf.fileName, nf.fileUrl) " +
            "FROM NoticeFile nf " +
            "WHERE nf.notice.id = :noticeId AND nf.fileType = :fileType " +
            "ORDER BY nf.createdAt ASC")
    List<FileRes> findNoticeFilesByNoticeIdAndFileType(@Param("noticeId") Long noticeId, @Param("fileType") FileType fileType);


    void deleteAllByIdIn(List<Long> list);
}
