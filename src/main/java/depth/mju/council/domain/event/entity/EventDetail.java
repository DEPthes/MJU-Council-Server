package depth.mju.council.domain.event.entity;

import depth.mju.council.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Entity
@Table(name = "event_detail")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDetail extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Builder
    public EventDetail(String title, String content, Event event) {
        this.title = title;
        this.content = content;
        this.event = event;
    }

    public void updateTitleAndContent(String title, String content) {
        this.title = title;
        this.content = content;
    }
}