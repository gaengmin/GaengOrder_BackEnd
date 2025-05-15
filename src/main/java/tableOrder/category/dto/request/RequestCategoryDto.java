package tableOrder.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import tableOrder.common.enums.SoftDelete;


public class RequestCategoryDto {
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsertCategory {
        @NotBlank(message = "카테고리 이름은 필수")
        private String name;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateCategory {
        @NotBlank(message = "카테고리 이름은 필수")
        private String name;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SoftDeleteCategory {
        @NotBlank(message = "softDelete 데이터")
        private SoftDelete softDelete;
    }

}
