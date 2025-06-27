package tableOrder.tables.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tableOrder.orders.dto.enums.OrdersStatusEnum;
import tableOrder.tables.entity.Tables;

public class ResponseTablesDto {

    @Getter
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ResponseTableInfoDto{
        private Long tableNo;
        private String tableCode;
        private Long storeNo;
        private OrdersStatusEnum status;

        public static ResponseTableInfoDto from(Tables table, String status) {
            return ResponseTableInfoDto.builder()
                    .tableCode(table.getTableCode())
                    .tableNo(table.getTableNo())
                    .storeNo(table.getStoreNo())
                    .status(OrdersStatusEnum.valueOf(status))
                    .build();

        }
    }
}
