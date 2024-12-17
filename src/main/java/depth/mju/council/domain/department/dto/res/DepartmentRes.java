package depth.mju.council.domain.department.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class DepartmentRes {

    @Schema(type = "Long", example = "1", description = "국별 소개 ID")
    public Long departmentId;

    @Schema(type = "String", example = "제휴나 외부 업체와의 소통을 담당합니다.", description = "국별 업무 소개글.")
    public String description;

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "이미지/파일의 주소")
    public String imgUrl;

}
