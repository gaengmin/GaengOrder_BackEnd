package tableOrder.tables.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import tableOrder.common.enums.SoftDelete;
import tableOrder.tables.entity.Tables;

public class RequestTablesDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Validated
    public static class addStoreTableDto {
        private Long storeNo;
        private String tableCode;

        public Tables toEntity(Long storeNo) {
            return Tables.builder()
                    .storeNo(storeNo)
                    .tableCode(this.tableCode)
                    .softDelete(SoftDelete.N)
                    .build();
        }
    }
}
