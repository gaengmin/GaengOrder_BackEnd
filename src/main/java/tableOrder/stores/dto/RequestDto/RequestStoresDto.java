package tableOrder.stores.dto.RequestDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class RequestStoresDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class RequestInsertDto {
        private String businessNo; //사업자 번호
        private String storeName; // 매장 이름
        private String storeTel; // 매장연락처
    }

}
