package tableOrder.stores.dto.ResponseDto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import tableOrder.stores.entity.Stores;

public class ResponseStoresDto {


    public static class ResponseStoresList {

    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ResponseStoreData {
        private String businessNo;
        private String storeName;
        private String storeTel;
        private String storeAddress;

        public static ResponseStoreData from(Stores store) {
            return ResponseStoreData.builder()
                    .businessNo(store.getBusinessNo())       // 사업자 번호
                    .storeName(store.getStoreName())         // 매장 이름
                    .storeTel(store.getStoreTel())           // 매장 전화번호
                    .storeAddress(store.getStoreAddress())   // 매장 주소
                    .build();
        }
    }
}




