package depth.mju.council.domain.minute.dto.res;

import depth.mju.council.domain.notice.dto.res.FileRes;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class GetMinuteRes {
    @Schema(type = "Long", example = "1", description = "회의록 ID")
    private Long minuteId;

//    @Schema(type = "String", example = "관리자", description = "작성자")
//    private String writer;

    @Schema(type = "String", example = "뎁스운영진회의", description = "회의록 제목")
    private String title;
    @Schema(type = "String", example = "회의록이다1", description = "회의록 내용")
    private String content;
    @Schema(type = "apiResult", example = "2024-11-17", description = "작성일")
    private LocalDateTime date;
    private List<GetMinuteFileRes> files;  // MinuteFile 정보를 담는 DTO 리스트
}
