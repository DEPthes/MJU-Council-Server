package depth.mju.council.domain.orgarnization.entity;

import depth.mju.council.domain.BaseEntity;
import depth.mju.council.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "organization")
public class Organization extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String imgUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}