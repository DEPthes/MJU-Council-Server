package depth.mju.council.domain.business.dto.res;

import depth.mju.council.domain.notice.dto.res.FileRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class BusinessRes {

    @Schema(type = "String", example = "2024학년도 교내 운동장 대여", description = "사업의 제목")
    public String title;

    @Schema(type = "String", example = "2024년부터 교내 운동장 대여를 실시합니다.", description = "사업의 내용")
    public String content;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "사업의 작성일자")
    public LocalDate createdAt;

    @Schema(description = "Schemas의 FileRes를 참고해주세요. 이미지의 리스트입니다.")
    public List<FileRes> images;

    @Schema(description = "Schemas의 FileRes를 참고해주세요. 파일의 리스트입니다.")
    public List<FileRes> files;

    @Builder
    public BusinessRes(String title, String content, LocalDate createdAt, List<FileRes> images, List<FileRes> files) {
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.images = images;
        this.files = files;
    }
}
