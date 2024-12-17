package depth.mju.council.domain.regulation.repository;

import depth.mju.council.domain.regulation.entity.Regulation;
import depth.mju.council.domain.regulation.entity.RegulationFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegulationFileRepository extends JpaRepository<RegulationFile,Long> {
    List<RegulationFile> findByRegulation(Regulation regulation);
}
