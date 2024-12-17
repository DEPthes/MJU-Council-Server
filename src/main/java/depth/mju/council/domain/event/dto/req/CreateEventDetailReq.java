package depth.mju.council.domain.event.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateEventDetailReq {

    @Schema(type = "String", example = "2024년 명지대학교 축제 세부사항01", description = "행사 세부사항의 제목")
    @NotBlank
    public String title;

    @Schema(type = "String", example = "2024년 명지대학교 축제 세부사항을 안내합니다.", description = "행사 세부사항의 내용")
    @NotBlank
    public String content;
}
