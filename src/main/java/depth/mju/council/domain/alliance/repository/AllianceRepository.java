package depth.mju.council.domain.alliance.repository;

import depth.mju.council.domain.alliance.dto.res.AllianceListRes;
import depth.mju.council.domain.alliance.entity.Alliance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AllianceRepository extends JpaRepository<Alliance, Long> {
    Optional<Alliance> findByIdAndIsDeleted(Long allianceId, boolean b);

    @Modifying
    @Query("UPDATE Alliance a SET a.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    @Query("SELECT new depth.mju.council.domain.alliance.dto.res.AllianceListRes(a.id, a.title, af.fileUrl, a.startDate, a.endDate) " +
            "FROM Alliance a " +
            "LEFT JOIN AllianceFile af ON af.alliance.id = a.id AND af.createdAt = ( " +
            "    SELECT MIN(afInner.createdAt) " +
            "    FROM AllianceFile afInner " +
            "    WHERE afInner.alliance.id = a.id " +
            ") " +
            "WHERE a.isDeleted = :isDeleted")
    Page<AllianceListRes> findAlliancesByIsDeleted(@Param("isDeleted") boolean isDeleted, Pageable pageable);

}
