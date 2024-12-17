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
public class CouncilRes {

    @Schema(type = "String", example = "45", description = "총학 기수(숫자만)")
    public String generation;
    @Schema(type = "String", example = "새로", description = "총학생회명")
    public String name;
    @Schema(type = "String", example = "saero@gmail.com", description = "총학 이메일")
    public String email;
    @Schema(type = "String", example = "https://www.instagram.com/mju_saero?utm_source=ig_web_button_share_sheet&igsh=ZDNlZDc0MzIxNw==", description = "총학 SNS주소")
    public String snsUrl;
    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189" +
            ".png", description = "로고 이미지 주소")
    public String logoUrl;
}
