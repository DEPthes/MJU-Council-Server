package depth.mju.council.domain.user.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateCouncilReq {

    @NotBlank(message = "기수(숫자만)을 입력해주세요")
    @Size(min = 1, message = "기수는 1자 이상이어야 합니다")
    @Schema(type = "String", example = "45", description = "총학 기수(숫자만)")
    private String generation;

    @NotBlank(message = "총학생회명을 입력해주세요")
    @Size(min = 1, message = "총학생회명은 1자 이상이어야 합니다")
    @Schema(type = "String", example = "새로", description = "총학생회명")
    private String name;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email
    @Schema(type = "String", example = "saero@gmail.com", description = "총학 이메일")
    public String email;

    @Nullable
    @Schema(type = "String", example = "https://www.instagram.com/mju_saero?utm_source=ig_web_button_share_sheet&igsh=ZDNlZDc0MzIxNw==", description = "총학 SNS주소")
    public String snsUrl;

}
