package tableOrder.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseDto<T> {
    private String code;      // 예: "200", "404"
    private String message;   // 예: "성공", "실패" 등의 설명
    private T data;           // 실제 반환 데이터 (카테고리 정보, 목록 등)
}