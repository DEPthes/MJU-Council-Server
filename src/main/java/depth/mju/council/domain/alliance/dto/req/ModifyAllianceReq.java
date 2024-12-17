package depth.mju.council.domain.alliance.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class ModifyAllianceReq {
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

    @Schema(type = "List<Integer>", example = "[1, 2, 3]", description = "제휴 수정 시 지우고자 하는 이미지의 리스트")
    public List<Integer> deleteImages;

    @Schema(type = "List<Integer>", example = "[1, 2, 3]", description = "제휴 수정 시 지우고자 하는 파일의 리스트")
    public List<Integer> deleteFiles;
}
