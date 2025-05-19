package tableOrder.category.dto.response;

import lombok.*;



public class ResponseCategoryDto {

    @Data
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponseListMenuDto{
        private Long menuNo;
        private String categoryName;
        private String menuName;
        private Integer menuPrice;
        private Character menuStatus;
    }




}