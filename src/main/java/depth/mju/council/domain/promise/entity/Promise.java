package depth.mju.council.domain.promise.entity;

import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.promise.dto.req.ModifyPromiseReq;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@Entity
@Table(name = "promise")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Promise extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Long progress;

    @ManyToOne
    @JoinColumn(name = "promise_category_id", nullable = false)
    private PromiseCategory promiseCategory;

    public void update(ModifyPromiseReq modifyPromiseReq) {
        this.title = modifyPromiseReq.getTitle();
        this.content = modifyPromiseReq.getContent();
        this.progress = modifyPromiseReq.getProgress();
    }
}