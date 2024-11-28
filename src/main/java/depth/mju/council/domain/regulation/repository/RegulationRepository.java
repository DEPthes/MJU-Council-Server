package depth.mju.council.domain.regulation.repository;

import depth.mju.council.domain.regulation.entity.Regulation;
import depth.mju.council.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegulationRepository extends JpaRepository<Regulation,Long> {
    List<Regulation> findByUserEntity(UserEntity userEntity);

    Page<Regulation> findByTitleContaining(String s, PageRequest pageRequest);
}
