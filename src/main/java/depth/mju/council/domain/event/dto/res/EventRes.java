package depth.mju.council.domain.event.dto.res;

import depth.mju.council.domain.notice.dto.res.FileRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class EventRes {

    @Schema(type = "String", example = "2024년 명지대학교 축제", description = "행사의 제목")
    public String title;

    @Schema(type = "String", example = "2024년 명지대학교 축제를 실시합니다.", description = "행사의 내용")
    public String content;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "행사의 시작일자")
    public LocalDate startDate;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "행사의 종료일자")
    public LocalDate endDate;

    @Schema(description = "Schemas의 FileRes를 참고해주세요. 이미지의 리스트입니다.")
    public List<FileRes> images;

    @Schema(description = "Schemas의 EventDetailListRes를 참고해주세요. 행사 세부사항 목록의 리스트입니다.")
    public List<EventDetailListRes> eventDetails;
    @Builder
    public EventRes(String title, String content, LocalDate startDate, LocalDate endDate, List<FileRes> images, List<EventDetailListRes> eventDetails) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.images = images;
        this.eventDetails = eventDetails;
    }
}
