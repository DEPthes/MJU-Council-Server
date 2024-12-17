package depth.mju.council.domain.event.entity;

import depth.mju.council.domain.alliance.entity.Alliance;
import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.common.FileType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "event_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    private String fileName;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Builder
    public EventFile(String fileUrl, String fileName, Event event, FileType fileType) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.event = event;
        this.fileType = fileType;
    }
}