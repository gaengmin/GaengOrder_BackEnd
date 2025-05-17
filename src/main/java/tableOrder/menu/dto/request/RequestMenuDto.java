package tableOrder.menu.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.validation.annotation.Validated;

public class RequestMenuDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddMenuDto{
        @NotNull(message = "카테고리 번호는 필수")
        private Long categoriesNo;
        @NotBlank(message = "메뉴명은 필수입니다.")
        private String menuName;
        @NotNull(message = "메뉴 가격은 필수입니다.")
        @Positive(message = "메뉴 가격은 0보다 커야 합니다.")
        private Integer menuPrice;
        private String description;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateMenuDto {
        private Long categoriesNo;    // null이면 변경 없음
        private String menuName;      // null이면 변경 없음
        private Integer menuPrice;    // null이면 변경 없음
        private String description;   // null이면 변경 없음
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionMenuDto {
        private Long menuNo;
        private int position;
    }

}
