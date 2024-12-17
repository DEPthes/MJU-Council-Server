package depth.mju.council.domain.event.repository;

import depth.mju.council.domain.event.dto.res.EventListRes;
import depth.mju.council.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Modifying
    @Query("UPDATE Event e SET e.isDeleted = :isDeleted")
    void updateIsDeletedForAll(@Param("isDeleted") boolean isDeleted);

    @Query(value = "SELECT e.id AS eventId, e.title AS title, " +
            "(SELECT ef.file_url FROM event_file ef WHERE ef.event_id = e.id ORDER BY ef.created_at ASC LIMIT 1) AS cover, " +
            "e.start_date AS startDate, e.end_date AS endDate " +
            "FROM event e " +
            "WHERE e.is_deleted = :isDeleted " +
            "ORDER BY e.created_at DESC", nativeQuery = true)
    List<EventListRes> findEventsByIsDeleted(@Param("isDeleted") boolean isDeleted);


    Optional<Event> findByIdAndIsDeleted(Long eventId, boolean b);
}
