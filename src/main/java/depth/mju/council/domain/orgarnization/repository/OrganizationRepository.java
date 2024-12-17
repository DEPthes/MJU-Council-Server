package depth.mju.council.domain.orgarnization.repository;

import depth.mju.council.domain.orgarnization.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}
