package depth.mju.council.domain.committe.repository;

import depth.mju.council.domain.committe.entity.Committee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommitteeRepository extends JpaRepository<Committee, Long> {
}
