package depth.mju.council.domain.alliance.dto.res;

import depth.mju.council.domain.notice.dto.res.FileRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class AllianceRes {

    @Schema(type = "String", example = "잡플래닛 제휴", description = "제휴의 제목")
    public String title;

    @Schema(type = "String", example = "잡플래닛 제휴를 실시합니다.", description = "제휴의 내용")
    public String content;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "제휴의 시작일자")
    public LocalDate startDate;

    @Schema(type = "LocalDate", example = "2024-11-24", description = "제휴의 종료일자")
    public LocalDate endDate;

    @Schema(description = "Schemas의 FileRes를 참고해주세요. 이미지의 리스트입니다.")
    public List<FileRes> images;

    @Schema(description = "Schemas의 FileRes를 참고해주세요. 파일의 리스트입니다.")
    public List<FileRes> files;


    @Builder
    public AllianceRes(String title, String content, LocalDate startDate, LocalDate endDate, List<FileRes> images, List<FileRes> files) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.images = images;
        this.files = files;
    }
}
