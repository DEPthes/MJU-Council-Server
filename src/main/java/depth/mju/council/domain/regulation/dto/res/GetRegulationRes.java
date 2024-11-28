package depth.mju.council.domain.regulation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetRegulationRes {
    @Schema(type = "Long", example = "1", description = "회칙아이디")
    private Long regulationId;
    @Schema(type = "String", example = "adsasd123123", description = "회칙url")
    private String imgUrl;
    @Schema(type = "LocalDateTime", example = "2024-12-23", description = "회칙만든시간")
    private LocalDateTime date;
}
