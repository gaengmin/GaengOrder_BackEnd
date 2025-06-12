package tableOrder.stores.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.bytecode.DuplicateMemberException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tableOrder.auth.util.AbstractAuthValidator;
import tableOrder.auth.util.SecurityUtil;
import tableOrder.category.mapper.CategoriesMapper;
import tableOrder.stores.dto.RequestDto.RequestStoresDto;
import tableOrder.stores.dto.ResponseDto.ResponseStoresDto;
import tableOrder.stores.entity.Stores;
import tableOrder.stores.mapper.StoresMapper;
import tableOrder.stores.repository.StoreRepository;


@Slf4j
@Service
public class StoresService extends AbstractAuthValidator {
    private final StoresMapper storesMapper;
    private final StoreRepository storeRepository;

    public StoresService(CategoriesMapper categoriesMapper, StoresMapper storesMapper, StoreRepository storeRepository) {
        super(categoriesMapper);
        this.storesMapper = storesMapper;
        this.storeRepository = storeRepository;
    }


    /**
     * @param requestInsertDto 등록할 매장 정보(사업자번호, 매장명, 매장연락처)
     * @throws IllegalArgumentException 이미 등록된 사업자번호가 존재할 경우
     * @throws RuntimeException         매장 등록 중 데이터베이스 오류가 발생한 경우
     * @Date 2025-05-03
     * <p>
     * 신규 매장 등록 트랜잭션
     * 사업자번호 중복 검사 후 매장 정보를 DB에 저장합니다.
     * 중복된 사업자번호가 존재하거나 저장 중 오류가 발생할 경우
     * 트랜잭션이 롤백됩니다.
     */
    @Transactional
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public void insertStores(RequestStoresDto.RequestInsertDto requestInsertDto) {
        int cnt = storesMapper.confirmStores(requestInsertDto.getBusinessNo());
        log.info(cnt + " : " + "사업자 갯수");
        if (cnt > 0) {
            throw new IllegalArgumentException("이미 존재하는 사업자 번호");
        }
        try {
            storesMapper.insertStores(requestInsertDto);
        } catch (Exception e) {

            throw new RuntimeException("매장 등록 중 오류 발생");
        }

    }

    //매장 정보 가져오기
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseStoresDto.ResponseStoreData getStoreData() {
        Long userStoreNo = SecurityUtil.getCurrentUsersStoreNo();
        String userId = SecurityUtil.getCurrentUserId();

        //공통으로 권한 체크
        verifyStoreOwner(userStoreNo, userId, "매장 정보 조회");

        Stores store = storeRepository.findById(userStoreNo)
                .orElseThrow(() -> new IllegalArgumentException("해당 매장을 찾을 수 없습니다."));

        return ResponseStoresDto.ResponseStoreData.from(store);
    }
}
