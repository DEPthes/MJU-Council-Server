package depth.mju.council.domain.regulation.entity;

import depth.mju.council.domain.regulation.dto.req.ModifyRegulationReq;
import depth.mju.council.domain.user.entity.UserEntity;
import depth.mju.council.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "regulation")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Regulation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    public void update(ModifyRegulationReq modifyRegulationReq) {
        this.title = modifyRegulationReq.getTitle();
        this.content = modifyRegulationReq.getContent();
    }
}