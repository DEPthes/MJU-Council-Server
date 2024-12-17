package depth.mju.council.domain.event.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public interface EventDetailListRes {
    @Schema(type = "Long", example = "1", description = "행사 세부사항의 id")
    Long getEventDetailId();

    @Schema(type = "String", example = "2024년 명지대학교 축제 세부사항01", description = "행사 세부사항의 제목")
    String getTitle();

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "커버 이미지의 주소")
    String getCover();

    @Schema(type = "LocalDate", example = "2024-11-17", description = "행사 세부사항의 작성일자")
    LocalDate getCreatedAt();
}
