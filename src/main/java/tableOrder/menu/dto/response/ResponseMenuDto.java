package tableOrder.menu.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tableOrder.common.enums.SoftDelete;

public class ResponseMenuDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MenuValidateDto{
        private Long menuNo;
        private char menuStatus;
        private SoftDelete softDelete;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseMenuDetailDto {
        private Long menuNo;
        private String categoryName;
        private String menuName;
        private Integer menuPrice;
        private String menuDescription;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseMenuDataDto {
        private Long menuNo;
        private String categoryName;
        private String menuName;
        private Integer menuPrice;
    }
}
