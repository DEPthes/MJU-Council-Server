package depth.mju.council.domain.event.dto.res;

import depth.mju.council.domain.notice.dto.res.FileRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class EventDetailRes {

    @Schema(type = "String", example = "2024년 명지대학교 축제 세부사항01", description = "행사 세부사항의 제목")
    public String title;

    @Schema(type = "String", example = "2024년 명지대학교 축제를 실시합니다.", description = "행사 세부사항의 내용")
    public String content;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "행사 세부사항의 작성일자")
    public LocalDate createdAt;

    @Schema(description = "Schemas의 FileRes를 참고해주세요. 이미지의 리스트입니다.")
    public List<FileRes> images;

    @Builder
    public EventDetailRes(String title, String content, LocalDate createdAt, List<FileRes> images) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.images = images;
    }
}
