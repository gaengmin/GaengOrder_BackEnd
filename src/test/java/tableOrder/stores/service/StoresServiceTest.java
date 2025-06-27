/*
package tableOrder.stores.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import tableOrder.stores.dto.RequestDto.RequestStoresDto;
import tableOrder.stores.mapper.StoresMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@MybatisTest
class StoresServiceTest {

    @Autowired
    private StoresMapper storesMapper;

    @DisplayName("Mapper테스트")
    @Test
    void insertStores() {
        //given
        RequestStoresDto.RequestInsertDto dto = RequestStoresDto.RequestInsertDto.builder()
                .businessNo("123-45-67890")
                .storeName("테스트매장")
                .storeTel("02-1234-5678")
                .build();

        //when
        storesMapper.insertStores(dto);

        //then
        int cnt = storesMapper.confirmStores(dto.getBusinessNo());
        assertThat(cnt).isEqualTo(1); // 정상적으로 insert 되었는지 확인
    }
}*/
