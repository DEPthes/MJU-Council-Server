package depth.mju.council.domain.minute.repository;

import depth.mju.council.domain.minute.entity.Minute;
import depth.mju.council.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MinuteRepository extends JpaRepository<Minute,Long> {
    List<Minute> findByUserEntity(UserEntity user);

    Page<Minute> findByTitleContaining(String s, PageRequest pageRequest);
}
