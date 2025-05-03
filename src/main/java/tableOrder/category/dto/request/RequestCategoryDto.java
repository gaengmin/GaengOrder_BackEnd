package tableOrder.category.dto.request;

import lombok.*;
import tableOrder.common.enums.SoftDelete;

import java.sql.Timestamp;

public class RequestCategoryDto {
  /*
    private Long categoryId;
    private String storeNo;
    private String name;
    private SoftDelete softDelete;
    private Timestamp createdDt;
    private Timestamp updateDt;
*/
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InsertCategory {
        private String name;
        private String businessNo;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpdateCategory {
        private Long categoryNo;
        private String name;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DeleteCategory {
        private Long categoryNo;
        private SoftDelete softDelete;
    }

}
