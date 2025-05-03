package tableOrder.category.dto.response;

import lombok.*;
import tableOrder.common.enums.SoftDelete;

import java.sql.Timestamp;


public class ResponseCategoryDto {
 /*   private Long categoryId;
    private String storeNo;
    private String name;
    private SoftDelete softDelete;
    private Timestamp createdDt;
    private Timestamp updateDt;*/

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseFindListCategoryDto{
        private Long categoryNo;
        private String storeNo;
        private String name;
        private Timestamp updateDt;
    }

    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseOneFindCategoryDto{
        private Long categoryNo;
        private String storeNo;
        private String name;
        private Timestamp updateDt;
    }

}