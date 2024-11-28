package depth.mju.council.domain.minute.entity;

import depth.mju.council.domain.minute.dto.req.ModifyMinuteReq;
import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "minute")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Minute extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    public void update(ModifyMinuteReq modifyMinuteReq) {
        this.title = modifyMinuteReq.getTitle();
        this.content = modifyMinuteReq.getContent();
    }
}