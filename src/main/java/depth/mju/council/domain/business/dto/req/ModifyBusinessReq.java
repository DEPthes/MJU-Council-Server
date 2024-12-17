package depth.mju.council.domain.business.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class ModifyBusinessReq {

    @Schema(type = "String", example = "2024학년도 교내 운동장 대여", description = "사업의 제목")
    @NotBlank
    public String title;

    @Schema(type = "String", example = "2024년부터 교내 운동장 대여를 실시합니다.", description = "사업의 내용")
    @NotBlank
    public String content;

    @Schema(type = "List<Integer>", example = "[1, 2, 3]", description = "사업 수정 시 지우고자 하는 이미지의 리스트")
    public List<Integer> deleteImages;

    @Schema(type = "List<Integer>", example = "[1, 2, 3]", description = "사업 수정 시 지우고자 하는 파일의 리스트")
    public List<Integer> deleteFiles;
}
