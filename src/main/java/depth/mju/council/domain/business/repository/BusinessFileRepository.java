package depth.mju.council.domain.business.repository;

import depth.mju.council.domain.alliance.entity.AllianceFile;
import depth.mju.council.domain.business.entity.Business;
import depth.mju.council.domain.business.entity.BusinessFile;
import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.dto.res.FileRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusinessFileRepository extends JpaRepository<BusinessFile, Long> {
    @Modifying
    @Query("UPDATE BusinessFile bf SET bf.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    @Query("SELECT new depth.mju.council.domain.notice.dto.res.FileRes(bf.id, bf.fileName, bf.fileUrl) " +
            "FROM BusinessFile bf " +
            "WHERE bf.business.id = :businessId AND bf.fileType = :fileType " +
            "ORDER BY bf.createdAt ASC")
    List<FileRes> findBusinessFilesByBusinessIdAndFileType(@Param("businessId") Long businessId, @Param("fileType") FileType fileType);

    List<BusinessFile> findByBusiness(Business business);

    @Modifying
    @Query("DELETE FROM BusinessFile bf WHERE bf IN :files")
    void deleteBusinessFiles(@Param("files") List<BusinessFile> files);
}
