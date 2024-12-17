package depth.mju.council.domain.regulation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetRegulationRes {
    @Schema(type = "Long", example = "1", description = "회칙아이디")
    private Long regulationId;
    @Schema(type = "String", example = "뎁스운영진회의", description = "회칙 제목")
    private String title;
    @Schema(type = "String", example = "회의록이다1", description = "회칙 내용")
    private String content;
    @Schema(type = "apiResult", example = "2024-11-17", description = "작성일")
    private LocalDateTime date;
    private List<GetRegulationFileRes> files;  // Regulation 정보를 담는 DTO 리스트
}
