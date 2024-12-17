package depth.mju.council.domain.business.entity;

import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.common.FileType;
import depth.mju.council.domain.notice.entity.Notice;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "business_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BusinessFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Builder
    public BusinessFile(String fileUrl, String fileName, Business business, FileType fileType) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.business = business;
        this.fileType = fileType;
    }
}