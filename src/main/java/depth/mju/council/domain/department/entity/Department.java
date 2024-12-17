package depth.mju.council.domain.department.entity;

import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "department")
public class Department extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    public void updateDescriptionAndImgUrl(String description, String newImageUrl) {
        this.description = description;
        this.imgUrl = imgUrl;
    }
}
