package depth.mju.council.domain.minute.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ModifyMinuteReq {
    @Schema(type = "String", example = "뎁스 운영진 회의", description="회의 제목")
    private String title;
    @Schema(type = "String", example = "회의했어욥", description="회의 내용")
    private String content;
    @Schema(type = "List<Integer>", example = "[1, 2, 3]", description = "회의 수정 시 지우고자 하는 파일의 리스트")
    public List<Integer> deleteFiles;
}
