package depth.mju.council.domain.alliance.repository;

import depth.mju.council.domain.alliance.entity.Alliance;
import depth.mju.council.domain.alliance.entity.AllianceFile;
import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.dto.res.FileRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AllianceFileRepository extends JpaRepository<AllianceFile, Long> {

    @Query("SELECT new depth.mju.council.domain.notice.dto.res.FileRes(af.id, af.fileName, af.fileUrl) " +
            "FROM AllianceFile af " +
            "WHERE af.alliance.id = :allianceId AND af.fileType = :fileType " +
            "ORDER BY af.createdAt ASC")
    List<FileRes> findAllianceFilesByAllianceIdAndFileType(@Param("allianceId") Long allianceId, @Param("fileType") FileType fileType);

    @Modifying
    @Query("DELETE FROM AllianceFile af WHERE af IN :files")
    void deleteAllianceFiles(@Param("files") List<AllianceFile> files);

    @Modifying
    @Query("UPDATE AllianceFile af SET af.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    List<AllianceFile> findByAlliance(Alliance alliance);
}
