package depth.mju.council.domain.minute.repository;

import depth.mju.council.domain.minute.entity.Minute;
import depth.mju.council.domain.minute.entity.MinuteFile;
import depth.mju.council.domain.notice.dto.res.FileRes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MinuteFileRepository extends JpaRepository<MinuteFile, Long> {

    List<MinuteFile> findByMinute(Minute minutes);
}
