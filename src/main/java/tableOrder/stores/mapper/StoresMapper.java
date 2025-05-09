package tableOrder.stores.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import tableOrder.stores.dto.RequestDto.RequestStoresDto;
import tableOrder.stores.dto.RequestDto.RequestStoresDto.*;

@Mapper
public interface StoresMapper {
    //storeNO 조회 : 사업자번호로
    @Select("select store_no from stores where business_no = #{businessNo}")
    Long findByStoreNo(String businessNo);
    //사업자 등록
    void insertStores(RequestStoresDto.RequestInsertDto requestInsertDto);
    //사업자 번호 중복체크
    int confirmStores(String businessNo);
}
