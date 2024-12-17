package depth.mju.council.domain.committe.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommitteeRes {

    @Schema(type = "Long", example = "1", description = "중운위(단과대) ID")
    public Long committeeId;

    @Schema(type = "String", example = "중앙운영위원회는 총학생회장, 부총학생회장, 단과대학 학생회장 및 대표자, 총동아리 연합회장 및 대표자로 구성됩니다.",
            description = "중운위 설명. 단과대는 null로 반환")
    public String description;

    @Schema(type = "String", example = "ICT융합대학", description = "단과대명. 중운위는 null로 반환")
    public String college;

    @Schema(type = "String", example = "새솔", description = "학생회명. 중운위는 null로 반환")
    public String name;

    @Schema(type = "String", example = "https://www.mju.ac.kr/sites/ict/index.do", description = "홈페이지 주소. 중운위는 null로 반환")
    public String pageUrl;

    @Schema(type = "String", example = "https://www.instagram.com/mju_ict?utm_source=ig_web_button_share_sheet&igsh=ZDNlZDc0MzIxNw==", description = "인스타 주소. 중운위는 null로 반환")
    public String snsUrl;

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "이미지 주소")
    public String imgUrl;

}
