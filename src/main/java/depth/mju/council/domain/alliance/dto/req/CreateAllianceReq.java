package depth.mju.council.domain.alliance.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;


@Getter
public class CreateAllianceReq {

    @Schema(type = "String", example = "잡플래닛 제휴", description = "제휴의 제목")
    @NotBlank
    public String title;

    @Schema(type = "String", example = "잡플래닛 제휴를 실시합니다.", description = "제휴의 내용")
    @NotBlank
    public String content;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "제휴의 시작일자")
    @NotNull
    public LocalDate startDate;

    @Schema(type = "LocalDate", example = "2024-11-24", description = "제휴의 종료일자")
    @NotNull
    public LocalDate endDate;
}
