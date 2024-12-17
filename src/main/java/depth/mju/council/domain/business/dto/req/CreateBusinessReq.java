package depth.mju.council.domain.business.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateBusinessReq {

    @Schema(type = "String", example = "2024학년도 교내 운동장 대여", description = "사업의 제목")
    @NotBlank
    public String title;

    @Schema(type = "String", example = "2024년부터 교내 운동장 대여를 실시합니다.", description = "사업의 내용")
    @NotBlank
    public String content;
}
