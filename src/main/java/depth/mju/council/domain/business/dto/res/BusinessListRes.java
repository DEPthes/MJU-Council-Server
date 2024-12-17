package depth.mju.council.domain.business.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class BusinessListRes {

    @Schema(type = "Long", example = "1", description = "사업의 id")
    public Long businessId;

    @Schema(type = "String", example = "2024학년도 교내 운동장 대여", description = "사업의 제목")
    public String title;

    @Schema(type = "LocalDate", example = "2024-11-17", description = "사업의 작성일자")
    public LocalDate createdAt;

    @Builder
    public BusinessListRes(Long businessId, String title, LocalDateTime createdAt) {
        this.businessId = businessId;
        this.title = title;
        this.createdAt = createdAt.toLocalDate();
    }
}
