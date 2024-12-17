package depth.mju.council.domain.event.repository;

import depth.mju.council.domain.event.dto.res.EventDetailListRes;
import depth.mju.council.domain.event.entity.Event;
import depth.mju.council.domain.event.entity.EventDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventDetailRepository extends JpaRepository<EventDetail, Long> {
    @Modifying
    @Query("UPDATE EventDetail ed SET ed.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    List<EventDetail> findByEventId(Long eventId);

    @Query(value = "SELECT e.id AS eventDetailId, e.title AS title, " +
            "(SELECT edf.file_url FROM event_detail_file edf WHERE edf.event_detail_id = e.id ORDER BY edf.created_at ASC LIMIT 1) AS cover, " +
            "e.created_at AS createdAt " +
            "FROM event_detail e " +
            "WHERE e.event_id = :eventId AND e.is_deleted = :isDeleted " +
            "ORDER BY e.created_at DESC", nativeQuery = true)
    List<EventDetailListRes> findEventDetailsByEventId(@Param("eventId") Long eventId, @Param("isDeleted") boolean isDeleted);

    Optional<EventDetail> findByIdAndIsDeleted(Long eventDetailId, boolean b);

    @Modifying
    @Query("DELETE FROM EventDetail ed WHERE ed.event = :event")
    void deleteEventDetailsByEvent(@Param("event") Event event);
}
