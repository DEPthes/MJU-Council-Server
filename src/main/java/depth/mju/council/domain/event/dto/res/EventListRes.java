package depth.mju.council.domain.event.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public interface EventListRes {
    @Schema(type = "Long", example = "1", description = "행사의 id")
    Long getEventId();

    @Schema(type = "String", example = "2024년 명지대학교 축제", description = "행사의 제목")
    String getTitle();

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "커버 이미지의 주소")
    String getCover();

    @Schema(type = "LocalDate", example = "2024-11-17", description = "행사의 시작일자")
    LocalDate getStartDate();

    @Schema(type = "LocalDate", example = "2024-11-17", description = "행사의 종료일자")
    LocalDate getEndDate();
}
