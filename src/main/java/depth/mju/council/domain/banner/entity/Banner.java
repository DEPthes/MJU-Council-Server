package depth.mju.council.domain.banner.entity;


import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "banner")
public class Banner extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    public void updateImgUrl(String 새로운_이미지_url) {
    }
}