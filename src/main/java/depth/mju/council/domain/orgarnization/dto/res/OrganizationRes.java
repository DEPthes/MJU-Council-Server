package depth.mju.council.domain.orgarnization.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class OrganizationRes {

    @Schema(type = "Long", example = "1", description = "조직도 ID")
    public Long organizationId;

    @Schema(type = "String", example = "대외협력국", description = "국명")
    public String title;

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "이미지/파일의 주소")
    public String imgUrl;

}
