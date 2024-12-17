package depth.mju.council.domain.alliance.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class AllianceListRes {
    @Schema(type = "Long", example = "1", description = "제휴의 id")
    public Long allianceId;
    
    @Schema(type = "String", example = "잡플래닛 제휴", description = "제휴의 제목")
    public String title;

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "커버 이미지의 주소")
    public String cover;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "제휴의 시작일자")
    public LocalDate startDate;

    @Schema(type = "LocalDate", example = "2024-11-24", description = "제휴의 종료일자")
    public LocalDate endDate;

    @Builder
    public AllianceListRes(Long allianceId, String title, String cover, LocalDate startDate, LocalDate endDate) {
        this.allianceId = allianceId;
        this.title = title;
        this.cover = cover;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
