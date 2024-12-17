package depth.mju.council.domain.committe.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCommitteeReq {

    @Schema(type = "String", example = "중앙운영위원회는 총학생회장, 부총학생회장, 단과대학 학생회장 및 대표자, 총동아리 연합회장 및 대표자로 구성됩니다.",
            description = "중운위 설명. 단과대는 null로 전송해주세요.")
    public String description;

    @Schema(type = "String", example = "ICT융합대학", description = "단과대명. 중운위는 null로 전송해주세요.")
    public String college;

    @Schema(type = "String", example = "새솔", description = "학생회명. 중운위는 null로 전송해주세요.")
    public String name;

    @Schema(type = "String", example = "홈페이지 주소", description = "홈페이지 주소. 중운위는 null로 전송해주세요.")
    public String pageUrl;

    @Schema(type = "String", example = "인스타 주소", description = "인스타 주소. 중운위는 null로 전송해주세요.")
    public String snsUrl;

}
