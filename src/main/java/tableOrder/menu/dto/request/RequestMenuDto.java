package tableOrder.menu.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
}
