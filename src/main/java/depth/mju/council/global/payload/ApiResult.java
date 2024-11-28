package depth.mju.council.global.payload;

import lombok.Builder;
import lombok.Data;

@Data
public class ApiResult {

    //올바르게 로직을 처리했으면 True, 아니면 False를 반환합니다.
    private boolean check;


    //정보를 object 형식으로 감싸서 표현합니다.
    private Object information;

    private String message;

    public ApiResult(){};

    @Builder
    public ApiResult(boolean check, Object information, String message) {
        this.check = check;
        this.information = information;
        this.message = message;
    }
}