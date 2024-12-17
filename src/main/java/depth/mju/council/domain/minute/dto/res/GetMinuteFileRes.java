package depth.mju.council.domain.minute.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetMinuteFileRes {
    @Schema(type = "Long", example = "1", description = "이미지/파일의 id")
    public Long minuteFileId;

    @Schema(type = "String", example = "공지사항001.png", description = "이미지/파일의 이름")
    public String fileName;

    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "이미지/파일의 주소")
    public String fileUrl;
}
