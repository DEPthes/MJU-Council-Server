package depth.mju.council.domain.event.entity;

import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.common.FileType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "event_detail_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventDetailFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "event_detail_id", nullable = false)
    private EventDetail eventDetail;

    private String fileName;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Builder
    public EventDetailFile(String fileUrl, String fileName, EventDetail eventDetail, FileType fileType) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.eventDetail = eventDetail;
        this.fileType = fileType;
    }
}