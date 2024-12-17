package depth.mju.council.domain.banner.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetBannerRes {
    @Schema(type = "Long", example = "1", description = "배너아이디")
    private Long bannerId;
    @Schema(type = "String", example = "adsasd123123", description = "배너url")
    private String imgUrl;
    @Schema(type = "LocalDateTime", example = "2024-12-23", description = "배너만든시간")
    private LocalDateTime date;

}
