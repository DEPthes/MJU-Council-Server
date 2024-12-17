package depth.mju.council.domain.committe.entity;

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
@Table(name = "committee")
public class Committee extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    private String description;
    private String college;
    private String name;
    private String pageUrl;
    private String snsUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    public void update(String description, String college, String name, String pageUrl, String snsUrl, String imgUrl) {
        this.description = description;
        this.college = college;
        this.name = name;
        this.pageUrl = pageUrl;
        this.snsUrl = snsUrl;
        this.imgUrl = imgUrl;
    }
}