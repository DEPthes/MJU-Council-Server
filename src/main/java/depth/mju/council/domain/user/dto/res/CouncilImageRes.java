package depth.mju.council.domain.user.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CouncilImageRes {

    @Schema(type = "Long", example = "1", description = "소개이미지 ID")
    public Long councilImageId;

    @Schema(type = "String", example = "어떤 이미지에 대한 설명입니다.", description = "소개 이미지 설명.")
    public String description;

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "이미지/파일의 주소")
    public String imgUrl;

}
