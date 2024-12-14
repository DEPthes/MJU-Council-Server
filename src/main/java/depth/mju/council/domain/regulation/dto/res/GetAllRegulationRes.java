package depth.mju.council.domain.regulation.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetAllRegulationRes {
    @Schema(type = "Long", example = "1", description = "회칙아이디")
    private Long regulationId;
    @Schema(type = "String", example = "뎁스운영진회의", description = "회칙 제목")
    private String title;
    @Schema(type = "LocalDateTime", example = "2024-11-17", description = "작성일")
    private LocalDateTime date;
    @Schema(type = "String", example = "https://council-s3-bucket.s3.amazonaws.com/image/79fdc2c6-02e42b-ffb2fde8b189.png", description = "이미지/파일의 주소")
    private List<String> fileUrls;
}
