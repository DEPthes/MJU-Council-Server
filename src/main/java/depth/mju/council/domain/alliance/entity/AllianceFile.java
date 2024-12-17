package depth.mju.council.domain.alliance.entity;

import depth.mju.council.domain.business.entity.Business;
import depth.mju.council.domain.common.BaseEntity;
import depth.mju.council.domain.common.FileType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "alliance_file")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AllianceFile extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "alliance_id", nullable = false)
    private Alliance alliance;

    private String fileName;

    @Enumerated(value = EnumType.STRING)
    private FileType fileType;

    @Builder
    public AllianceFile(String fileUrl, String fileName, Alliance alliance, FileType fileType) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.alliance = alliance;
        this.fileType = fileType;
    }
}