package depth.mju.council.domain.business.entity;

import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "business")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Business extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Builder
    public Business(String title, String content, UserEntity userEntity) {
        this.title = title;
        this.content = content;
        this.userEntity = userEntity;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }

}