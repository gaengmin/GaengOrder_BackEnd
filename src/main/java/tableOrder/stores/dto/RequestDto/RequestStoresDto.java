package tableOrder.stores.dto.RequestDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
        @NotBlank(message = "사업자 번호 필수")
        private String businessNo; //사업자 번호
        @NotBlank(message = "매장 이름 필수")
        private String storeName; // 매장 이름
        @NotBlank(message = "매장 주소 필수")
        private String storeAddress;
        @NotBlank(message = "연락처 필수")
        @Size(min = 8, max = 14)
        private String storeTel; // 매장연락처
    }

}
