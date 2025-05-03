package tableOrder.stores.mapper;

import org.apache.ibatis.annotations.Mapper;
import tableOrder.stores.dto.RequestDto.RequestStoresDto;
import tableOrder.stores.dto.RequestDto.RequestStoresDto.*;

@Mapper
public interface StoresMapper {
    //사업자 등록
    void insertStores(RequestStoresDto.RequestInsertDto requestInsertDto);
    //사업자 번호 중복체크
    int confirmStores(String businessNo);
}
