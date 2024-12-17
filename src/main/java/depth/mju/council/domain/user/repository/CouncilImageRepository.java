package depth.mju.council.domain.user.repository;

import depth.mju.council.domain.user.entity.CouncilImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouncilImageRepository extends JpaRepository<CouncilImage, Long> {
}
