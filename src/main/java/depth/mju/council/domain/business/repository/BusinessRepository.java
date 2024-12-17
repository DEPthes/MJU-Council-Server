package depth.mju.council.domain.business.repository;

import depth.mju.council.domain.business.dto.res.BusinessListRes;
import depth.mju.council.domain.business.entity.Business;
import depth.mju.council.domain.notice.dto.res.NoticeListRes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    @Modifying
    @Query("UPDATE Business b SET b.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    Optional<Business> findByIdAndIsDeleted(Long businessId, boolean b);

    @Query("SELECT new depth.mju.council.domain.business.dto.res.BusinessListRes(b.id, b.title, b.createdAt) " +
            "FROM Business b " +
            "WHERE b.title LIKE %:keyword% AND b.isDeleted = false")
    Page<BusinessListRes> findByTitleContaining(String keyword, Pageable pageable);

    @Query("SELECT new depth.mju.council.domain.business.dto.res.BusinessListRes(b.id, b.title, b.createdAt) " +
            "FROM Business b " +
            "WHERE b.isDeleted = false")
    Page<BusinessListRes> findAllBusinesses(Pageable pageable);
}
