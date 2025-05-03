package tableOrder.stores.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.DuplicateMemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.stores.dto.RequestDto.RequestStoresDto;
import tableOrder.stores.mapper.StoresMapper;


@Slf4j
@Service
@RequiredArgsConstructor
public class StoresService {

    private final StoresMapper storesMapper;

    /**
     * @Date 2025-05-03
     *
     * 신규 매장 등록 트랜잭션
     * 사업자번호 중복 검사 후 매장 정보를 DB에 저장합니다.
     * 중복된 사업자번호가 존재하거나 저장 중 오류가 발생할 경우
     * 트랜잭션이 롤백됩니다.
     *
     *
     * @param requestInsertDto 등록할 매장 정보(사업자번호, 매장명, 매장연락처)
     * @throws IllegalArgumentException 이미 등록된 사업자번호가 존재할 경우
     * @throws RuntimeException 매장 등록 중 데이터베이스 오류가 발생한 경우
     */
    @Transactional
    public void insertStores(RequestStoresDto.RequestInsertDto requestInsertDto) {
        int cnt = storesMapper.confirmStores(requestInsertDto.getBusinessNo());
        log.info(cnt +  " : "+ "사업자 갯수");
        if(cnt > 0) {
            throw new IllegalArgumentException("이미 존재하는 사업자 번호");
        }
        try{
            storesMapper.insertStores(requestInsertDto);
        }catch (Exception e) {

            throw new RuntimeException("매장 등록 중 오류 발생");
        }

    }
}
